/*
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.solid.converter.wordpress;

import java.util.Optional;
import java.util.function.Consumer;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.xml.Visitor;

/*
 * 
	<wp:term>
		<wp:term_id><![CDATA[100]]></wp:term_id>
		<wp:term_taxonomy><![CDATA[post_tag]]></wp:term_taxonomy>
		<wp:term_slug><![CDATA[behavior]]></wp:term_slug>
		<wp:term_parent><![CDATA[]]></wp:term_parent>
		<wp:term_name><![CDATA[behavior]]></wp:term_name>
	</wp:term>

 */
@Immutable
public abstract class WpTerm {
	public abstract String id();
	public abstract String taxonomy();
	public abstract String urlName();
	public abstract Optional<String> parent();
	public abstract String name();

	public static void read(Visitor visitor, Consumer<WpTerm> categoryConsumer) {
		Preconditions.checkArgument(visitor.currentTagName().equals("wp:term"),"not wp:term");
		
		ImmutableWpTerm.Builder builder=ImmutableWpTerm.builder();
		visitor.visit(v -> properties().accept(v, builder));
		categoryConsumer.accept(builder.build());
	}

	private static VisitorConsumer<ImmutableWpTerm.Builder> properties() {
		return VisitorConsumer.ofMap(ImmutableMap.<String, VisitorConsumer<ImmutableWpTerm.Builder>>builder()
				.put("wp:term_id", (visitor, builder) -> builder.id(visitor.dataAsType(String.class)))
				.put("wp:term_taxonomy", (visitor, builder) -> builder.taxonomy(visitor.dataAsType(String.class)))
				.put("wp:term_slug", (visitor, builder) -> builder.urlName(visitor.dataAsType(String.class)))
				.put("wp:term_parent", (visitor, builder) -> builder.parent(visitor.dataIfType(String.class)
						.flatMap((String s) -> !s.isEmpty() ? Optional.of(s) : java.util.Optional.empty())))
				.put("wp:term_name", (visitor, builder) -> builder.name(visitor.dataAsType(String.class)))
				.build());
	}
}
