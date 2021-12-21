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
package de.flapdoodle.solid.converter.wordpress;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.xml.Visitor;

/*
 * 	
	<item>
		<title>Modell-Referenzen</title>
		<link>http://www.wicket-praxis.de/blog/2009/01/03/modell-referenzen/</link>
		<pubDate>Sat, 03 Jan 2009 10:15:11 +0000</pubDate>
		<dc:creator><![CDATA[michael]]></dc:creator>
		<guid isPermaLink="false">http://www.wicket-praxis.de/blog/?p=10</guid>
		<description></description>
		<content:encoded><![CDATA[Wie zeige ich eine Liste von Elementen und die Länge der Liste mit Wicket an, ohne dass ich die Liste zweimal erzeugen muss?
		...
Schön kurz.]]></content:encoded>
		<excerpt:encoded><![CDATA[]]></excerpt:encoded>
		<wp:post_id>10</wp:post_id>
		<wp:post_date><![CDATA[2009-01-03 11:15:11]]></wp:post_date>
		<wp:post_date_gmt><![CDATA[2009-01-03 10:15:11]]></wp:post_date_gmt>
		<wp:comment_status><![CDATA[open]]></wp:comment_status>
		<wp:ping_status><![CDATA[open]]></wp:ping_status>
		<wp:post_name><![CDATA[modell-referenzen]]></wp:post_name>
		<wp:status><![CDATA[publish]]></wp:status>
		<wp:post_parent>0</wp:post_parent>
		<wp:menu_order>0</wp:menu_order>
		<wp:post_type><![CDATA[post]]></wp:post_type>
		<wp:post_password><![CDATA[]]></wp:post_password>
		<wp:is_sticky>0</wp:is_sticky>
		<category domain="post_tag" nicename="cascading"><![CDATA[cascading]]></category>
		<category domain="post_tag" nicename="detach"><![CDATA[detach]]></category>
		<category domain="category" nicename="wicket"><![CDATA[Wicket]]></category>
		...
		<wp:postmeta>
			<wp:meta_key><![CDATA[aktt_notify_twitter]]></wp:meta_key>
			<wp:meta_value><![CDATA[no]]></wp:meta_value>
		</wp:postmeta>
		<wp:comment>
			...
		</wp:comment>
	</item>

 */
@Immutable
public abstract class WpItem {
	public abstract String id();
	public abstract LocalDateTime date();
	
	public abstract String author();
	public abstract String title();
	public abstract String link();
	public abstract String urlName();
	public abstract String description();
	public abstract String content();
	public abstract String type();
	public abstract String status();
	public abstract Optional<String> excerpt();
	public abstract ImmutableMultimap<String,String> categories();
	public abstract ImmutableMultimap<String,String> meta();

	public static void read(Visitor visitor, Consumer<WpItem> categoryConsumer) {
		Preconditions.checkArgument(visitor.currentTagName().equals("item"),"not item");
		
		ImmutableWpItem.Builder builder=ImmutableWpItem.builder();
		visitor.visit(v -> properties().accept(v, builder));
		categoryConsumer.accept(builder.build());
	}

	private static VisitorConsumer<ImmutableWpItem.Builder> properties() {
		return VisitorConsumer.ofMap(ImmutableMap.<String, VisitorConsumer<ImmutableWpItem.Builder>>builder()
				.put("wp:post_id", (visitor, builder) -> builder.id(visitor.dataAsType(String.class)))
				.put("wp:post_date", (visitor, builder) -> builder.date(WordpressRssConverter.parseWpDate(visitor.dataAsType(String.class))))
				
				.put("dc:creator", (visitor, builder) -> builder.author(visitor.dataAsType(String.class)))
				.put("title", (visitor, builder) -> builder.title(visitor.dataAsType(String.class)))
				.put("link", (visitor, builder) -> builder.link(visitor.dataAsType(String.class)))
				.put("wp:post_name", (visitor, builder) -> builder.urlName(visitor.dataAsType(String.class)))
				.put("description", (visitor, builder) -> builder.description(visitor.dataAsType(String.class)))
				.put("content:encoded", (visitor, builder) -> builder.content(visitor.dataAsType(String.class)))
				.put("wp:post_type", (visitor, builder) -> builder.type(visitor.dataAsType(String.class)))
				.put("wp:status", (visitor, builder) -> builder.status(visitor.dataAsType(String.class)))
				.put("category", WpItem::category)
				.put("wp:postmeta", WpItem::postMeta)
				.build());
	}
	
