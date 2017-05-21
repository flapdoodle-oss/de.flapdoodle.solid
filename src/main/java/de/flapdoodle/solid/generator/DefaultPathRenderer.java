package de.flapdoodle.solid.generator;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.parser.path.Path.Property;
import de.flapdoodle.solid.types.Maybe;

public class DefaultPathRenderer implements PathRenderer {

	@Override
	public Maybe<String> render(Path path, ImmutableMap<String, Object> properties, FormatterOfProperty propertyFormatter) {
		if (path.propertyNames().size()<=properties.keySet().size()) {
			StringBuilder sb=new StringBuilder();
			for (Path.Part part : path.parts()) {
				if (part instanceof Path.Static) {
					sb.append(urlify(((Path.Static) part).fixed()));
				} else {
					if (part instanceof Path.Property) {
						Property property = (Path.Property) part;
						Maybe<Object> mappedValue = Maybe.ofNullable(properties.get(property.property()));
						Maybe<String> urlPart = mappedValue.flatMap(v -> propertyFormatter.of(property.property(), property.formatter()).format(v));
						if (urlPart.isPresent()) {
							if ((property.property().equals(Path.PAGE)) && urlPart.get().equals("1")) {
								break;
							}
							sb.append(urlify(urlPart.get()));
						} else {
							return Maybe.empty();
						}
					}
				}
			};
			return Maybe.of(sb.toString());
		}
		return Maybe.empty();
	}
	
	private static String urlify(String src) {
		return src.replace(' ', '-')
				.replace("--", "-")
				.toLowerCase()
				.replaceAll("[^a-zA-Z0-9/\\-_.]", "");
	}
}
