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
package de.flapdoodle.solid.types;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ByteArrayTest {

	@Test
	public void thatByteArrayMakesACopyOfSourceArray() {
		byte[] source = new byte[] {0, 1, 2};
		ByteArray byteArray = ByteArray.fromArray(source);
		source[0] = 17;
		assertEquals(0, byteArray.data()[0]);
	}

	@Test
	public void sameDataMustBeEqual() {
		ByteArray a = ByteArray.fromArray(new byte[] {0, 1, 2});
		ByteArray a2 = ByteArray.fromArray(new byte[] {0, 1, 2});
		ByteArray b = ByteArray.fromArray(new byte[] {0, 1, 2, 4});

		assertEquals(a, a);
		assertEquals(a, a2);
		assertEquals(a.hashCode(), a2.hashCode());
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
	}

	@Test
	public void isNotEqualToOtherStuffThanByteArray() {
		ByteArray a = ByteArray.fromArray(new byte[] {0, 1, 2});
		assertFalse(a.equals(null));
		assertFalse(a.equals(""));
		assertFalse(a.equals(new byte[] {0, 1, 2}));
	}

	@Test
	public void readDataToByteArrayMustContainAllData() throws IOException {
		ByteArray source = ByteArray.fromArray(new byte[] {0, 1, 2});
		ByteArray dest = ByteArray.fromStream(source.asInputStream());
		assertEquals(source, dest);
	}
}