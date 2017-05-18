package de.flapdoodle.solid.formatter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import de.flapdoodle.solid.types.dates.Dates;

public class StringFormatFormatterTest {

	@Test
	public void formatDate() {
		java.util.Formatter f;
		Date date = Dates.map(LocalDateTime.of(2017, 3, 12, 18, 33));
		Optional<String> result = new StringFormatFormatter("%1$td.%1$tm.%1$tY %1$tH:%1$tM").format(date);
		assertEquals("12.03.2017 18:33",result.get());
	}
}
