/**
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
