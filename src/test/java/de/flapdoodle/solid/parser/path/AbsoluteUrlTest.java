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
package de.flapdoodle.solid.parser.path;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AbsoluteUrlTest {

	@Test
	public void emptyPathMustFail() {
		assertThatThrownBy(() ->	AbsoluteUrl.parse(""))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void notAbsoluteMustFail() {
		assertThatThrownBy(() ->	AbsoluteUrl.parse("foo"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void smallestUrl() {
		AbsoluteUrl url = AbsoluteUrl.parse("/");
		assertThat(url.parts())
				.containsExactly("/");
		assertThat(url.endsWithSlash()).isTrue();
	}

	@Test
	public void oneElement() {
		AbsoluteUrl url = AbsoluteUrl.parse("/foo");
		assertThat(url.parts())
				.containsExactly("/foo");
		assertThat(url.endsWithSlash()).isFalse();
	}

	@Test
	public void moreThanOneElement() {
		AbsoluteUrl url = AbsoluteUrl.parse("/foo/bar/baz");
		assertThat(url.parts())
				.containsExactly("/foo", "/bar", "/baz");
		assertThat(url.endsWithSlash()).isFalse();
	}

	@Test
	public void pathCanEndWithOnlySlash() {
		AbsoluteUrl url = AbsoluteUrl.parse("/foo/bar/baz/");
		assertThat(url.parts())
				.containsExactly("/foo", "/bar", "/baz", "/");
		assertThat(url.endsWithSlash()).isTrue();
	}

	@Test
	public void relativePathToFileInSameDirectoryShouldContainNewFilename() {
		AbsoluteUrl current = AbsoluteUrl.parse("/root/foo/bar");
		AbsoluteUrl destination = AbsoluteUrl.parse("/root/foo/baz");

		String result = current.relativePathTo(destination);

		assertThat(result).isEqualTo("baz");
	}

	@Test
	public void relativePathToFileInOtherDirectoryShouldGoOneDirUp() {
		AbsoluteUrl current = AbsoluteUrl.parse("/root/foo/bar");
		AbsoluteUrl destination = AbsoluteUrl.parse("/root/blob/baz");

		String result = current.relativePathTo(destination);

		assertThat(result)
				.describedAs("from %s to %s", current.parts(), destination.parts())
				.isEqualTo("../blob/baz");
	}

	@Test
	public void relativePathToTotalDifferentDir() {
		AbsoluteUrl current = AbsoluteUrl.parse("/one/two/three");
		AbsoluteUrl destination = AbsoluteUrl.parse("/1/2/3");

		String result = current.relativePathTo(destination);

		assertThat(result)
				.describedAs("from %s to %s", current.parts(), destination.parts())
				.isEqualTo("../../1/2/3");
	}

	@Test
	public void relativePathToTotalDifferentDirIfCurrentEndsWithSlash() {
		AbsoluteUrl current = AbsoluteUrl.parse("/one/two/three/");
		AbsoluteUrl destination = AbsoluteUrl.parse("/1/2/3");

		String result = current.relativePathTo(destination);

		assertThat(result)
				.describedAs("from %s to %s", current.parts(), destination.parts())
				.isEqualTo("../../../1/2/3");
	}

	@Test
	public void relativePathToTotalDifferentDirIfDestinationEndsWithSlash() {
		AbsoluteUrl current = AbsoluteUrl.parse("/one/two/three");
		AbsoluteUrl destination = AbsoluteUrl.parse("/1/2/3/");

		String result = current.relativePathTo(destination);

		assertThat(result)
				.describedAs("from %s to %s", current.parts(), destination.parts())
				.isEqualTo("../../1/2/3/");
	}

	@Test
	public void sample() {
		AbsoluteUrl current = AbsoluteUrl.parse("/blog/");
		AbsoluteUrl destination = AbsoluteUrl.parse("/blog/css/all.css");

		String result = current.relativePathTo(destination);

		assertThat(result)
				.describedAs("from %s to %s", current.parts(), destination.parts())
				.isEqualTo("css/all.css");
	}
}
