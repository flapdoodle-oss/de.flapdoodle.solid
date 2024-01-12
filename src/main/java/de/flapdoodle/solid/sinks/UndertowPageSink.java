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
package de.flapdoodle.solid.sinks;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
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
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.Pair;
import de.flapdoodle.types.Try;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class UndertowPageSink implements PageSink {

	private final Undertow server;
	private final String serverUrl;
//	private ImmutableMap<String, Document> documentMap=ImmutableMap.of();
	private final AtomicReference<DocumentSet> documents=new AtomicReference<>();

	public UndertowPageSink() {
		this.serverUrl = "http://localhost:8080";
		this.server = Undertow.builder()
			.addHttpListener(8080, "localhost")
			.setHandler(new HttpHandler() {
				
				@Override
				public void handleRequest(HttpServerExchange exchange) throws Exception {
					String requestPath = exchange.getRequestPath();
					
					DocumentSet docSet = documents.get();
					if (docSet!=null) {
						if (requestPath.equals("/")) {
							if (!docSet.basePath.equals(requestPath)) {
								System.out.println("Roooooooooooooooooooooot -> "+docSet.basePath);
		            exchange.setStatusCode(302);
								exchange.getResponseHeaders().put(Headers.LOCATION, docSet.basePath);
		            exchange.getResponseSender().send("redirect to basePath");
							}
						} else {
							ImmutableMap<String, Document> documentMap = docSet.documentMap;
						
							Document document = Maybe.ofNullable(documentMap.get(requestPath))
									.or(() -> Maybe.ofNullable(documentMap.get(requestPath+"/")))
									.orElseNull();
							
							if (document!=null) {
								Content content = document.content();
								if (content instanceof Text) {
									exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, content.mimeType()+"; charset=UTF-8");
			            exchange.getResponseSender().send(((Text) content).text());//
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
		            pageNotFound(exchange, documentMap);
							}
						}
					} else {
            exchange.setStatusCode(404);
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("no content at all");
					}
				}

				private void pageNotFound(HttpServerExchange exchange, ImmutableMap<String, Document> documentMap) {
					exchange.setStatusCode(404);
					exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
					exchange.getResponseSender().send(pageNotFoundMessage(documentMap));
				}

				private String pageNotFoundMessage(ImmutableMap<String, Document> documentMap) {
					StringBuilder sb=new StringBuilder();
					sb.append("<html><head><title>Page Not Found</title></head><body>");
					sb.append("<h1>Page Not Found</h1>");
					documentMap.keySet().forEach(d -> {
						sb.append("<a href=\"").append(d).append("\">").append(d).append("</a><br>\n");
					});
					sb.append("</body></html>");
					return sb.toString();
				}
			})
			.build();
		
		this.server.start();
	}
	
	@Override
	public void accept(SiteConfig siteConfig, ImmutableList<Document> documents) {
//		this.documentMap = documents.stream()
//				.map(doc -> replaceBaseUrl(siteConfig.baseUrl(), this.serverUrl, doc))
//				.collect(ImmutableMap.toImmutableMap(doc -> asValidUrl(doc.path()), doc -> doc));
		
		Pair<String, String> hostAndBasePath = hostAndBasePath(siteConfig.baseUrl());
		
		this.documents.set(new DocumentSet(siteConfig, hostAndBasePath.b(), documents.stream()
			.map(doc -> replaceBaseUrl(hostAndBasePath.a(), this.serverUrl, doc))
			.collect(ImmutableMap.toImmutableMap(doc -> asValidUrl(doc.path()), doc -> doc))));
	}

	private String rewriteUrl(String basePath, String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@VisibleForTesting
	protected static Pair<String, String> hostAndBasePath(String baseUrl) {
		URL asUrl = Try.supplier(() -> new URL(baseUrl))
			.mapToUncheckedException(RuntimeException::new)
			.get();
		
		String basePath = asUrl.getPath();
		int indexOfPath = baseUrl.indexOf(basePath);
		Preconditions.checkArgument(indexOfPath!=1,"could not find %s in %s",basePath, baseUrl);
		Preconditions.checkArgument(baseUrl.length()==indexOfPath+basePath.length(),"basepath %s is not the last thing in %s",basePath, baseUrl);
		if (!basePath.endsWith("/")) {
			basePath=basePath+"/";
		}
		return Pair.of(baseUrl.substring(0, indexOfPath), basePath);
	}

	private Document replaceBaseUrl(String baseUrl, String serverUrl, Document doc) {
		doc =ImmutableDocument.copyOf(doc)
				.withPath(doc.path().replace(baseUrl, ""));
		
		if (doc.content() instanceof Text) {
			Text text=(Text) doc.content();
			return ImmutableDocument.copyOf(doc)
					.withContent(ImmutableText.copyOf(text)
					.withText(text.text().replace(baseUrl, serverUrl)));
		}
		return doc;
	}
	
	private String asValidUrl(String path) {
		if (!path.startsWith("/")) {
			return "/"+path;
		}
		return path;
	}
	
	private static class DocumentSet {
		
		private final SiteConfig siteConfig;
		private final ImmutableMap<String, Document> documentMap;
		private final String basePath;

		public DocumentSet(SiteConfig siteConfig, String basePath, ImmutableMap<String, Document> documentMap) {
			this.siteConfig = siteConfig;
			this.basePath = basePath;
			this.documentMap = documentMap;
		}
	}

}
