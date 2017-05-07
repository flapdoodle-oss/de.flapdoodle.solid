package de.flapdoodle.solid.generator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.content.Site;

public class DefaultSiteGenerator implements SiteGenerator {

	@Override
	public ImmutableList<Document> generate(Site site) {
		System.out.println(" -> "+site);
		
		System.out.println("dates -> "+metaValues(site, "date"));
		System.out.println("titles -> "+metaValues(site, "title"));
		
		return ImmutableList.of();
	}

	private static List<Object> metaValues(Site site, String key) {
		List<Object> dates = site.blobs().stream()
			.map(blob -> blob.meta().find(Object.class, key))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
		return dates;
	}
}
