package de.flapdoodle.solid.parser.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.types.GroupedPropertyMap;

public class Yaml2GroupedPropertyMapTest {
	@Test
	public void mapToml() throws IOException {
		String tomlContent = Resources.asCharSource(Resources.getResource(getClass(), "sample.yaml"), Charsets.UTF_8).read();
		Yaml toml = Yaml.parse(tomlContent);
		GroupedPropertyMap groupedMap = new Yaml2GroupedPropertyMap().asGroupedPropertyMap(toml);
		assertEquals("ImmutableGroupedPropertyMap{{Key{path=[]}={title=spf13-vim 3.0 release and new website, description=spf13-vim is a cross platform distribution of vim plugins and resources for Vim., tags=[.vimrc, plugins, spf13-vim, vim], lastmod=Wed Dec 23 01:00:00 CET 2015, date=2012-04-06, created=Tue Oct 24 23:04:33 CEST 2006, categories=[Development, VIM], slug=spf13-vim-3-0-release-and-new-website}}}", groupedMap.toString());
	}
}
