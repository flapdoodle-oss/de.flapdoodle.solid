package de.flapdoodle.solid.theme;

import static de.flapdoodle.solid.types.Multimaps.reverseOrdering;

import java.util.Collection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import de.flapdoodle.solid.generator.GroupedBlobs;
import de.flapdoodle.solid.generator.Menu;
import de.flapdoodle.solid.generator.PathRenderer;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.theme.LinkFactories.Filtered;
import de.flapdoodle.solid.theme.LinkFactories.MenuItem;
import de.flapdoodle.solid.types.Maybe;

public class DefaultLinkFactory implements LinkFactories.Named {

	private final ImmutableMap<String, GroupedBlobs> groupedBlobsById;
	private final PathRenderer pathRenderer;
	private final FormatterOfProperty propertyFormatter;

	public DefaultLinkFactory(ImmutableMap<String, GroupedBlobs> groupedBlobsById, PathRenderer pathRenderer, FormatterOfProperty propertyFormatter) {
		this.groupedBlobsById = groupedBlobsById;
		this.pathRenderer = pathRenderer;
		this.propertyFormatter = propertyFormatter;
	}

	@Override
	public Maybe<LinkFactories.Blobs> byId(String id) {
		return Maybe.ofNullable(groupedBlobsById.get(id)).map(grouped -> new DefaultBlobsLinkFactory(pathRenderer, propertyFormatter, grouped));
	}

	public static LinkFactories.Named of(ImmutableMap<String,GroupedBlobs> groupedBlobsById, PathRenderer pathRenderer, FormatterOfProperty propertyFormatter) {
		return new DefaultLinkFactory(groupedBlobsById, pathRenderer, propertyFormatter);
	}

	private static class DefaultBlobsLinkFactory implements LinkFactories.Blobs {

		private final PathRenderer pathRenderer;
		private final FormatterOfProperty propertyFormatter;
		private final GroupedBlobs grouped;

		public DefaultBlobsLinkFactory(PathRenderer pathRenderer, FormatterOfProperty propertyFormatter, GroupedBlobs grouped) {
			this.pathRenderer = pathRenderer;
			this.propertyFormatter = propertyFormatter;
			this.grouped = grouped;
		}

		@Override
		public Maybe<LinkFactories.OneBlob> filterBy(Blob blob) {
			ImmutableCollection<ImmutableMap<String, Object>> keys = grouped.keysOf(blob);
			if (!keys.isEmpty()) {
				return Maybe.of(new DefaultBlobLinkFactory(pathRenderer, propertyFormatter, grouped.currentPath(), keys));
			}
			return Maybe.absent();
		}

		@Override
		public LinkFactories.Filtered filter() {
			return new DefaultGroupLinkFactory(pathRenderer, propertyFormatter, grouped.currentPath(), grouped.groupedBlobs().asMap());
		}

		@Override
		public LinkFactories.Menu asMenu() {
			return new DefaultMenuLinkFactory(pathRenderer, propertyFormatter, grouped.currentPath(), Menu.of(grouped.groupedBlobs()));
		}
	}

	private static class DefaultMenuLinkFactory implements LinkFactories.Menu {

		private final PathRenderer pathRenderer;
		private final FormatterOfProperty propertyFormatter;
		private final ImmutableList<Menu> menuList;
		private final Path currentPath;

		public DefaultMenuLinkFactory(PathRenderer pathRenderer, FormatterOfProperty propertyFormatter, Path currentPath, ImmutableList<Menu> menuList) {
			this.pathRenderer = pathRenderer;
			this.propertyFormatter = propertyFormatter;
			this.currentPath = currentPath;
			this.menuList = menuList;
		}

		@Override
		public ImmutableList<MenuItem> items() {
			return menuList.stream()
					.map(m -> new DefaultMenuItemLinkFactory(pathRenderer, propertyFormatter, currentPath, m))
					.collect(ImmutableList.toImmutableList());
		}
	}

	private static class DefaultMenuItemLinkFactory implements LinkFactories.MenuItem {

		private final PathRenderer pathRenderer;
		private final FormatterOfProperty propertyFormatter;
		private final Menu menu;
		private final Path currentPath;

