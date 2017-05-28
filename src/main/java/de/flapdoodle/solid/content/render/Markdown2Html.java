package de.flapdoodle.solid.content.render;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentLinkResolverFactory;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.CoreNodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

import de.flapdoodle.solid.types.Maybe;

public class Markdown2Html implements MarkupRenderer {
	
	private final Parser parser;

	public Markdown2Html() {
		MutableDataSet options = new MutableDataSet();

    // uncomment to set optional extensions
    //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

    // uncomment to convert soft-breaks to hard breaks
    //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
		
		this.parser = Parser.builder(options).build();
	}

	private static HtmlRenderer renderer(RenderContext renderContext) {
		MutableDataSet options = new MutableDataSet();
		LinkResolverFactory linkResolverFactory=new IndependentLinkResolverFactory() {
			
			@Override
			public LinkResolver create(NodeRendererContext context) {
				return new LinkResolver() {
					
					@Override
					public ResolvedLink resolveLink(Node node, NodeRendererContext context, ResolvedLink link) {
						Maybe<String> mappedUrl = renderContext.urlMapping().apply(link.getUrl());
						if (mappedUrl.isPresent()) {
							return new ResolvedLink(link.getLinkType(), mappedUrl.get(), link.getStatus());
						}
						return link;
					}
				};
			}
		};
		HtmlRenderer renderer = HtmlRenderer.builder(options)
				.nodeRendererFactory(o -> new CoreNodeRenderer(o))
    		.linkResolverFactory(linkResolverFactory)
    		.indentSize(2)
    		.build();
		
		return renderer;
	}

	@Override
	public String asHtml(RenderContext context, String markdown) {
		Document document = (Document) parser.parse(markdown);
    return renderer(context).render(document);
	}
}
