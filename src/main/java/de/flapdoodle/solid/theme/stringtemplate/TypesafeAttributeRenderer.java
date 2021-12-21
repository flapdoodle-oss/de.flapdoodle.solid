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
package de.flapdoodle.solid.theme.stringtemplate;

import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.STGroup;

import com.google.common.base.Preconditions;

public abstract class TypesafeAttributeRenderer<T> implements AttributeRenderer,TypeRenderer<T> {
	
	private final Class<T> type;

	public TypesafeAttributeRenderer(Class<T> type) {
		this.type = type;
	}

	@Override
	public final String toString(Object o, String formatString, Locale locale) {
		Preconditions.checkArgument(type.isInstance(o),"instance %s is not of type %s",o,type);
		return render((T) o, formatString, locale);
	}

	public Class<T> getType() {
		return type;
	}

	public void register(STGroup group) {
		group.registerRenderer(getType(), this);
	}
	

	public static <T> TypesafeAttributeRenderer<T> of(Class<T> type, TypeRenderer<T> renderer) {
		return new TypesafeAttributeRenderer<T>(type) {
			@Override
			public String render(T o, String formatString, Locale locale) {
				return renderer.render(o, formatString, locale);
			}
		};
	}
}
