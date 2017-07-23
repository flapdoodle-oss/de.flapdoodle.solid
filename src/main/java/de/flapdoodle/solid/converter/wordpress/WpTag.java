package de.flapdoodle.solid.converter.wordpress;

import java.util.function.Consumer;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.xml.Visitor;

/*
 * 
	<wp:tag>
		<wp:term_id>21</wp:term_id>
		<wp:tag_slug><![CDATA[135]]></wp:tag_slug>
		<wp:tag_name><![CDATA[1.3.5]]></wp:tag_name>
	</wp:tag>

 */
@Immutable
public abstract class WpTag {
	public abstract String id();
	public abstract String urlName();
	public abstract String name();

	public static void read(Visitor visitor, Consumer<WpTag> categoryConsumer) {
		Preconditions.checkArgument(visitor.currentTagName().equals("wp:tag"),"not wp:tag");
		
		ImmutableWpTag.Builder builder=ImmutableWpTag.builder();
		visitor.visit(v -> properties().accept(v, builder));
		categoryConsumer.accept(builder.build());
	}

	private static VisitorConsumer<ImmutableWpTag.Builder> properties() {
		return VisitorConsumer.ofMap(ImmutableMap.<String, VisitorConsumer<ImmutableWpTag.Builder>>builder()
				.put("wp:term_id", (visitor, builder) -> builder.id(visitor.dataAsType(String.class)))
				.put("wp:tag_slug", (visitor, builder) -> builder.urlName(visitor.dataAsType(String.class)))
				.put("wp:tag_name", (visitor, builder) -> builder.name(visitor.dataAsType(String.class)))
				.build());
	}
}
