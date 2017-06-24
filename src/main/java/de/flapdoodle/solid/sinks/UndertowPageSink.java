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

import java.nio.ByteBuffer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.PageSink;
import de.flapdoodle.solid.generator.Binary;
import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.ImmutableDocument;
import de.flapdoodle.solid.generator.ImmutableText;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.site.SiteConfig;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class UndertowPageSink implements PageSink {

	private final Undertow server;
	private final String serverUrl;
	private ImmutableMap<String, Document> documentMap=ImmutableMap.of();

	public UndertowPageSink() {
		this.serverUrl = "http://localhost:8080";
		this.server = Undertow.builder()
			.addHttpListener(8080, "localhost")
			.setHandler(new HttpHandler() {
				
				@Override
				public void handleRequest(HttpServerExchange exchange) throws Exception {
					Document document = documentMap.get(exchange.getRequestPath());
					if (document!=null) {
						Content content = document.content();
						if (content instanceof Text) {
							exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, content.mimeType()+"; charset=UTF-8");
	            exchange.getResponseSender().send(((Text) content).text());
						} else {
							if (content instanceof Binary) {
								exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, content.mimeType());
		            exchange.getResponseSender().send(ByteBuffer.wrap(((Binary) content).data().data()));
							} else {
		            exchange.setStatusCode(500);
								exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		            exchange.getResponseSender().send("unknown content type: "+content.getClass());
							}
						}
						
					} else {
            exchange.setStatusCode(404);
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("page not found");
					}
				}
			})
			.build();
		
		this.server.start();
	}
	
	@Override
	public void accept(SiteConfig siteConfig, ImmutableList<Document> documents) {
		this.documentMap = documents.stream()
				.map(doc -> replaceBaseUrl(siteConfig.baseUrl(), this.serverUrl, doc))
				.collect(ImmutableMap.toImmutableMap(doc -> asValidUrl(doc.path()), doc -> doc));
	}

	private Document replaceBaseUrl(String baseUrl, String serverUrl, Document doc) {
		if (doc.content() instanceof Text) {
			Text text=(Text) doc.content();
			return ImmutableDocument.copyOf(doc).withContent(ImmutableText.copyOf(text).withText(text.text().replace(baseUrl, serverUrl)));
		}
		return doc;
	}

	private String asValidUrl(String path) {
		if (!path.startsWith("/")) {
			return "/"+path;
		}
		return path;
	}

}
