package de.flapdoodle.solid.parser.path;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AbsoluteUrlTest {

	@Test(expected = IllegalArgumentException.class)
	public void emptyPathMustFail() {
		AbsoluteUrl.parse("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAbsoluteMustFail() {
		AbsoluteUrl.parse("foo");
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
}
