package com.mitchellbosecke.pebble.types;

import com.mitchellbosecke.pebble.types.TypeConverter.Converted;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TypeConverterTest {

	@Test
	public void byteRangeLongValueConvertedToByte() {
		Optional<Converted<Byte>> asByte = TypeConverter.convertTo(byte.class, 1L);
		assertTrue(asByte.isPresent());
		assertEquals(0x01,asByte.get().value().byteValue());
	}
	
	@Test
	public void shortRangeLongValueCanNotConvertToByte() {
		Optional<Converted<Byte>> asByte = TypeConverter.convertTo(byte.class, 129L);
		assertFalse(asByte.isPresent());
		Optional<Converted<Short>> asShort = TypeConverter.convertTo(short.class, 129L);
		assertTrue(asShort.isPresent());
		assertEquals((short) 129,asShort.get().value().shortValue());
	}
}
