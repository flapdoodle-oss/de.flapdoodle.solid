package com.mitchellbosecke.pebble.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mitchellbosecke.pebble.utils.Pair;

public class TypeConverter {

	private static final List<TypeMapping<?, ?>> TYPE_MAPPING = asList(
			TypeMapping.of(Long.class, Integer.class, TypeConverter::longAsInt),
			TypeMapping.of(Integer.class, Short.class, TypeConverter::intAsShort),
			TypeMapping.of(Short.class, Byte.class, TypeConverter::shortAsByte),
			TypeMapping.of(Integer.class, Long.class, TypeConverter::intAsLong),
			TypeMapping.of(Short.class, Integer.class, TypeConverter::shortAsInt),
			TypeMapping.of(Byte.class, Short.class, TypeConverter::byteAsShort)
			);
	
	private static final Map<Pair<Class<?>, Class<?>>, TypeMapping<?, ?>> TYPE_MAPPING_MAP=Collections.unmodifiableMap(TYPE_MAPPING.stream()
			.collect(Collectors.toMap(t -> Pair.of(t.src,t.dst), t -> t)));

	private static final List<List<Class<?>>> TYPECAST_PATHS = asList(
			asList(Long.class, Integer.class, Short.class, Byte.class),
			asList(Byte.class, Short.class, Integer.class, Long.class),
			asList(Double.class, Float.class),
			asList(Float.class, Double.class),
			asList(String.class, Character.class),
			asList(Character.class, String.class)
			);
	
	private static final Map<Pair<Class<?>, Class<?>>, List<Class<?>>> TYPECAST_PATHS_MAP=typeCastPathMapOf(TYPECAST_PATHS);

	public static <T> Optional<Converted<T>> convertTo(Class<T> type, Object instance) {
		if (instance == null) {
			return (type.isPrimitive())
					? notConverted()
					: converted((T) instance);

		}
		return castTo(asNonPrimitive(type),instance);
	}

	private static Map<Pair<Class<?>, Class<?>>, List<Class<?>>> typeCastPathMapOf(List<List<Class<?>>> typecastPaths) {
		Map<Pair<Class<?>, Class<?>>, List<Class<?>>> ret=new LinkedHashMap<>();
		for (List<Class<?>> path : typecastPaths) {
			for (int start=0;(start+1)<path.size();start++) {
				Class<?> startType=path.get(start);
				for (int end=start+1;end<path.size();end++) {
					Class<?> endType=path.get(end);
					ret.put(Pair.of(startType, endType), Collections.unmodifiableList(path.subList(start, end+1)));
				}
			}
		}
		return Collections.unmodifiableMap(ret);
	}

	private static <T> Optional<Converted<T>> castTo(Class<T> type, Object instance) {
		Optional<List<Class<?>>> matchingCastPath = Optional.ofNullable(TYPECAST_PATHS_MAP.get(Pair.of(instance.getClass(), type)));
		
		return matchingCastPath.flatMap(path -> castTo(type, instance, path));
	}

	private static <T> Optional<Converted<T>> castTo(Class<T> type, Object instance, List<Class<?>> path) {
		Object current=instance;
		
		for (Class<?> newType : path.subList(1, path.size())) {
			if (current!=null) {
				Object starting=current;
				TypeMapping typeMapping = TYPE_MAPPING_MAP.get(Pair.of(current.getClass(), newType));
				if (typeMapping==null) {
					throw new IllegalArgumentException("not type mapping for "+current.getClass()+" -> "+newType);
				}
				current = typeMapping.map(starting).orElse(null);
			} else {
				break;
			}
		}
		
		return Optional.ofNullable((T) current)
				.map(Converted::new);
	}

	private static <T> Class<T> asNonPrimitive(Class<T> clazz) {
		if (clazz == boolean.class)
			return (Class<T>) Boolean.class;
		if (clazz == char.class)
			return (Class<T>) Character.class;
		if (clazz == byte.class)
			return (Class<T>) Byte.class;
		if (clazz == short.class)
			return (Class<T>) Short.class;
		if (clazz == int.class)
			return (Class<T>) Integer.class;
		if (clazz == long.class)
			return (Class<T>) Long.class;
		if (clazz == float.class)
			return (Class<T>) Float.class;
		if (clazz == double.class)
			return (Class<T>) Double.class;
		if (clazz == void.class)
			return (Class<T>) Void.class;
		return clazz;
	}

	private static <T> Optional<Converted<T>> notConverted() {
		return Optional.empty();
	}

	private static <T> Optional<Converted<T>> converted(T value) {
		return Optional.of(new Converted<T>(value));
	}

	public static class Converted<T> {

		private final T value;

		public Converted(T value) {
			this.value = value;
		}

		public T value() {
			return value;
		}
	}

	private static class TypeMapping<S, D> {

		private final Class<S> src;
		private final Class<D> dst;
		private final Function<S, Optional<D>> mapper;

		public TypeMapping(Class<S> src, Class<D> dst, Function<S, Optional<D>> mapper) {
			this.src = src;
			this.dst = dst;
			this.mapper = mapper;
		}
		
		public Optional<D> map(S src) {
			return mapper.apply(src);
		}

		public static <S, D> TypeMapping<S, D> of(Class<S> src, Class<D> dst, Function<S, Optional<D>> mapper) {
			return new TypeMapping<S, D>(src, dst, mapper);
		}
	}

	private static <T> List<T> asList(T... values) {
		return Collections.unmodifiableList(Arrays.asList(values));
	}

	private static Optional<Long> intAsLong(int value) {
		return Optional.of((long) value);
	}
	
	private static Optional<Integer> shortAsInt(short value) {
		return Optional.of((int) value);
	}
	
	private static Optional<Short> byteAsShort(byte value) {
		return Optional.of((short) value);
	}
	
	private static Optional<Integer> longAsInt(long value) {
		int asInt = (int) value;
		return (asInt != value)
				? Optional.empty()
				: Optional.of(asInt);
	}
	
	private static Optional<Short> intAsShort(int value) {
		short asShort = (short) value;
		return (asShort != value)
				? Optional.empty()
				: Optional.of(asShort);
	}
	
	private static Optional<Byte> shortAsByte(short value) {
		byte asByte = (byte) value;
		return (asByte != value)
				? Optional.empty()
				: Optional.of(asByte);
	}
}
