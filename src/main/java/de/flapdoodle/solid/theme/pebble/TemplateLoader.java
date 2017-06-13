package de.flapdoodle.solid.theme.pebble;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;

public class TemplateLoader implements Loader<String> {

	private final Path templateRoot;

	public TemplateLoader(Path templateRoot) {
		this.templateRoot = templateRoot;
	}
	
	@Override
	public Reader getReader(String cacheKey) throws LoaderException {
		try {
			return new FileReader(templateRoot.resolve(cacheKey).toFile());
		}
		catch (FileNotFoundException e) {
			throw new LoaderException(e, "could not load "+cacheKey);
		}
	}

	@Override
	public void setCharset(String charset) {
		throw new IllegalArgumentException("this should not be called.");
	}

	@Override
	public void setPrefix(String prefix) {
		throw new IllegalArgumentException("this should not be called.");
	}

	@Override
	public void setSuffix(String suffix) {
		throw new IllegalArgumentException("this should not be called.");
	}

	@Override
	public String resolveRelativePath(String relativePath, String anchorPath) {
		return resolvePath(relativePath, anchorPath);
	}

	@VisibleForTesting
	protected static String resolvePath(String relativePath, String anchorPath) {
		Path parent = resolve(anchorPath, '/').getParent();
		if (parent==null) {
			parent=Paths.get("");
		}
		return parent.resolve(resolve(relativePath+".html", '/')).normalize().toString();
	}
	
	protected static Path resolve(String path, char pathSeparator) {
		Iterable<String> splitted = Splitter.on(pathSeparator).split(path);
		Iterator<String> iterator = splitted.iterator();
		Path current=Paths.get(iterator.next());
		while (iterator.hasNext()) {
			current=current.resolve(iterator.next());
		}
		return current;
	}

	@Override
	public String createCacheKey(String templateName) {
		return templateName;
	}

}
