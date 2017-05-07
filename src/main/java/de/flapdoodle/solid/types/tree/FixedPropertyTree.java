/**
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
package de.flapdoodle.solid.types.tree;

import java.util.Date;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.types.Either;

@Immutable
public abstract class FixedPropertyTree implements PropertyTree {

	protected abstract ImmutableMultimap<String, Either<Object, ? extends PropertyTree>> map();
	
	@Override
	public ImmutableSet<String> properties() {
		return map().keySet();
	}
	
	@Override
	public ImmutableList<Either<Object, ? extends PropertyTree>> get(String key) {
		return map().get(key).asList();
	}
	
	public static class Builder extends ImmutableFixedPropertyTree.Builder {
		public Builder putValue(String key, Object value) {
			return this.putMap(key, Either.left(value));
		}

		public Builder put(String key, PropertyTree value) {
			return this.putMap(key, Either.right(value));
		}
		
		public Builder put(String key, String value) {
			return putValue(key,value);
		}
		public Builder put(String key, Number value) {
			return putValue(key,value);
		}
		public Builder put(String key, Date value) {
			return putValue(key,value);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
}
