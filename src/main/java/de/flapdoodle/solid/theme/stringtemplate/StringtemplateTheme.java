package de.flapdoodle.solid.theme.stringtemplate;

import java.nio.file.Path;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.theme.AbstractTheme;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Renderer.Renderable;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class StringtemplateTheme extends AbstractTheme {

	private final STGroupDir groupDir;

	public StringtemplateTheme(Path rootDir, PropertyTree config, MarkupRendererFactory markupRenderFactory) {
		super(rootDir,config,markupRenderFactory);
		groupDir = new STGroupDir(rootDir.toAbsolutePath().toFile().toString());
	}
	
	@Override
	public Renderer rendererFor(String templateName) {
		return renderable -> render(renderable, groupDir, templateName);
	}
	
	private Content render(Renderable renderable, STGroupDir group, String templateName) {
		ST template = Preconditions.checkNotNull(groupDir.getInstanceOf(templateName),"could not get template %s in %s",templateName,group);
		
		return Text.builder()
				.mimeType("text/html")
				.text(template.render())
				.build();
	}
}
