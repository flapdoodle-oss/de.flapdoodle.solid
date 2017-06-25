package com.mitchellbosecke.pebble.attributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.types.TypeConverter;

public class MethodResolver implements AttributeResolver {

  private static final Object[] NO_ARGS = new Object[0];
  
  private final ConcurrentHashMap<MethodCacheKey, MethodCalls> memberCache = new ConcurrentHashMap<>(100, 0.9f, 1);

  public MethodResolver() {
  }

	@Override
	public Optional<ResolvedAttribute> resolve(Object instance, Object attribute, Object[] argumentValues, boolean isStrictVariables, String filename,
			int lineNumber) throws PebbleException {
		if (instance!=null) {
	    try {
				return resolveMethodCall(instance, String.valueOf(attribute), argumentValues != null ? argumentValues : NO_ARGS);
		  } catch (RuntimeException rx) {
		      throw new RuntimeException("error on "+instance+"."+attribute+" ("+filename+":"+lineNumber+")", rx);
		  }
		}
		return Optional.empty();
	}

	private Optional<ResolvedAttribute> resolveMethodCall(Object instance, String attributeName, Object[] argumentValues) {
		MethodCacheKey key = MethodCacheKey.of(instance.getClass(), attributeName, argumentValues.length);
		return memberCache.computeIfAbsent(key, k -> methodCallsOf(k))
				.resolve(instance, attributeName, argumentValues);
	}

	
	private MethodCalls methodCallsOf(MethodCacheKey key) {
		List<Method> matchingMethods = Stream.of(key.clazz.getMethods())
			.filter(m -> m.getParameterTypes().length == key.arguments)
			.filter(m -> methodNameMatches(m.getName(), key.name))
			.sorted((a,b) -> a.getName().compareTo(b.getName()))
			.sorted((a,b) -> a.getReturnType().getName().compareTo(b.getReturnType().getName()))
			.collect(Collectors.toList());
		
		return new MethodCalls(key, matchingMethods);
	}

	private static class MethodCalls {

		private final MethodCacheKey key;
		private final List<Method> matchingMethods;

		public MethodCalls(MethodCacheKey key, List<Method> matchingMethods) {
			this.key = key;
			this.matchingMethods = matchingMethods;
			this.matchingMethods.forEach(m -> m.setAccessible(true));
		}

		public Optional<ResolvedAttribute> resolve(Object instance, String attributeName, Object[] argumentValues) {
			return matchingMethods.stream()
				.filter(m -> matchingArgumentTypes(m.getParameterTypes(), argumentValues))
				.findFirst()
				.map(m -> resolveMethodCall(m,instance,argumentValues));
		}

	}

	private static ResolvedAttribute resolveMethodCall(Method m, Object instance, Object[] argumentValues) {
		return () -> {
			try {
				return m.invoke(instance, convertAll(m.getParameterTypes(), argumentValues));
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("call "+m+" on "+instance+" with "+Arrays.asList(argumentValues), e);
			}
		};
	}

	private static Object[] convertAll(Class<?>[] parameterTypes, Object[] argumentValues) {
		Object[] ret = new Object[parameterTypes.length];
		for (int i=0;i<ret.length;i++) {
			Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(parameterTypes[i], argumentValues[i]);
			if (!converted.isPresent()) {
				throw new IllegalArgumentException("could not convert "+ argumentValues[i]+" to "+parameterTypes[i]);
			}
			ret[i]=converted.get().value();
		}
		return ret;
	}

	private static boolean matchingArgumentTypes(Class<?>[] parameterTypes, Object[] argumentValues) {
		if (parameterTypes.length!=argumentValues.length) {
			throw new IllegalArgumentException("arg lenth mismatch: "+parameterTypes.length+"!="+argumentValues.length);
		}
		
		for (int i=0;i<parameterTypes.length;i++) {
			Optional<TypeConverter.Converted<?>> converted = (Optional) TypeConverter.convertTo(parameterTypes[i], argumentValues[i]);
			if (!converted.isPresent()) {
				return false;
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
}
