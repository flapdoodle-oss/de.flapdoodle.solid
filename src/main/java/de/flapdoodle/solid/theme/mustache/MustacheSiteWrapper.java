package de.flapdoodle.solid.theme.mustache;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.types.Maybe;

@Immutable
public abstract class MustacheSiteWrapper implements MapLike {
	@Parameter
	protected abstract SiteConfig config();
	
	@Override
	@Auxiliary
	public Maybe<Object> get(String key) {
		return Maybe.<Object>ofNullable(config().properties().get(key))
				.or(() -> Maybe.<Object>ofNullable(config().properties().get(key.substring(0,1).toLowerCase()+key.substring(1))));
	}
	
	public static MustacheSiteWrapper of(SiteConfig config) {
		return ImmutableMustacheSiteWrapper.of(config);
	}
}
