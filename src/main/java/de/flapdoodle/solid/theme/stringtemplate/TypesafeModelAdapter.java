package de.flapdoodle.solid.theme.stringtemplate;

import java.util.function.BiFunction;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import com.google.common.base.Preconditions;

public abstract class TypesafeModelAdapter<T> implements ModelAdaptor {

	private final Class<T> type;

	public TypesafeModelAdapter(Class<T> type) {
		this.type = type;
	}
	
	@Override
	public final Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
		Preconditions.checkArgument(type.isInstance(o),"instance %s is not of type %s",o,type);
		return getProperty((T) o, propertyName);
	}
	
	public Class<T> getType() {
		return type;
	}
	
	protected abstract Object getProperty(T instance, String propertyName); 
	
	public void register(STGroup group) {
		group.registerModelAdaptor(getType(), this);
	}
	
	public static <T> TypesafeModelAdapter<T> of(Class<T> type, BiFunction<T, String, Object> propertyAccess) {
		return new TypesafeModelAdapter<T>(type) {
			@Override
			protected Object getProperty(T instance, String propertyName) {
				return propertyAccess.apply(instance, propertyName);
			}
		};
	}
}
