package com.mitchellbosecke.pebble.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PathUtils}.
 *
 * @author Thomas Hunziker
 *
 */
public class PathUtilsTest {

    private static final char FORWARD_SLASH = '/';
    private static final char BACKWARD_SLASH = '\\';

    /**
     * Tests if {@link PathUtils#resolveRelativePath(String, String, char)} is
     * working with Unix systems.
     */
    @Test
    public void testRelativePathResolutionUnixStyle() {
        assertEquals("test/test/sample.peb", PathUtils.resolveRelativePath("./sample.peb", "test/test/", FORWARD_SLASH));
        assertEquals("test/test/sample.peb",
                PathUtils.resolveRelativePath("./sample.peb", "test/test/other.peb", FORWARD_SLASH));
        assertEquals("test/sample.peb", PathUtils.resolveRelativePath("../sample.peb", "test/test/", FORWARD_SLASH));
        assertEquals("test/sample.peb", PathUtils.resolveRelativePath("../sample.peb", "test/test/other.peb", FORWARD_SLASH));
        assertEquals(null, PathUtils.resolveRelativePath("test/sample.peb", "test/test/other.peb", FORWARD_SLASH));

    }

    /**
     * Tests if {@link PathUtils#resolveRelativePath(String, String, char)} is
     * working with Windows.
     */
    @Test
    public void testRelativePathResolutionWindowsStyle() {
        assertEquals("test\\test\\sample.peb", PathUtils.resolveRelativePath(".\\sample.peb", "test\\test\\", BACKWARD_SLASH));
        assertEquals("test\\test\\sample.peb",
                PathUtils.resolveRelativePath(".\\sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
        assertEquals("test\\sample.peb", PathUtils.resolveRelativePath("..\\sample.peb", "test\\test\\", BACKWARD_SLASH));
        assertEquals("test\\sample.peb",
                PathUtils.resolveRelativePath("..\\sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
        assertEquals(null, PathUtils.resolveRelativePath("test\\sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));

    }


    @Test
    public void testRelativePathResolutionMixedStyle1() {
        assertEquals("test/test/sample.peb", PathUtils.resolveRelativePath(".\\sample.peb", "test/test/", FORWARD_SLASH));
        assertEquals("test/test/sample.peb",
                PathUtils.resolveRelativePath(".\\sample.peb", "test/test/other.peb", FORWARD_SLASH));
        assertEquals("test/sample.peb", PathUtils.resolveRelativePath("..\\sample.peb", "test/test/", FORWARD_SLASH));
        assertEquals("test/sample.peb", PathUtils.resolveRelativePath("..\\sample.peb", "test/test/other.peb", FORWARD_SLASH));
        assertEquals(null, PathUtils.resolveRelativePath("test\\sample.peb", "test/test/other.peb", FORWARD_SLASH));
    }

    @Test
    public void testRelativePathResolutionMixedStyle2() {
        assertEquals("test\\test\\sample.peb", PathUtils.resolveRelativePath("./sample.peb", "test\\test\\", BACKWARD_SLASH));
        assertEquals("test\\test\\sample.peb",
                PathUtils.resolveRelativePath("./sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
        assertEquals("test\\sample.peb", PathUtils.resolveRelativePath("../sample.peb", "test\\test\\", BACKWARD_SLASH));
        assertEquals("test\\sample.peb",
                PathUtils.resolveRelativePath("../sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
        assertEquals(null, PathUtils.resolveRelativePath("test/sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
    }
}