		public DefaultMenuItemLinkFactory(PathRenderer pathRenderer, FormatterOfProperty propertyFormatter, Path currentPath, Menu menu) {
			this.pathRenderer = pathRenderer;
			this.propertyFormatter = propertyFormatter;
			this.currentPath = currentPath;
			this.menu = menu;
		}

		@Override
		public String title() {
			return menu.blob().meta().find(String.class, "title").orElseNull();
		}

		@Override
		public String getLink() {
			return pathRenderer.render(currentPath, menu.key(), propertyFormatter).orElseNull();
		}

		@Override
		public ImmutableList<MenuItem> children() {
			return menu.getChildren().stream()
					.map(c -> new DefaultMenuItemLinkFactory(pathRenderer, propertyFormatter, currentPath, c))
					.collect(ImmutableList.toImmutableList());
		}
	}

	private static class DefaultGroupLinkFactory implements LinkFactories.Filtered {

		private final PathRenderer pathRenderer;
		private final FormatterOfProperty propertyFormatter;
		private final Path currentPath;
		private final ImmutableMap<ImmutableMap<String,Object>,Collection<Blob>> groupedBlobs;

		public DefaultGroupLinkFactory(PathRenderer pathRenderer, FormatterOfProperty propertyFormatter, Path currentPath,
				ImmutableMap<ImmutableMap<String,Object>,Collection<Blob>> immutableMap) {
					this.pathRenderer = pathRenderer;
					this.propertyFormatter = propertyFormatter;
					this.currentPath = currentPath;
					this.groupedBlobs = immutableMap;
		}

		@Override
		public LinkFactories.Filtered by(String key, Object value) {
			ImmutableMap<ImmutableMap<String, Object>, Collection<Blob>> filteredMap = filter(groupedBlobs, key, value);
			return new DefaultGroupLinkFactory(pathRenderer, propertyFormatter, currentPath, filteredMap);
		}

		@Override
		public LinkFactories.Filtered orderBy(String key) {
			ImmutableMap<ImmutableMap<String, Object>, Collection<Blob>> orderedMap = order(groupedBlobs, key);
			return new DefaultGroupLinkFactory(pathRenderer, propertyFormatter, currentPath, orderedMap);
		}

		@Override
		public Filtered reversed() {
			return new DefaultGroupLinkFactory(pathRenderer, propertyFormatter, currentPath, reverseOrdering(groupedBlobs));
		}

		private static ImmutableMap<ImmutableMap<String, Object>, Collection<Blob>> order(ImmutableMap<ImmutableMap<String, Object>, Collection<Blob>> src, String key) {
			ImmutableMap.Builder<ImmutableMap<String, Object>, Collection<Blob>> builder=ImmutableMap.builder();
			src.entrySet()
				.stream()
				.sorted(Ordering.natural().onResultOf(e -> {
					Object val = e.getKey().get(key);
					return (val instanceof Comparable)
							? (Comparable) val
							: null;
				}))
				.forEach((e) -> {
					builder.put(e.getKey(), e.getValue());
				});
			return builder.build();
		}

		private static ImmutableMap<ImmutableMap<String, Object>, Collection<Blob>> filter(ImmutableMap<ImmutableMap<String, Object>, Collection<Blob>> src, String key,
				Object value) {
			ImmutableMap.Builder<ImmutableMap<String, Object>, Collection<Blob>> builder=ImmutableMap.builder();
			src.forEach((k,blobs) -> {
				if (value.equals(k.get(key))) {
					builder.put(k, blobs);
				}
			});
			return builder.build();
		}

		@Override
		public ImmutableSet<Object> values(String key) {
			return groupedBlobs.keySet().stream()
				.map(k -> k.get(key))
				.filter(o -> o!=null)
				.collect(ImmutableSet.toImmutableSet());
		}

		@Override
		public String getLink() {
			if (groupedBlobs.keySet().size()==1) {
				return pathRenderer.render(currentPath, groupedBlobs.keySet().iterator().next(), propertyFormatter).orElseNull();
			}
			return null;
		}

		@Override
		public Filtered firstPage() {
			return by(Path.PAGE,1);
		}

		@Override
		public int count() {
			return groupedBlobs.values().stream()
					.map(c -> c.size())
					.reduce(0, (a,b) -> a+b);
		}
	}




	private static class DefaultBlobLinkFactory implements LinkFactories.OneBlob {

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