package de.flapdoodle.solid.parser.types;

import java.io.IOException;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class YamlParserTest {
	@Test
	public void mapYaml() throws IOException {
		String yamlContent = Resources.asCharSource(Resources.getResource(getClass(), "sample.yaml"), Charsets.UTF_8).read();
		Object result = new Yaml().load(yamlContent);
		System.out.println(result);
		System.out.println(result.getClass());
	}

}
