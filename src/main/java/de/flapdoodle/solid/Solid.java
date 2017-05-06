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

import de.flapdoodle.solid.generator.DefaultSiteGenerator;

@SuppressWarnings("ucd")
public class Solid {

	public static void main(String[] args) {
		System.out.println("solid - a static site generator");
		
		Path siteARoot = Paths.get(args[0]);
		
		testing(siteARoot).run();
	}

	public static Runnable testing(Path siteRoot) {
		return StaticPageGenerator.once().generator(SiteSpring.withPath(siteRoot), new DefaultSiteGenerator(), (documents) -> {
			if (!documents.isEmpty()) {
				System.out.println("-------------------------");
				System.out.println("Documents: ");
				documents.forEach(d -> {
					System.out.println(" - "+d.path());
				});
				System.out.println("-------------------------");
			} else {
				System.out.println("-------------------------");
				System.out.println("No generated Documents.");
				System.out.println("-------------------------");
			}
		});
	}
}
