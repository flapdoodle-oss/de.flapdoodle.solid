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
package de.flapdoodle.solid.converter.wordpress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import de.flapdoodle.solid.converter.segments.Replacement;

public class Html2MarkdownTest {

	@Test
	public void codeMatcherMustMatch() {
		Optional<Replacement> replacement = Html2Markdown.codeMatcher("pre", "lang").find("  <pre lang=\"java\">java code</pre>", 0);
		assertTrue(replacement.isPresent());
		assertEquals("\n```java\njava code\n```\n\n",replacement.get().content());
	}
	
	@Test
	public void partialHtmlSample() {
		String src="Soeben habe ich eine Migration auf Wicket 1.4-rc2 durchgeführt. Ausschlaggebend für die Migration war die Überarbeitung im Model-Bereich. Typisierte Modelle sind sehr viel handlicher und machen den Code sehr viel lesbarer. Da man die Komponenten ja auch schon generisch angelegt hat, ging diese Information bisher in den Modellen verloren.\n" + 
				"\n" + 
				"Interessante Statistik:\n" + 
				"<ul>\n" + 
				"	<li>Anzahl der Java-Dateien im Webprojekt: 376</li>\n" + 
				"	<li>Anzahl der Java-Dateien, die angepasst werden mussten: 32</li>\n" + 
				"	<li>Zeitaufwand für die Umstellung: 1h,30min</li>\n" + 
				"</ul>\n" + 
				"Ich glaube, das Risiko ist gering und der Vorteil ist groß. Meine Empfehlung: Umsteigen.";
		String result = Html2Markdown.newInstance().convert(src);
		
		assertEquals("Soeben habe ich eine Migration auf Wicket 1.4-rc2 durchgeführt. Ausschlaggebend für die Migration war die Überarbeitung im Model-Bereich. Typisierte Modelle sind sehr viel handlicher und machen den Code sehr viel lesbarer. Da man die Komponenten ja auch schon generisch angelegt hat, ging diese Information bisher in den Modellen verloren.\n" + 
				"\n" + 
				"Interessante Statistik:\n" + 
				"* Anzahl der Java-Dateien im Webprojekt: 376\n" + 
				"* Anzahl der Java-Dateien, die angepasst werden mussten: 32\n" + 
				"* Zeitaufwand für die Umstellung: 1h,30min\n" + 
				"\n" + 
				"Ich glaube, das Risiko ist gering und der Vorteil ist groß. Meine Empfehlung: Umsteigen.\n" + 
				"\n" + 
				"", result);
	}
	
	@Test
	public void sample() {
		String src="Interessante Statistik:\n" + 
				"<ul>\n" + 
				"	<li>Anzahl der Java-Dateien im Webprojekt: 376</li>\n" + 
				"	<li>Anzahl der Java-Dateien, die angepasst werden mussten: 32</li>\n" + 
				"	<li>Zeitaufwand für die Umstellung: 1h,30min</li>\n" + 
				"</ul>\n" + 
				"Ich glaube, das Risiko ist gering und der Vorteil ist groß. Meine Empfehlung: Umsteigen.";
		
		String result = Html2Markdown.newInstance().convert(src);
		
		assertEquals("Interessante Statistik:\n" + 
				"* Anzahl der Java-Dateien im Webprojekt: 376\n" + 
				"* Anzahl der Java-Dateien, die angepasst werden mussten: 32\n" + 
				"* Zeitaufwand für die Umstellung: 1h,30min\n" + 
				"\n" + 
				"Ich glaube, das Risiko ist gering und der Vorteil ist groß. Meine Empfehlung: Umsteigen.\n" + 
				"\n" + 
				"", result);
	}
	
