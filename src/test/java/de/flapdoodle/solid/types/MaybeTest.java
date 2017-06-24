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
package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

public class MaybeTest {

	@Test
	public void some() {
		Maybe<String> maybe = Maybe.of("foo");
		
		assertEquals("foo",maybe.get());
		assertTrue(maybe.isPresent());
	}
	
	@Test
	public void optionals() {
		Optional<String> asOptional = Maybe.fromOptional(Optional.<String>empty()).asOptional();
		assertFalse(asOptional.isPresent());
		
		asOptional = Maybe.fromOptional(Optional.of("foo")).asOptional();
		assertTrue(asOptional.isPresent());
		assertEquals("foo",asOptional.get());
	}
}
