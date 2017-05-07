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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.flapdoodle.solid.types.OneOf.OneOfFactory;

public class OneOfTest {

	@Test
	public void simpleFactory() {
		OneOfFactory oneOf = OneOf.type(String.class);
		assertNotNull(oneOf);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void canNotCreateFactoryForDependendClasses() {
		OneOf.type(Number.class,String.class, Double.class);
	}
}
