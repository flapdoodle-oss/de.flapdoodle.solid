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
package de.flapdoodle.solid.formatter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.Test;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.dates.Dates;

public class StringFormatFormatterTest {

	@Test
	public void formatDate() {
		java.util.Formatter f;
		Date date = Dates.map(LocalDateTime.of(2017, 3, 12, 18, 33));
		Maybe<String> result = new StringFormatFormatter("%1$td.%1$tm.%1$tY %1$tH:%1$tM").format(date);
		assertEquals("12.03.2017 18:33",result.get());
	}
}
