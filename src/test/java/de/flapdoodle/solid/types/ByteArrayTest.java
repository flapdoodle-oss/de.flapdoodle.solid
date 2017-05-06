package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;

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