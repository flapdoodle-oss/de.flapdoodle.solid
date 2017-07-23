package de.flapdoodle.solid.converter.wordpress;

import java.util.Optional;
import java.util.function.Consumer;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.xml.Visitor;

/*
 * 	<wp:category>
		<wp:term_id>1</wp:term_id>
		<wp:category_nicename><![CDATA[allgemein]]></wp:category_nicename>
		<wp:category_parent><![CDATA[]]></wp:category_parent>
		<wp:cat_name><![CDATA[Allgemein]]></wp:cat_name>
	</wp:category>

 */
@Immutable
public abstract class WpCategory {
	public abstract String id();
	public abstract String urlName();
	public abstract Optional<String> parent();
	public abstract String name();

	public static void read(Visitor visitor, Consumer<WpCategory> categoryConsumer) {
		Preconditions.checkArgument(visitor.currentTagName().equals("wp:category"),"not wp:category");
		
		ImmutableWpCategory.Builder builder=ImmutableWpCategory.builder();
		visitor.visit(v -> properties().accept(v, builder));
		categoryConsumer.accept(builder.build());
	}

	private static VisitorConsumer<ImmutableWpCategory.Builder> properties() {
		ImmutableMap<String, VisitorConsumer<ImmutableWpCategory.Builder>> map = ImmutableMap.<String, VisitorConsumer<ImmutableWpCategory.Builder>>builder()
				.put("wp:term_id", (visitor, builder) -> builder.id(visitor.dataAsType(String.class)))
				.put("wp:category_nicename", (visitor, builder) -> builder.urlName(visitor.dataAsType(String.class)))
				.put("wp:category_parent", (visitor, builder) -> builder.parent(visitor.dataIfType(String.class)
						.flatMap((String s) -> !s.isEmpty() ? Optional.of(s) : java.util.Optional.empty())))
				.put("wp:cat_name", (visitor, builder) -> builder.name(visitor.dataAsType(String.class)))
				.build();
		
		return VisitorConsumer.ofMap(map);
	}
}
