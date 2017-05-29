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
