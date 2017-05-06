package de.flapdoodle.solid;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.generator.Document;

public interface PageSink extends Consumer<ImmutableList<Document>> {

}
