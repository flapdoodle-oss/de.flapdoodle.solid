package de.flapdoodle.solid.theme.pebble2;

import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.types.TypeConverter;
import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.attributes.ResolvedAttribute;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodResolver implements AttributeResolver {

  private static final Object[] NO_ARGS = new Object[0];
  
  private final ConcurrentHashMap<MethodCacheKey, MethodCalls> memberCache = new ConcurrentHashMap<>(100, 0.9f, 1);

  public MethodResolver() {
  }

	@Override
	public ResolvedAttribute resolve(Object instance, Object attribute, Object[] argumentValues, ArgumentsNode args, EvaluationContextImpl context,
		String filename, int lineNumber) {
		if (instance!=null) {
	    try {
				return resolveMethodCall(instance, String.valueOf(attribute), argumentValues != null ? argumentValues : NO_ARGS);
		  } catch (RuntimeException rx) {
		      throw new RuntimeException("error on "+instance+"."+attribute+" ("+filename+":"+lineNumber+")", rx);
		  }
		}
		return null;
	}

	private ResolvedAttribute resolveMethodCall(Object instance, String attributeName, Object[] argumentValues) {
		MethodCacheKey key = MethodCacheKey.of(instance.getClass(), attributeName, argumentValues.length);
		return memberCache.computeIfAbsent(key, k -> methodCallsOf(k))
				.resolve(instance, attributeName, argumentValues);
	}

	
	private MethodCalls methodCallsOf(MethodCacheKey key) {
		List<Method> matchingMethods = Stream.of(key.clazz.getMethods())
			.filter(m -> parameterLengthMatches(m, key))
			.filter(m -> methodNameMatches(m.getName(), key.name))
			.sorted((a,b) -> a.getName().compareTo(b.getName()))
			.sorted((a,b) -> a.getReturnType().getName().compareTo(b.getReturnType().getName()))
			.collect(Collectors.toList());
		
		return new MethodCalls(key, matchingMethods);
	}

	private static boolean parameterLengthMatches(Method m, MethodCacheKey key) {
		return m.isVarArgs() || m.getParameterTypes().length == key.arguments;
	}

	private static class MethodCalls {

		private final MethodCacheKey key;
		private final List<Method> matchingMethods;

		public MethodCalls(MethodCacheKey key, List<Method> matchingMethods) {
			this.key = key;
			this.matchingMethods = matchingMethods;
			this.matchingMethods.forEach(m -> m.setAccessible(true));
		}

		public ResolvedAttribute resolve(Object instance, String attributeName, Object[] argumentValues) {
			return matchingMethods.stream()
				.filter(m -> matchingArgumentTypes(m.getParameterTypes(), m.isVarArgs(), argumentValues))
				.findFirst()
				.map(m -> resolveMethodCall(m,instance,argumentValues))
				.orElse(null);
		}

	}

	private static ResolvedAttribute resolveMethodCall(Method m, Object instance, Object[] argumentValues) {
			try {
				return new ResolvedAttribute(m.invoke(instance, convertAll(m.getParameterTypes(), m.isVarArgs(), argumentValues)));
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("call "+m+" on "+shortDescription(instance)+" with "+shortenedArgumentValues(argumentValues), e);
			}
	}
	
	private static String shortenedArgumentValues(Object[] argumentValues) {
		return "["+Arrays.asList(argumentValues).stream()
			.map(MethodResolver::shortDescription)
			.collect(Collectors.joining(" ,"))+"]";
	}

	private static String shortDescription(Object s) {
		String sval=s!=null ? s.toString() : "null";
		if (sval.length()>64) {
			sval=sval.substring(0, 64)+"...";
		}
		return sval;
	}

	private static Object[] convertAll(Class<?>[] parameterTypes, boolean varargs, Object[] argumentValues) {
		Object[] ret = new Object[parameterTypes.length];
		if (varargs) {
			int varargTypeIndex = parameterTypes.length-1;
			for (int i=0;i<varargTypeIndex;i++) {
				Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(parameterTypes[i], argumentValues[i]);
				if (!converted.isPresent()) {
					throw new IllegalArgumentException("could not convert "+ argumentValues[i]+" to "+parameterTypes[i]);
				}
				ret[i]=converted.get().value();
			}
			Class<?> varargType = parameterTypes[varargTypeIndex].getComponentType();
			Object varargArray = Array.newInstance(varargType, argumentValues.length-varargTypeIndex);
			ret[varargTypeIndex] = varargArray;
			for (int v=varargTypeIndex;v<argumentValues.length;v++) {
				Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(varargType, argumentValues[v]);
				if (!converted.isPresent()) {
					throw new IllegalArgumentException("could not convert "+ argumentValues[v]+" to "+varargType);
				}
				Array.set(varargArray, v-varargTypeIndex,converted.get().value());
			}
		} else {
			for (int i=0;i<ret.length;i++) {
				Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(parameterTypes[i], argumentValues[i]);
				if (!converted.isPresent()) {
					throw new IllegalArgumentException("could not convert "+ argumentValues[i]+" to "+parameterTypes[i]);
				}
				ret[i]=converted.get().value();
			}
		}
		return ret;
	}

	private static boolean matchingArgumentTypes(Class<?>[] parameterTypes, boolean varargs, Object[] argumentValues) {
		if (parameterTypes.length!=argumentValues.length && !varargs) {
			throw new IllegalArgumentException("arg length mismatch: "+parameterTypes.length+"!="+argumentValues.length);
		}
		
		if (varargs) {
			int varargTypeIndex = parameterTypes.length-1;
			
			for (int i=0;i<varargTypeIndex;i++) {
				Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(parameterTypes[i], argumentValues[i]);
				if (!converted.isPresent()) {
					return false;
				}
			}
			Class<?> varargType = parameterTypes[varargTypeIndex].getComponentType();
			for (int v=varargTypeIndex;v<argumentValues.length;v++) {
				Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(varargType, argumentValues[v]);
				if (!converted.isPresent()) {
					return false;
				}
			}
		} else {
			for (int i=0;i<parameterTypes.length;i++) {
				Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(parameterTypes[i], argumentValues[i]);
				if (!converted.isPresent()) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean methodNameMatches(String methodName, String propertyName) {
		if (propertyName.equals(methodName)) {
			return true;
		}
		String attributeCapitalized = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
		if (methodName.equals("get"+attributeCapitalized)) {
			return true;
		}
		if (methodName.equals("is"+attributeCapitalized)) {
			return true;
		}
		if (methodName.equals("has"+attributeCapitalized)) {
			return true;
		}
		return false;
	}

	private static class MethodCacheKey {

		private final Class<? extends Object> clazz;
		private final String name;
		private final int arguments;

		public MethodCacheKey(Class<? extends Object> clazz, String name, int arguments) {
			this.clazz = clazz;
			this.name = name;
			this.arguments = arguments;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + arguments;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodCacheKey other = (MethodCacheKey) obj;
			if (arguments != other.arguments)
				return false;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public static MethodCacheKey of(Class<? extends Object> clazz, String name, int arguments) {
			return new MethodCacheKey(clazz, name, arguments);
		}
		
	}

	public Extension asExtension() {
		return new AbstractExtension() {
			@Override
			public List<AttributeResolver> getAttributeResolver() {
				return ImmutableList.of(MethodResolver.this);
			}
		};
	}
}
