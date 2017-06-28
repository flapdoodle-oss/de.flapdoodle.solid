package de.flapdoodle.solid.theme;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.generator.GroupedBlobs;
import de.flapdoodle.solid.generator.PathRenderer;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.types.Maybe;

public class DefaultLinkFactory implements LinkFactory {
	
	private final ImmutableMap<String, GroupedBlobs> groupedBlobsById;
	private final PathRenderer pathRenderer;
	private final FormatterOfProperty propertyFormatter;

	public DefaultLinkFactory(ImmutableMap<String, GroupedBlobs> groupedBlobsById, PathRenderer pathRenderer, FormatterOfProperty propertyFormatter) {
		this.groupedBlobsById = groupedBlobsById;
		this.pathRenderer = pathRenderer;
		this.propertyFormatter = propertyFormatter;
	}
	
	@Override
	public Maybe<? extends BlobsLinkFactory> byId(String id) {
		return Maybe.ofNullable(groupedBlobsById.get(id)).map(grouped -> new DefaultBlobsLinkFactory(pathRenderer, propertyFormatter, grouped));
	}

	public static LinkFactory of(ImmutableMap<String,GroupedBlobs> groupedBlobsById, PathRenderer pathRenderer, FormatterOfProperty propertyFormatter) {
		return new DefaultLinkFactory(groupedBlobsById, pathRenderer, propertyFormatter);
	}
	
	private static class DefaultBlobsLinkFactory implements BlobsLinkFactory {

		private final PathRenderer pathRenderer;
		private final FormatterOfProperty propertyFormatter;
		private final GroupedBlobs grouped;

		public DefaultBlobsLinkFactory(PathRenderer pathRenderer, FormatterOfProperty propertyFormatter, GroupedBlobs grouped) {
			this.pathRenderer = pathRenderer;
			this.propertyFormatter = propertyFormatter;
			this.grouped = grouped;
		}
		
		@Override
		public Maybe<BlobLinkFactory> filterBy(Blob blob) {
			ImmutableCollection<ImmutableMap<String, Object>> keys = grouped.keysOf(blob);
			if (!keys.isEmpty()) {
				return Maybe.of(new DefaultBlobLinkFactory(pathRenderer, propertyFormatter, grouped.currentPath(), keys));
			}
			return Maybe.absent();
		}
		
	}
	
	
	private static class DefaultBlobLinkFactory implements BlobLinkFactory {

		private final PathRenderer pathRenderer;
		private final FormatterOfProperty propertyFormatter;
		private final Path currentPath;
		private final ImmutableList<ImmutableMap<String, Object>> keys;

		public DefaultBlobLinkFactory(PathRenderer pathRenderer, FormatterOfProperty propertyFormatter, Path currentPath,
				ImmutableCollection<ImmutableMap<String, Object>> keys) {
					this.pathRenderer = pathRenderer;
					this.propertyFormatter = propertyFormatter;
					this.currentPath = currentPath;
					this.keys = keys.asList();
		}

		@Override
		public String getLink() {
			if (keys.size()==1) {
				return pathRenderer.render(currentPath, keys.get(0), propertyFormatter).orElseNull();
			}
			return null;
		}
		
		@Override
		public String getLink(String key, Object value) {
			ImmutableList<ImmutableMap<String, Object>> matchingKeys = keys.stream()
				.filter(m -> value.equals(m.get(key)))
				.collect(ImmutableList.toImmutableList());
			if (matchingKeys.size()==1) {
				return pathRenderer.render(currentPath, matchingKeys.get(0), propertyFormatter).orElseNull();
			}
			return null;
		}
	}
}