package de.flapdoodle.solid.theme.pebble;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;

import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.types.Maybe;

@Immutable
public abstract class PebbleSiteWrapper implements DynamicAttributeProvider {
	@Parameter
	protected abstract SiteConfig config();
	
	@Override
	@Auxiliary
	public boolean canProvideDynamicAttribute(Object attributeName) {
		return config().properties().containsKey(attributeName);
	}
	
	@Override
	@Auxiliary
	public Object getDynamicAttribute(Object attributeName, Object[] argumentValues) {
		String key=attributeName.toString();
		return Maybe.<Object>ofNullable(config().properties().get(key))
				.orElse(() -> config().properties().get(key.substring(0,1).toLowerCase()+key.substring(1)));
	}
	
	@Auxiliary
	public String getBaseUrl() {
		return config().baseUrl();
	}
	
	@Auxiliary
	public ImmutableMap<String,Formatter> getFormatters() {
		return config().formatters().formatters();
	}
	
	
	public static PebbleSiteWrapper of(SiteConfig config) {
		return ImmutablePebbleSiteWrapper.of(config);
	}

}