	@Test
	@Ignore
	public void sample2() {
		String src="Mit Wicket ist es sehr einfach, Webanwendungen zu schreiben. Wenn man dem Wicket-Pfad folgt und keine besonderen Wünsche hat. Im folgenden besteht der Wunsch darin, den Zustand einer Seite in Seitenparametern abzulegen. Wenn durch einen Link, also eine Aktion, die der Nutzer wählen kann, nur ein Parameter verändert wird, sollen natürlich alle anderen Parameter unverändert weitergereicht werden.\n" + 
				"\n" + 
				"Zuerst erstellen wir uns ein paar Hilfsklassen, welche die Handhabung wesentlich vereinfachen werden. Die Idee ist dabei folgende: die Parameter, die mit dem Request übergeben werden, werden auf Attribute einer JavaBean gemappt und mit Wicket-Bordmitteln in den entsprechenden Datentyp konvertiert. Aus der JavaBean kann man dann die gewünschten Werte auslesen. Die Attribute der JavaBean können dann neu gesetzt werden. Ein weiteres Mal wird mit Wicket-Bordmitteln aus den Attributen der JavaBean eine Liste von Parametern gewonnen, die dann (fast) direkt in einem Link benutzt werden können.\n" + 
				"\n" + 
				"Zuerst erstellen wir uns eine Annotation, mit der wir die Attribute markieren, die von und in PageParameter umgewandelt werden sollen.\n" + 
				"<pre lang=\"java\">import java.lang.annotation.ElementType;\n" + 
				"import java.lang.annotation.Retention;\n" + 
				"import java.lang.annotation.RetentionPolicy;\n" + 
				"import java.lang.annotation.Target;\n" + 
				"\n" + 
				"@Retention(RetentionPolicy.RUNTIME)\n" + 
				"@Target(ElementType.METHOD)\n" + 
				"public @interface PublicProperty\n" + 
				"{	\n" + 
				"}</pre>\n" + 
				"\n" + 
				"Als nächstes erstellen wir uns ein Interface, was von der JavaBean implementiert werden muss. Die Aufgabe des Interface liegt darin, sicherzustellen, dass von der JavaBean eine Kopie angefertigt werden kann, damit die Änderungen an der Kopie und nicht am Original vorgenommen werden.\n" + 
				"\n" + 
				"<pre lang=\"java\">import org.apache.wicket.IClusterable;\n" + 
				"\n" + 
				"public interface PageStateBeanInterface<T extends PageStateBeanInterface<?>> extends IClusterable\n" + 
				"{\n" + 
				"	public T getClone();\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Jetzt kommt der aufwendigste Teil. Wir schreiben uns eine Hilfsklasse, welche die Transformation von und in PageParameter durchführt.\n" + 
				"\n" + 
				"<pre lang=\"java\">import java.lang.reflect.Method;\n" + 
				"import java.util.ArrayList;\n" + 
				"import java.util.HashMap;\n" + 
				"import java.util.List;\n" + 
				"import java.util.Locale;\n" + 
				"import java.util.Map;\n" + 
				"\n" + 
				"import org.apache.wicket.Application;\n" + 
				"import org.apache.wicket.IConverterLocator;\n" + 
				"import org.apache.wicket.PageParameters;\n" + 
				"import org.apache.wicket.Session;\n" + 
				"import org.apache.wicket.model.PropertyModel;\n" + 
				"import org.apache.wicket.util.convert.IConverter;\n" + 
				"\n" + 
				"public class BeanPagePropertyUtil\n" + 
				"{\n" + 
				"	public static <B> PageParameters getBeanPageParameters(B bean)\n" + 
				"	{\n" + 
				"		return new PageParameters(getParameter(bean));\n" + 
				"	}\n" + 
				"	\n" + 
				"	public static <B> PageParameters getBeanPageParameters(B bean,B defaults)\n" + 
				"	{\n" + 
				"		Map<String, Object> beanParameter = getParameter(bean);\n" + 
				"		Map<String, Object> defaultParameter = getParameter(defaults);\n" + 
				"		for (String s : defaultParameter.keySet())\n" + 
				"		{\n" + 
				"			Object defaultValue = defaultParameter.get(s);\n" + 
				"			if (defaultValue!=null)\n" + 
				"			{\n" + 
				"				Object curValue = beanParameter.get(s);\n" + 
				"				if (defaultValue.equals(curValue))\n" + 
				"				{\n" + 
				"					beanParameter.remove(s);\n" + 
				"				}\n" + 
				"			}\n" + 
				"		}\n" + 
				"		return new PageParameters(beanParameter);\n" + 
				"	}\n" + 
				"	\n" + 
				"	protected static <B> List<String> getPublicProperties(B bean)\n" + 
				"	{\n" + 
				"		List<String> ret=new ArrayList<String>();\n" + 
				"		\n" + 
				"		Method[] methods = bean.getClass().getMethods();\n" + 
				"		for (Method m : methods)\n" + 
				"		{\n" + 
				"			PublicProperty annotation = m.getAnnotation(PublicProperty.class);\n" + 
				"			if (annotation!=null)\n" + 
				"			{\n" + 
				"				String name = m.getName();\n" + 
				"				if (name.startsWith(\"get\")) ret.add(name.substring(3));\n" + 
				"				else\n" + 
				"				{\n" + 
				"					if (name.startsWith(\"is\")) ret.add(name.substring(2));\n" + 
				"				}\n" + 
				"			}\n" + 
				"		}\n" + 
				"		\n" + 
				"		return ret;\n" + 
				"	}\n" + 
				"	\n" + 
				"	public static <B> Map<String,Object> getParameter(B bean)\n" + 
				"	{\n" + 
				"		Map<String,Object> ret=new HashMap<String, Object>();\n" + 
				"		\n" + 
				"		Locale locale = Session.get().getLocale();\n" + 
				"		IConverterLocator converterLocator = Application.get().getConverterLocator();\n" + 
				"		\n" + 
				"		for (String s : getPublicProperties(bean))\n" + 
				"		{\n" + 
				"			PropertyModel<?> propertyModel = new PropertyModel(bean,s);\n" + 
				"			IConverter converter = converterLocator.getConverter(propertyModel.getObjectClass());\n" + 
				"			Object value = propertyModel.getObject();\n" + 
				"			if (value!=null)\n" + 
				"			{\n" + 
				"				ret.put(s, converter.convertToString(value, locale));\n" + 
				"			}\n" + 
				"		}\n" + 
				"		return ret;\n" + 
				"	}\n" + 
				"\n" + 
				"	public static <B> void setParameter(B bean,PageParameters pageParameters)\n" + 
				"	{\n" + 
				"		Locale locale = Session.get().getLocale();\n" + 
				"		IConverterLocator converterLocator = Application.get().getConverterLocator();\n" + 
				"		\n" + 
				"		for (String s : getPublicProperties(bean))\n" + 
				"		{\n" + 
				"			PropertyModel<Object> propertyModel = new PropertyModel<Object>(bean,s);\n" + 
				"			IConverter converter = converterLocator.getConverter(propertyModel.getObjectClass());\n" + 
				"			String svalue = pageParameters.getString(s);\n" + 
				"			if (svalue!=null)\n" + 
				"			{\n" + 
				"				propertyModel.setObject(converter.convertToObject(svalue, locale));\n" + 
				"			}\n" + 
				"			else\n" + 
				"			{\n" + 
				"				propertyModel.setObject(null);\n" + 
				"			}\n" + 
				"		}\n" + 
				"	}\n" + 
				"\n" + 
				"	public static <B> void setParameter(B bean,Map<String,?> parameter)\n" + 
				"	{\n" + 
				"		for (String s : getPublicProperties(bean))\n" + 
				"		{\n" + 
				"			if (parameter.containsKey(s))\n" + 
				"			{\n" + 
				"				PropertyModel<Object> propertyModel = new PropertyModel<Object>(bean,s);\n" + 
				"				Object value=parameter.get(s);\n" + 
				"				propertyModel.setObject(value);\n" + 
				"			}\n" + 
				"		}\n" + 
				"	}\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Es ist wichtig, darauf hinzuweisen, dass es vorgesehen ist,  eine mit Standardwerten initialisierte JavaBean als Abgleich zu benutzen. So kann sichergestellt werden, dass Seitenparameter dann aus der Url entfernt werden, wenn der Wert dem Standardwert entspricht. Das reduziert die Gefahr für Double-Content-Probleme wesentlich.\n" + 
				"\n" + 
				"Jetzt haben wir alles zusammen, um  Seitenparameter in JavaBean-Attribute zu überführen und zurück wandeln zu können. Als nächstes benötigen wir noch ein paar Komponenten, die den Prozess der Parameterlistenerstellung und der Konvertierung für uns übernehmen. Dazu fügen wir der Seite eine Komponente hinzu, die folgendes Interface implementiert:\n" + 
				"\n" + 
				"<pre lang=\"java\">public interface PageStateInterface<B extends PageStateBeanInterface<B>>\n" + 
				"{\n" + 
				"	public B getState();\n" + 
				"	public B getDefaults();\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Die Komponente konvertiert die Seitenparameter in die Attribute und stellt über die Schnittstelle die beiden Zustände zur Verfügung. Damit andere Komponenten auf diese Werte zugreifen können, erstellen wir gleichzeitig eine Funktion, welche die Komponente im Komponentenbaum sucht.\n" + 
				"\n" + 
				"<pre lang=\"java\">import org.apache.wicket.Component;\n" + 
				"import org.apache.wicket.Page;\n" + 
				"import org.apache.wicket.PageParameters;\n" + 
				"import org.apache.wicket.markup.html.panel.Panel;\n" + 
				"\n" + 
				"public class PageContext<B extends PageStateBeanInterface<B>> extends Panel implements PageStateInterface<B>\n" + 
				"{\n" + 
				"	private static final Logger _logger=LoggerFactory.getLogger(PageContext.class);\n" + 
				"\n" + 
				"	B _defaults;\n" + 
				"\n" + 
				"	B _state;\n" + 
				"\n" + 
				"	public PageContext(String id, PageParameters pageParameters, B defaults)\n" + 
				"	{\n" + 
				"		super(id);\n" + 
				"\n" + 
				"		_defaults=defaults;\n" + 
				"		_state=_defaults.getClone();\n" + 
				"		BeanPagePropertyUtil.setParameter(_state, pageParameters);\n" + 
				"	}\n" + 
				"\n" + 
				"	public B getDefaults()\n" + 
				"	{\n" + 
				"		return _defaults;\n" + 
				"	}\n" + 
				"\n" + 
				"	public B getState()\n" + 
				"	{\n" + 
				"		return _state.getClone();\n" + 
				"	}\n" + 
				"\n" + 
				"	public static <B extends PageStateBeanInterface<B>> PageStateInterface<B> getPageState(Page page, Class<? extends B> type)\n" + 
				"	{\n" + 
				"		NodeVisitor visitor=new NodeVisitor(type);\n" + 
				"		page.visitChildren(PageContext.class, visitor);\n" + 
				"		return visitor.getPageState();\n" + 
				"	}\n" + 
				"\n" + 
				"	static class NodeVisitor<B extends PageStateBeanInterface<B>> implements IVisitor<Component>\n" + 
				"	{\n" + 
				"		Class<B> _type;\n" + 
				"\n" + 
				"		PageStateInterface<B> _pageState;\n" + 
				"\n" + 
				"		public NodeVisitor(Class<B> type)\n" + 
				"		{\n" + 
				"			_type=type;\n" + 
				"		}\n" + 
				"\n" + 
				"		public PageStateInterface<B> getPageState()\n" + 
				"		{\n" + 
				"			return _pageState;\n" + 
				"		}\n" + 
				"\n" + 
				"		public Object component(Component component)\n" + 
				"		{\n" + 
				"			if (component instanceof PageContext)\n" + 
				"			{\n" + 
				"				PageContext rawContext=(PageContext) component;\n" + 
				"				if (_type.isAssignableFrom(rawContext.getDefaults().getClass()))\n" + 
				"				{\n" + 
				"					_pageState=rawContext;\n" + 
				"					return IVisitor.STOP_TRAVERSAL;\n" + 
				"				}\n" + 
				"			}\n" + 
				"			return IVisitor.CONTINUE_TRAVERSAL;\n" + 
				"		}\n" + 
				"	}\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Des weiteren erstellen wir eine Linkklasse, die sich um das setzen der richtigen Seitenparameter kümmert.\n" + 
				"\n" + 
				"<pre lang=\"java\">import java.util.Map;\n" + 
				"\n" + 
				"import org.apache.wicket.Page;\n" + 
				"import org.apache.wicket.PageParameters;\n" + 
				"import org.apache.wicket.markup.html.link.BookmarkablePageLink;\n" + 
				"import org.apache.wicket.util.collections.MiniMap;\n" + 
				"\n" + 
				"public class PageStateLink<P extends Page, B extends PageStateBeanInterface<B>> extends BookmarkablePageLink<P>\n" + 
				"{\n" + 
				"	Map<String, ?> _linkParameter;\n" + 
				"\n" + 
				"	Class<B> _beanType;\n" + 
				"\n" + 
				"	public PageStateLink(String id, Class<P> pageClass, Class<B> beanType, Map<String, ?> linkParameter)\n" + 
				"	{\n" + 
				"		super(id, pageClass);\n" + 
				"		_beanType=beanType;\n" + 
				"		_linkParameter=linkParameter;\n" + 
				"		if (_linkParameter == null) _linkParameter=new HashMap<String,?>();\n" + 
				"	}\n" + 
				"\n" + 
				"	public PageStateLink(String id, Class<P> pageClass, Class<B> beanType)\n" + 
				"	{\n" + 
				"		this(id, pageClass, beanType, null);\n" + 
				"	}\n" + 
				"\n" + 
				"	@Override\n" + 
				"	protected void onBeforeRender()\n" + 
				"	{\n" + 
				"		PageStateInterface<B> pageState=PageContext.getPageState(getPage(), _beanType);\n" + 
				"		if (pageState != null)\n" + 
				"		{\n" + 
				"			B bean=pageState.getState();\n" + 
				"			B defaults=pageState.getDefaults();\n" + 
				"			BeanPagePropertyUtil.setParameter(bean, _linkParameter);\n" + 
				"			onAfterSetParameter(bean);\n" + 
				"			PageParameters beanPageParameters=BeanPagePropertyUtil.getBeanPageParameters(bean, defaults);\n" + 
				"			applyPageParameter(beanPageParameters);\n" + 
				"		}\n" + 
				"		super.onBeforeRender();\n" + 
				"	}\n" + 
				"\n" + 
				"	protected void onAfterSetParameter(B bean)\n" + 
				"	{\n" + 
				"\n" + 
				"	}\n" + 
				"\n" + 
				"	private void applyPageParameter(PageParameters pageParameters)\n" + 
				"	{\n" + 
				"		this.parameters=pageParametersToMiniMap(pageParameters);\n" + 
				"	}\n" + 
				"\n" + 
				"	private MiniMap<String, Object> pageParametersToMiniMap(PageParameters parameters)\n" + 
				"	{\n" + 
				"		if (parameters != null)\n" + 
				"		{\n" + 
				"			MiniMap<String, Object> map=new MiniMap<String, Object>(parameters, parameters.keySet().size());\n" + 
				"			return map;\n" + 
				"		}\n" + 
				"		else\n" + 
				"		{\n" + 
				"			return null;\n" + 
				"		}\n" + 
				"\n" + 
				"	}\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Die Methode <em>applyPageParameter</em> wurde nur aus Geschwindigkeitsgründen erstellt, da sonst jeder Aufruf von <em>setParameter</em> dazu führt, dass die interne Map kopiert wird.\n" + 
				"\n" + 
				"Um auf die JavaBean über ein Modell zuzugreifen, schreiben wir uns noch eine Modellklasse, bevor wir uns dann ansehen, wie man die Klassen dann benutzt.\n" + 
				"\n" + 
				"<pre lang=\"java\">import org.apache.wicket.Component;\n" + 
				"import org.apache.wicket.model.LoadableDetachableModel;\n" + 
				"\n" + 
				"public class PageStateModel<B extends PageStateBeanInterface<B>> extends LoadableDetachableModel<B>\n" + 
				"{\n" + 
				"	Component _component;\n" + 
				"	Class<? extends B> _type;\n" + 
				"	\n" + 
				"	public PageStateModel(Component component, Class<? extends B> type)\n" + 
				"  {\n" + 
				"		_component=component;\n" + 
				"		_type=type;\n" + 
				"  }\n" + 
				"	\n" + 
				"	@Override\n" + 
				"	protected B load()\n" + 
				"	{\n" + 
				"	  PageStateInterface<B> pageState=PageContext.getPageState(_component.getPage(), _type);\n" + 
				"	  if (pageState!=null)\n" + 
				"	  {\n" + 
				"	  	return pageState.getState();\n" + 
				"	  }\n" + 
				"		return null;\n" + 
				"	}\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Ok. Das war ganz schön aufwendig, aber dafür ist die Verwendung um so einfacher. Wir erstellen eine JavaBean und eine Seite, auf der wir dann die Komponenten einbinden.\n" + 
				"\n" + 
				"<pre lang=\"java\">public class ConfigBean implements PageStateBeanInterface<ConfigBean>\n" + 
				"{\n" + 
				"	Integer _start;\n" + 
				"	Integer _stop;\n" + 
				"	String _name;\n" + 
				"\n" + 
				"	@PublicProperty\n" + 
				"	public Integer getStart()\n" + 
				"	{\n" + 
				"		return _start;\n" + 
				"	}\n" + 
				"\n" + 
				"	public void setStart(Integer start)\n" + 
				"	{\n" + 
				"		_start=start;\n" + 
				"	}\n" + 
				"\n" + 
				"	@PublicProperty\n" + 
				"	public Integer getStop()\n" + 
				"	{\n" + 
				"		return _stop;\n" + 
				"	}\n" + 
				"\n" + 
				"	public void setStop(Integer stop)\n" + 
				"	{\n" + 
				"		_stop=stop;\n" + 
				"	}\n" + 
				"\n" + 
				"	@PublicProperty\n" + 
				"	public String getName()\n" + 
				"	{\n" + 
				"		return _name;\n" + 
				"	}\n" + 
				"\n" + 
				"	public void setName(String name)\n" + 
				"	{\n" + 
				"		_name=name;\n" + 
				"	}\n" + 
				"\n" + 
				"	public ConfigBean getClone()\n" + 
				"	{\n" + 
				"		ConfigBean ret=new ConfigBean();\n" + 
				"		ret._name=_name;\n" + 
				"		ret._start=_start;\n" + 
				"		ret._stop=_stop;\n" + 
				"		return ret;\n" + 
				"	}\n" + 
				"}</pre>\n" + 
				"\n" + 
				"<pre lang=\"java\">import org.apache.wicket.PageParameters;\n" + 
				"import org.apache.wicket.markup.html.WebPage;\n" + 
				"import org.apache.wicket.markup.html.basic.Label;\n" + 
				"import org.apache.wicket.markup.html.panel.Panel;\n" + 
				"import org.apache.wicket.model.IModel;\n" + 
				"import org.apache.wicket.model.PropertyModel;\n" + 
				"\n" + 
				"public class TestPage extends WebPage\n" + 
				"{\n" + 
				"	IModel<ConfigBean> _config=new PageStateModel<ConfigBean>(this, ConfigBean.class);\n" + 
				"\n" + 
				"	public StatelessTestPage(PageParameters pageParameters)\n" + 
				"	{\n" + 
				"		add(new PageContext<ConfigBean>(\"context\", pageParameters, new ConfigBean()));\n" + 
				"\n" + 
				"		add(new Label(\"start\", new PropertyModel<Integer>(_config, \"start\")));\n" + 
				"		add(new Label(\"stop\", new PropertyModel<Integer>(_config, \"stop\")));\n" + 
				"		add(new SubPanel(\"sub\"));\n" + 
				"	}\n" + 
				"\n" + 
				"	public static class SubPanel extends Panel\n" + 
				"	{\n" + 
				"		IModel<ConfigBean> _config=new PageStateModel<ConfigBean>(this, ConfigBean.class);\n" + 
				"\n" + 
				"		public SubPanel(String id)\n" + 
				"		{\n" + 
				"			super(id);\n" + 
				"\n" + 
				"			add(new Label(\"name\", new PropertyModel<Integer>(_config, \"name\")));\n" + 
				"\n" + 
				"			PageStateLink<StatelessTestPage, ConfigBean> link=new PageStateLink<StatelessTestPage, ConfigBean>(\"link\", StatelessTestPage.class, ConfigBean.class, new HashMap<String,Object>(\"Name\",\"Klaus\"));\n" + 
				"			add(link);\n" + 
				"			PageStateLink<StatelessTestPage, ConfigBean> link2=new PageStateLink<StatelessTestPage, ConfigBean>(\"link2\", StatelessTestPage.class, ConfigBean.class, new HashMap<String,Object>(\"Start\", 1));\n" + 
				"			add(link2);\n" + 
				"			PageStateLink<StatelessTestPage, ConfigBean> link3=new PageStateLink<StatelessTestPage, ConfigBean>(\"link3\", StatelessTestPage.class, ConfigBean.class, new HashMap<String,Object>(\"Start\", null));\n" + 
				"			add(link3);\n" + 
				"			PageStateLink<StatelessTestPage, ConfigBean> link4=new PageStateLink<StatelessTestPage, ConfigBean>(\"link4\", StatelessTestPage.class, ConfigBean.class, new HashMap<String,Object>(\"Name\", \"Bert\", \"Stop\", null));\n" + 
				"			add(link4);\n" + 
				"		}\n" + 
				"	}\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Wie man sieht, muss ich bei den Modellen eigentlich nichts besonderes machen. Die Links übergibt man eine Map mit neuen Parametern, mit denen der aktuelle Zustand, der in der JavaBean gespeichert wurde, für diesen Link, diese Nutzeraktion überschrieben wird. Dabei spielt es keine Rolle, in welcher Komponente so ein Link benutzt wird, da sich die Linkklasse und die Modellklasse selbsttätig um die Informationen bemühen.\n" + 
				"\n" + 
				"Der hier vorgeschlagene Ansatz ist sicher a) verbesserungswürdig und b) ausbaufähig. Er soll als Anregung dienen, wie man dieses und möglicherweise ähnliche Probleme lösen kann und dabei besonders von der Komponentenarchitektur von Wicket profitieren kann.\n" + 
				"";

//    Document document = Jsoup.parse(src);
//
//    System.out.println(document);
		String result = Html2Markdown.newInstance().convert(src);
    
//    String result = Html2Markdown.convert(src, Html2Markdown.newInstance(), Pair.of("<pre", "</pre>"));
//		
		assertEquals("", result);
	}
	
	@Test
	public void parseCodeSegments() {
		String src="Text ... Text.\n" + 
				"\n" + 
				"<pre lang=\"java\">\n" + 
				"java.lang.Double x=1.23;\n" + 
				"\n" + 
				"public static <T> T foo() {\n" + 
				"...\n" + 
				"}\n" + 
				"</pre>\n" + 
				"\n" + 
				"Other text.\n" + 
				"\n" + 
				"<code lang=\"java\">\n" + 
				"java.lang.Double x=1.23;\n" + 
				"\n" + 
				"public static <T> T foo() {\n" + 
				"...\n" + 
				"}\n" + 
				"</code>\n" + 
				"\n" + 
				"<pre><code lang=\"java\">\n" + 
				"java.lang.Double x=1.23;\n" + 
				"\n" + 
				"public static <T> T foo() {\n" + 
				"...\n" + 
				"}\n" + 
				"</code></pre>\n" + 
				"\n" + 
				"End.\n" + 
				"";
		
		Document document = Jsoup.parse(src);
		
		System.out.println(document);
	}
	
}
