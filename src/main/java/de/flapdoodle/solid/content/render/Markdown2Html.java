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
package de.flapdoodle.solid.content.render;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.IndependentLinkResolverFactory;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.CoreNodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataHolder;
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
//		options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
		options.set(HtmlRenderer.ESCAPE_HTML_BLOCKS, true);
		
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
							return new ResolvedLink(link.getLinkType(), mappedUrl.get(), link.getTitle(), link.getStatus());
						}
						return link;
					}
				};
			}
		};
		options.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
		
		HtmlRenderer renderer = HtmlRenderer.builder(options)
				.nodeRendererFactory(o -> new CustomCoreNodeRenderer(o, renderContext))
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
	
	private static class CustomCoreNodeRenderer extends CoreNodeRenderer {

		private final RenderContext renderContext;

		public CustomCoreNodeRenderer(DataHolder options, RenderContext renderContext) {
			super(options);
			this.renderContext = renderContext;
		}
		
		@Override
		public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
			return withCustomHeading(super.getNodeRenderingHandlers());
		}

		private Set<NodeRenderingHandler<?>> withCustomHeading(Set<NodeRenderingHandler<?>> src) {
			Set<NodeRenderingHandler<?>> withoutHeadline = src.stream()
				.filter(h -> !(h.getNodeType() == Heading.class))
				.collect(Collectors.toSet());
			LinkedHashSet<NodeRenderingHandler<?>> ret = Sets.newLinkedHashSet(withoutHeadline);
			ret.add(new NodeRenderingHandler<Heading>(Heading.class, new CustomNodeRenderer<Heading>() {
                    @Override
                    public void render(Heading node, NodeRendererContext context, HtmlWriter html) {
                    	CustomCoreNodeRenderer.this.renderHeading(node, context, html);
                    }
                }));
			ret.add(new NodeRenderingHandler<BlockQuote>(BlockQuote.class, new CustomNodeRenderer<BlockQuote>() {
                    @Override
                    public void render(BlockQuote node, NodeRendererContext context, HtmlWriter html) {
                    	CustomCoreNodeRenderer.this.renderBlockQuote(node, context, html);
                    }
                }));
			return ret;
		}

		protected void renderBlockQuote(BlockQuote node, NodeRendererContext context, HtmlWriter html) {
    	html.withAttr().tagLineIndent("blockquote", new Runnable() {
        @Override
        public void run() {
//        	html.text(Escaping.escapeHtml(node.getChars(), false));
            context.renderChildren(node);
        }
    	});
		}

		private void renderHeading(final Heading node, final NodeRendererContext context, final HtmlWriter html) {
      if (context.getHtmlOptions().renderHeaderId) {
          String id = context.getNodeId(node);
          if (id != null) {
              html.attr("id", id);
          }
      }

      if (context.getHtmlOptions().sourcePositionParagraphLines) {
          html.srcPos(node.getChars()).withAttr().tagLine("h" + (node.getLevel()+renderContext.incrementHeading()), new Runnable() {
              @Override
              public void run() {
                  html.srcPos(node.getText()).withAttr().tag("span");
                  context.renderChildren(node);
                  html.tag("/span");
              }
          });
      } else {
          html.srcPos(node.getText()).withAttr().tagLine("h" + (node.getLevel()+renderContext.incrementHeading()), new Runnable() {
              @Override
              public void run() {
                  context.renderChildren(node);
              }
          });
      }
  }
	}
}