	private static void postMeta(Visitor visitor, ImmutableWpItem.Builder builder) {
		AtomicReference<String> key=new AtomicReference<String>();
		AtomicReference<String> value=new AtomicReference<String>();
		
		visitor.visit(v -> {
			switch (v.currentTagName()) {
				case "wp:meta_key":
					key.set(v.dataAsType(String.class));
					break;
				case "wp:meta_value":
					value.set(v.dataAsType(String.class));
					break;
			}
		});
		
		if (key.get()!=null && value.get()!=null) {
			builder.putMeta(key.get(), value.get());
		}
	}
	
	private static void category(Visitor visitor, ImmutableWpItem.Builder builder) {
		String name = visitor.dataAsType(String.class);
		String domain = visitor.current().attribute("domain").getData().toString();
		builder.putCategories(domain, name);
	}
	
	@Lazy
	public boolean isPost() {
		return type().equals("post");
	}
	
	@Lazy
	public boolean isPage() {
		return type().equals("page");
	}
	
	/*
	<item>
		<title>Modell-Referenzen</title>
		<link>http://www.wicket-praxis.de/blog/2009/01/03/modell-referenzen/</link>
		<pubDate>Sat, 03 Jan 2009 10:15:11 +0000</pubDate>
		<dc:creator><![CDATA[michael]]></dc:creator>
		<guid isPermaLink="false">http://www.wicket-praxis.de/blog/?p=10</guid>
		<description></description>
		<content:encoded><![CDATA[Wie zeige ich eine Liste von Elementen und die Länge der Liste mit Wicket an, ohne dass ich die Liste zweimal erzeugen muss?

Für die Liste würde man ein LoadableDetachableModel benutzen und in load() das Ergebnis zurückliefern.
<pre lang="java">final LoadableDetachableModel<List<Something>> modelListe = new LoadableDetachableModel<List<Something>>()
{
  @Override
  protected List<Something> load()
  {
    return Something.asList();
  }
};</pre>
Für die Anzahl der Einträge würde ich jetzt auf dieses Modell zurückgreifen:
<pre lang="java">LoadableDetachableModel<Integer> modelAnzahl = new LoadableDetachableModel<Integer>()
{
  @Override
  protected Integer load()
  {
    return modelListe.getObject().size();
  }

  @Override
  public void detach()
  {
    modelListe.detach();
    super.detach();
  }
};</pre>
Wenn modelListe nicht mit einer Wicketkomponente verbunden wäre, würde detach für diese Modell nie aufgerufen und die Liste damit nicht neu erzeugt. Änderungen an der Liste würden sich nicht sofort in der Anzeige wiederspiegeln. Daher muss man in dem Modell, dass mit einer Wicketkomponente verbunden ist auch detach für alle referenzierten Modelle aufrufen. Es gibt eine einfachere Lösung:
<pre lang="java">public abstract class CascadingLoadableDetachableModel<M,P> extends LoadableDetachableModel<M>
{
  IModel<P> _parent;

  public CascadingLoadableDetachableModel(IModel<P> parent)
  {
    super();
    _parent=parent;
  }

  @Override
  public void detach()
  {
    super.detach();
    _parent.detach();
  }

  @Override
  protected M load()
  {
    return load(_parent.getObject());
  }

  protected abstract M load(P parentModelData);
}</pre>
Man übergibt die Modell-Referenz im Konstruktor, bekommt die Modell-Daten automatisch als Methodenparameter und detach wird auch automatisch aufgerufen:
<pre lang="java">CascadingLoadableDetachableModel<Integer,List<Something>> modelAnzahl = new CascadingLoadableDetachableModel<Integer,List<Something>>()
{
  @Override
  protected Integer load(List<Something> parentModelData)
  {
    return parentModelData.size();
  }
};</pre>
Schön kurz.]]></content:encoded>
		<excerpt:encoded><![CDATA[]]></excerpt:encoded>
		<wp:post_id>10</wp:post_id>
		<wp:post_date><![CDATA[2009-01-03 11:15:11]]></wp:post_date>
		<wp:post_date_gmt><![CDATA[2009-01-03 10:15:11]]></wp:post_date_gmt>
		<wp:comment_status><![CDATA[open]]></wp:comment_status>
		<wp:ping_status><![CDATA[open]]></wp:ping_status>
		<wp:post_name><![CDATA[modell-referenzen]]></wp:post_name>
		<wp:status><![CDATA[publish]]></wp:status>
		<wp:post_parent>0</wp:post_parent>
		<wp:menu_order>0</wp:menu_order>
		<wp:post_type><![CDATA[post]]></wp:post_type>
		<wp:post_password><![CDATA[]]></wp:post_password>
		<wp:is_sticky>0</wp:is_sticky>
		<category domain="post_tag" nicename="cascading"><![CDATA[cascading]]></category>
		<category domain="post_tag" nicename="detach"><![CDATA[detach]]></category>
		<category domain="post_tag" nicename="model"><![CDATA[model]]></category>
		<category domain="category" nicename="wicket"><![CDATA[Wicket]]></category>
		<category domain="post_tag" nicename="wicket"><![CDATA[Wicket]]></category>
		<wp:postmeta>
			<wp:meta_key><![CDATA[aktt_notify_twitter]]></wp:meta_key>
			<wp:meta_value><![CDATA[no]]></wp:meta_value>
		</wp:postmeta>
		<wp:postmeta>
			<wp:meta_key><![CDATA[_edit_last]]></wp:meta_key>
			<wp:meta_value><![CDATA[1]]></wp:meta_value>
		</wp:postmeta>
		<wp:postmeta>
			<wp:meta_key><![CDATA[aktt_tweeted]]></wp:meta_key>
			<wp:meta_value><![CDATA[1]]></wp:meta_value>
		</wp:postmeta>
		<wp:postmeta>
			<wp:meta_key><![CDATA[_wp_rp_related_posts_query_result_cache_expiration]]></wp:meta_key>
			<wp:meta_value><![CDATA[1386676722]]></wp:meta_value>
		</wp:postmeta>
		<wp:postmeta>
			<wp:meta_key><![CDATA[_wp_rp_related_posts_query_result_cache_3]]></wp:meta_key>
			<wp:meta_value><![CDATA[a:6:{i:0;O:8:"stdClass":2:{s:7:"post_id";s:3:"175";s:5:"score";s:18:"52.301910890771154";}i:1;O:8:"stdClass":2:{s:7:"post_id";s:3:"143";s:5:"score";s:17:"50.36822486917605";}i:2;O:8:"stdClass":2:{s:7:"post_id";s:3:"192";s:5:"score";s:16:"32.6280997587056";}i:3;O:8:"stdClass":2:{s:7:"post_id";s:3:"166";s:5:"score";s:17:"25.65902079132514";}i:4;O:8:"stdClass":2:{s:7:"post_id";s:2:"21";s:5:"score";s:17:"9.668430107975498";}i:5;O:8:"stdClass":2:{s:7:"post_id";s:2:"53";s:5:"score";s:17:"7.111092908562556";}}]]></wp:meta_value>
		</wp:postmeta>
		<wp:comment>
			<wp:comment_id>3</wp:comment_id>
			<wp:comment_author><![CDATA[@wicketpraxis]]></wp:comment_author>
			<wp:comment_author_email><![CDATA[wicketpraxis@tweetback]]></wp:comment_author_email>
			<wp:comment_author_url>http://twitter.com/wicketpraxis/statuses/1093212329</wp:comment_author_url>
			<wp:comment_author_IP><![CDATA[]]></wp:comment_author_IP>
			<wp:comment_date><![CDATA[2009-01-03 11:07:47]]></wp:comment_date>
			<wp:comment_date_gmt><![CDATA[2009-01-03 10:07:47]]></wp:comment_date_gmt>
			<wp:comment_content><![CDATA[New blog post: Modell-Referenzen http://tinyurl.com/6ufou7]]></wp:comment_content>
			<wp:comment_approved><![CDATA[1]]></wp:comment_approved>
			<wp:comment_type><![CDATA[]]></wp:comment_type>
			<wp:comment_parent>0</wp:comment_parent>
			<wp:comment_user_id>0</wp:comment_user_id>
		</wp:comment>
		<wp:comment>
			<wp:comment_id>715</wp:comment_id>
			<wp:comment_author><![CDATA[Migration zu Wicket: Model | wicket praxis]]></wp:comment_author>
			<wp:comment_author_email><![CDATA[]]></wp:comment_author_email>
			<wp:comment_author_url>http://www.wicket-praxis.de/blog/2009/09/22/migration-zu-wicket-model/</wp:comment_author_url>
			<wp:comment_author_IP><![CDATA[88.198.25.105]]></wp:comment_author_IP>
			<wp:comment_date><![CDATA[2009-09-22 08:48:58]]></wp:comment_date>
			<wp:comment_date_gmt><![CDATA[2009-09-22 07:48:58]]></wp:comment_date_gmt>
			<wp:comment_content><![CDATA[[...] Die Beispielliste ist eine Liste von Posten auf einer Rechnung. Wir möchten aber zum Betrag auch noch den MwSt-Anteil ausweisen. Dazu schreiben wir ein allgemeines Model, dass von einer Erweiterung des LoadableDetachableModel abgeleitet ist: CascadingLoadableDetachableModel. [...]]]></wp:comment_content>
			<wp:comment_approved><![CDATA[1]]></wp:comment_approved>
			<wp:comment_type><![CDATA[pingback]]></wp:comment_type>
			<wp:comment_parent>0</wp:comment_parent>
			<wp:comment_user_id>0</wp:comment_user_id>
		</wp:comment>
		<wp:comment>
			<wp:comment_id>871</wp:comment_id>
			<wp:comment_author><![CDATA[Wicket Model Transformation | wicket praxis]]></wp:comment_author>
			<wp:comment_author_email><![CDATA[]]></wp:comment_author_email>
			<wp:comment_author_url>http://www.wicket-praxis.de/blog/2009/10/28/wicket-model-transformation/</wp:comment_author_url>
			<wp:comment_author_IP><![CDATA[88.198.25.105]]></wp:comment_author_IP>
			<wp:comment_date><![CDATA[2009-10-28 07:17:50]]></wp:comment_date>
			<wp:comment_date_gmt><![CDATA[2009-10-28 06:17:50]]></wp:comment_date_gmt>
			<wp:comment_content><![CDATA[[...] auf die bereits geladenen Daten aus dem Model zurückzugreifen. Für diesen Zweck kann man auf eine spezialisierte Model-Klasse zurückgreifen, die sich darum kümmert, das die detach()-Methode auch für alle referenzierten [...]]]></wp:comment_content>
			<wp:comment_approved><![CDATA[1]]></wp:comment_approved>
			<wp:comment_type><![CDATA[pingback]]></wp:comment_type>
			<wp:comment_parent>0</wp:comment_parent>
			<wp:comment_user_id>0</wp:comment_user_id>
		</wp:comment>
	</item>

	 */
}
