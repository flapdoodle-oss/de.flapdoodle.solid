package de.flapdoodle.solid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.javaslang.encodings.JavaslangEncodingEnabled;
import org.immutables.value.Value;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(defaults = @Value.Immutable(), strictBuilder=true)
@JavaslangEncodingEnabled
public @interface SolidStyle {

}
