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
package de.flapdoodle.solid;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.flapdoodle.solid.generator.PathRenderer;
import de.flapdoodle.solid.generator.SiteGenerator;
import de.flapdoodle.solid.sinks.DebuggingPageSink;
import de.flapdoodle.solid.sinks.StaticHttpServerPageSink;
import de.flapdoodle.solid.sinks.UndertowPageSink;

@SuppressWarnings("ucd")
public class Solid {

	public static void main(String[] args) {
		System.out.println("solid - a static site generator");
		Preconditions.checkArgument(args.length>=2,"usage: <siteRoot> <exportDirectory>");
		
		Path siteRoot = Paths.get(args[0]);
		Path target = Paths.get(args[1]);
		
		Injector injector = Guice.createInjector();
		
		StaticPageGenerator.onChange(siteRoot, target)
			.generator(SiteSpring.withPath(siteRoot), SiteGenerator.defaultGenerator(site -> PathRenderer.defaultPathRenderer(site.config().baseUrl())), new StaticHttpServerPageSink(target)
					.andThen(new DebuggingPageSink(false))
					.andThen(new UndertowPageSink()))
			.run();
	}

	public static Runnable testing(Path siteRoot) {
		return StaticPageGenerator.once().generator(SiteSpring.withPath(siteRoot), SiteGenerator.defaultGenerator(site -> PathRenderer.defaultPathRenderer(site.config().baseUrl())), new DebuggingPageSink(true));
	}
}
