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
package de.flapdoodle.solid.xml;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.dom4j.Element;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Visitor {

	private final Optional<Element> parent;
	private final Element current;

	private Visitor(Optional<Element> parent, Element current) {
		this.parent = parent;
		this.current = current;
	}
	
	public Element current() {
		return current;
	}
	
	public Optional<Element> parent() {
		return parent;
	}
	
	public String currentTagName() {
		return current.getQualifiedName();
	}
	
	public <T> T dataAsType(Class<T> type) {
		Optional<T> ret = dataIfType(type);
		Preconditions.checkArgument(ret.isPresent(),"data is not of type %s but %s",type, current.getData());
		return ret.get();
	}
	
	public <T> Optional<T> dataIfType(Class<T> type) {
		return type.isInstance(current.getData()) 
				? Optional.of((T) current.getData()) 
				: Optional.empty();
	}
	
	private Visitor downWith(Element current) {
		return new Visitor(Optional.of(this.current), current);
	}
	
	public void visit(Consumer<Visitor> listener) {
		current.elements()
			.forEach(e -> {
				listener.accept(downWith(e));
			});
	}
	
	public <T> ImmutableList<T> collect(Function<Visitor, Iterable<T>> listener) {
		Builder<T> builder = ImmutableList.<T>builder();
		visit(e -> builder.addAll(listener.apply(e)));
		return builder.build();
	}

	public static Visitor root(Element root) {
		return new Visitor(Optional.empty(),root);
	}

}
