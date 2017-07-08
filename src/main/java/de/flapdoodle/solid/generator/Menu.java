package de.flapdoodle.solid.generator;

import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public abstract class Menu {
	
	public abstract Blob blob();
	public abstract ImmutableMap<String, Object> key();
	public abstract ImmutableList<Menu> getChildren();
	
	public static ImmutableList<Menu> of(ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs) {
		ImmutableList<BlobAndMenu> sortedBlobs = groupedBlobs.inverse().asMap()
				.entrySet()
				.stream()
				.filter(e -> !e.getValue().isEmpty())
				.map(e -> BlobAndMenu.of(e.getKey(), e.getValue().iterator().next()))
				.sorted((a,b) -> Long.compare(a.menu().weight(), b.menu().weight()))
				.collect(ImmutableList.toImmutableList());
		
		ImmutableList<BlobAndMenu> parents = sortedBlobs.stream()
				.filter(blobmenu -> !blobmenu.menu().parent().isPresent())
				.collect(ImmutableList.toImmutableList());
		
		return parents.stream()
			.map(parent -> asMenu(parent,sortedBlobs))
			.collect(ImmutableList.toImmutableList());
	}
	
	private static Menu asMenu(BlobAndMenu parent, ImmutableList<BlobAndMenu> sortedBlobs) {
		return ImmutableMenu.builder()
				.blob(parent.blob())
				.putAllKey(parent.key())
				.addAllChildren(sortedBlobs.stream()
						.filter(c -> isParent(parent.menu(), c.menu()))
						.map(c -> asMenu(c,sortedBlobs))
						.collect(ImmutableList.toImmutableList()))
				.build();
	}


	private static boolean isParent(MenuMeta parent, MenuMeta child) {
		return parent.id().isPresent() && child.parent().isPresent() && parent.id().get().equals(child.parent().get());
	}


	@Immutable
	static abstract class MenuMeta {
		protected abstract Optional<String> id();
		protected abstract Optional<String> parent();
		protected abstract long weight();
		
		public static MenuMeta of(PropertyTree tree) {
			return ImmutableMenuMeta.builder()
				.id(tree.find(String.class, "menu","main","identifier").asOptional())
				.parent(tree.find(String.class, "menu","main","parent").asOptional())
				.weight(tree.find(Number.class, "menu","main","weight")
						.map(number -> number.longValue())
						.orElseGet(0L))
				.build();
		}
	}
	
	@Immutable
	static interface BlobAndMenu {
		Blob blob();
		ImmutableMap<String, Object> key();
		MenuMeta menu();
		
		public static BlobAndMenu of(Blob blob, ImmutableMap<String, Object> key) {
			return ImmutableBlobAndMenu.builder()
					.blob(blob)
					.putAllKey(key)
					.menu(MenuMeta.of(blob.meta()))
					.build();
		}
	}
}
