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

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.theme.MapLike;
import de.flapdoodle.solid.types.Maybe;

@Immutable
public abstract class StringtemplateSiteWrapper implements MapLike {
	@Parameter
	protected abstract SiteConfig config();
	
	@Override
	@Auxiliary
	public Maybe<Object> get(String key) {
		return Maybe.<Object>ofNullable(config().properties().get(key))
				.or(() -> Maybe.<Object>ofNullable(config().properties().get(key.substring(0,1).toLowerCase()+key.substring(1))));
	}
	
	public static StringtemplateSiteWrapper of(SiteConfig config) {
		return ImmutableStringtemplateSiteWrapper.of(config);
	}
}
