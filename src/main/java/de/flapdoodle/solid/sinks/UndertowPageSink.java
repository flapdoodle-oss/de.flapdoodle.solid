package de.flapdoodle.solid.sinks;

import java.nio.ByteBuffer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.PageSink;
import de.flapdoodle.solid.generator.Binary;
import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class UndertowPageSink implements PageSink {

	private final Undertow server;
	private ImmutableMap<String, Document> documentMap=ImmutableMap.of();

	public UndertowPageSink() {
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
	public void accept(ImmutableList<Document> documents) {
		this.documentMap = documents.stream()
				.collect(ImmutableMap.toImmutableMap(doc -> asValidUrl(doc.path()), doc -> doc));
	}

	private String asValidUrl(String path) {
		if (!path.startsWith("/")) {
			return "/"+path;
		}
		return path;
	}

}
