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
package de.flapdoodle.solid.sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.PageSink;
import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.generator.Binary;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Try;

public class StaticHttpServerPageSink implements PageSink {

	private static final Pattern VALID_FS_NAME=Pattern.compile("[a-zA-Z0-9_\\-+\\.]+");
	private final Path exportDirectory;

	public StaticHttpServerPageSink(Path exportDirectory) {
		this.exportDirectory = exportDirectory;
	}
	
	@Override
	public void accept(SiteConfig siteConfig, ImmutableList<Document> documents) {
		String baseUrl = siteConfig.baseUrl();
		
		documents.forEach(doc -> Try.consumer((Document d) -> write(baseUrl, d, exportDirectory))
				.mapCheckedException(SomethingWentWrong::new)
				.accept(doc));
	}

	private static void write(String baseUrl, Document doc, Path exportDirectory) throws IOException {
		Path documentPath=resolve(exportDirectory, baseUrl, doc.path());
		if (doc.content() instanceof Text) {
			Text text=(Text) doc.content();
			Files.createDirectories(documentPath.getParent());
			Files.write(documentPath, text.text().getBytes(text.encoding()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} else {
			if (doc.content() instanceof Binary) {
				Binary binary=(Binary) doc.content();
				Files.createDirectories(documentPath.getParent());
				Files.write(documentPath, binary.data().data(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			} else {
				Preconditions.checkArgument(false, "unsupported content type: "+doc.content());
			}
		}
	}

	@VisibleForTesting
	static Path resolve(Path base, String baseUrl, String url) {
		String documentPath = url;
		if (url.startsWith(baseUrl)) {
			documentPath=url.substring(baseUrl.length());
		}
		
		List<String> parts = Splitter.on('/').omitEmptyStrings().splitToList(documentPath);
		assertValidFilesystemPaths(parts);
		Path filePath = base.resolve(Paths.get(".", parts.toArray(new String[parts.size()]))).normalize();
		if (url.endsWith("/")) {
			filePath=filePath.resolve("index.html");
		} else {
			Preconditions.checkArgument(filePath.getFileName().toString().indexOf('.')!=-1,"invalid url: %s",url);
		}
		return filePath;
	}

	private static void assertValidFilesystemPaths(List<String> parts) {
		for (String part : parts) {
			Preconditions.checkArgument(VALID_FS_NAME.matcher(part).matches(),"invalid fs name: '%s' (%s)",part, parts);
		}
	}
}
