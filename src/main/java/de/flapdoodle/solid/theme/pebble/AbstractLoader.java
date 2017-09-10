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
package de.flapdoodle.solid.theme.pebble;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mitchellbosecke.pebble.loader.Loader;

public abstract class AbstractLoader implements Loader<String> {

	@Override
	public void setCharset(String charset) {
		throw new IllegalArgumentException("this should not be called.");
	}

	@Override
	public void setPrefix(String prefix) {
		throw new IllegalArgumentException("this should not be called.");
	}

	@Override
	public void setSuffix(String suffix) {
		throw new IllegalArgumentException("this should not be called.");
	}

	@Override
	public String resolveRelativePath(String relativePath, String anchorPath) {
		return resolvePath(relativePath, anchorPath);
	}

	@VisibleForTesting
	protected static String resolvePath(String relativePath, String anchorPath) {
		Path parent = resolve(anchorPath, '/').getParent();
		if (parent==null) {
			parent=Paths.get("");
		}
		return parent.resolve(resolve(relativePath+".html", '/')).normalize().toString();
	}
	
	protected static Path resolve(String path, char pathSeparator) {
		Iterable<String> splitted = Splitter.on(pathSeparator).split(path);
		Iterator<String> iterator = splitted.iterator();
		Path current=Paths.get(iterator.next());
		while (iterator.hasNext()) {
			current=current.resolve(iterator.next());
		}
		return current;
	}

	@Override
	public String createCacheKey(String templateName) {
		return templateName;
	}


}
