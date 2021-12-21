/*
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.solid.types.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import de.flapdoodle.solid.types.Maybe;

public class Inspector {

	public static ImmutableSet<String> propertyNamesOf(Class<?> type) {
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		propertiesOf(builder, type);
		return builder.build();
	}

	private static void propertiesOf(Builder<String> builder, Class<?> type) {
		if (!type.getPackage().getName().startsWith("java.lang")) {
			Method[] methods = type.getDeclaredMethods();
			for (Method m : methods) {
				if (isVisibleMethod(m)) {
					System.out.println(type+"."+m.getName());
					Maybe<String> propertyName=propertyNameOf(m);
					propertyName.ifPresent(name -> {
						builder.add(name);
					});
				}
			}
			Class<?> superClass = type.getSuperclass();
			if (superClass!=null && superClass!=Object.class) {
				propertiesOf(builder, superClass);
			}
			Class<?>[] interfaces = type.getInterfaces();
			for (Class<?> i:interfaces) {
				propertiesOf(builder, i);
			}
		}
	}

	private static Maybe<String> propertyNameOf(Method m) {
		String name=m.getName();
		if (m.getParameterTypes().length==0) {
			return asPropertyName(name, "get")
					.or(() -> asPropertyName(name, "is"))
					.or(() -> Maybe.of(name));
		}
		return Maybe.absent();
	}
	
	private static Maybe<String> asPropertyName(String name, String prefix) {
		return cut(name,prefix).map(Inspector::lowerCaseFirstChar);
	}
	
	private static String lowerCaseFirstChar(String src) {
		return src.substring(0,1).toLowerCase()+src.substring(1);
	}
	
	private static Maybe<String> cut(String src, String prefix) {
		if (src.startsWith(prefix)) {
			return Maybe.of(src.substring(prefix.length()));
		}
		return Maybe.absent();
	}

	private static boolean isVisibleMethod(Method m) {
		return Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers());
	}
}
