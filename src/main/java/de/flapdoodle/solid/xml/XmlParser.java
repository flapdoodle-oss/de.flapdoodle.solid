package de.flapdoodle.solid.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class XmlParser {

	private final Document doc;

	private XmlParser(Document doc) {
		this.doc = doc;
	}
	
	public <T> T collect(Function<Visitor, T> listener) {
		Element root = doc.getRootElement();
		return listener.apply(Visitor.root(root));
	}
	
	
	public <T> ImmutableList<T> collect(BiFunction<Optional<Element>, Element, Iterable<? extends T>> mapper) {
		ImmutableList.Builder<T> builder=ImmutableList.builder();
		
		Element root = doc.getRootElement();
		visit(null, root, builder::addAll, mapper);
		
		return builder.build();
	}
	
	private <T> void visit(Element parent, Element current, Consumer<Iterable<? extends T>> consumer, BiFunction<Optional<Element>, Element, Iterable<? extends T>> mapper) {
		consumer.accept(mapper.apply(Optional.fromNullable(parent), current));
		current.elements().forEach(child -> {
			visit(current, child, consumer, mapper);
		});
	}

	public <T> ImmutableList<T> collect(String name, Function<Element, Iterable<? extends T>> mapper) {
		ImmutableList.Builder<T> builder=ImmutableList.builder();
		
		doc.getRootElement()
			.elementIterator(name)
			.forEachRemaining(element -> {
				builder.addAll(mapper.apply(element));
		});
		
		return builder.build();
	}
	

	public static XmlParser of(Path path) throws FileNotFoundException, DocumentException {
		return of(new FileReader(path.toFile()));
	}

	public static XmlParser of(Reader reader) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(reader);
		
		return new XmlParser(doc);
	}
}
