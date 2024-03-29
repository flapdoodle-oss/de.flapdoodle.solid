/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.error.RuntimePebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ErrorReportingTest extends AbstractTest {

//    @Rule
//    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testLineNumberErrorReportingWithUnixNewlines() throws PebbleException, IOException {
        //Arrange
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

//        thrown.expect(RuntimePebbleException.class);
//        thrown.expectMessage(endsWith(":7)"));
//        thrown.expectCause(instanceOf(ParserException.class));

        //Act + Assert
        assertThatThrownBy(() -> pebble.getTemplate("test\n\n\ntest\ntest\ntest\n{% error %}\ntest"))
          .isInstanceOf(RuntimePebbleException.class)
          .hasMessageEndingWith(":7)")
          .hasCauseInstanceOf(ParserException.class);
    }

    @Test
    public void testLineNumberErrorReportingWithWindowsNewlines() throws PebbleException, IOException {
        //Arrange
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

//        thrown.expect(RuntimePebbleException.class);
//        thrown.expectMessage(endsWith(":6)"));
//        thrown.expectCause(instanceOf(ParserException.class));

        //Act + Assert
        assertThatThrownBy(() -> pebble.getTemplate("test\r\n\r\ntest\r\ntest\r\ntest\r\n{% error %}\r\ntest"))
          .isInstanceOf(RuntimePebbleException.class)
          .hasMessageEndingWith(":6)")
          .hasCauseInstanceOf(ParserException.class);
    }

    @Test
    public void testLineNumberErrorReportingDuringEvaluation() throws PebbleException, IOException {
        //Arrange
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("templates/template.errorReporting.peb");

//        thrown.expect(PebbleException.class);
//        thrown.expectMessage(endsWith(":8)"));

        //Act + Assert
        assertThatThrownBy(() -> template.evaluate(new StringWriter()))
          .isInstanceOf(PebbleException.class)
          .hasMessageEndingWith(":8)");
    }

    @Test
    public void testMissingRootPropertyInStrictMode() throws PebbleException, IOException {
        //Arrange
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate template = pebble.getTemplate("templates/template.errorReportingMissingRootProperty.peb");

//        thrown.expect(allOf(
//                    instanceOf(RootAttributeNotFoundException.class),
//                    hasProperty("attributeName", is("root"))
//                )
//        );
//        thrown.expectMessage("Root attribute [root] does not exist or can not be accessed and strict variables is set to true. (templates/template.errorReportingMissingRootProperty.peb:2)");

        //Act
        assertThatThrownBy(() -> template.evaluate(new StringWriter()))
          .isInstanceOf(RootAttributeNotFoundException.class)
          .hasFieldOrPropertyWithValue("attributeName","root")
          .hasMessage("Root attribute [root] does not exist or can not be accessed and strict variables is set to true. (templates/template.errorReportingMissingRootProperty.peb:2)");
    }

}
