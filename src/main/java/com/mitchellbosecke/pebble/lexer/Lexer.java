/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.io.Reader;

import com.mitchellbosecke.pebble.error.ParserException;

public interface Lexer {

    TokenStream tokenize(Reader templateReader, String name) throws ParserException;
}
