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
package de.flapdoodle.solid.exceptions;

import java.nio.file.Path;

import com.google.common.base.Joiner;

public class NotASolidSite extends AbstractRuntimeException {

	public NotASolidSite(Path path, Iterable<String> triedConfigs) {
		super(asMessage(path, triedConfigs));
	}

	private static String asMessage(Path path, Iterable<String> triedConfigs) {
		return "could not find a matching config"+Joiner.on(",").join(triedConfigs)+" in "+path;
	}

}
