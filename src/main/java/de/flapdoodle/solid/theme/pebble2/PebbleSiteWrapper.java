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
package de.flapdoodle.solid.theme.pebble2;

import com.google.common.collect.ImmutableMap;
import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.parser.Tree;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.types.Maybe;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class PebbleSiteWrapper implements DynamicAttributeResolver {
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
	public boolean enableDisqus() {
		return config().enableDisqus();
	}
	
	@Auxiliary
	public ImmutableMap<String,Formatter> getFormatters() {
		return config().formatters().formatters();
	}

	@Auxiliary
	public Tree tree(String id) {
		return config().tree(id).orElse(null);
	}


	public static PebbleSiteWrapper of(SiteConfig config) {
		return ImmutablePebbleSiteWrapper.of(config);
	}

}
