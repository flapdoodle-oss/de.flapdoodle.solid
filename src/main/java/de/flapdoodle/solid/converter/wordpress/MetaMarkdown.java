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

import java.util.Collection;
import java.util.regex.Pattern;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;

/*
 * ---
title: Wicket Resourcen mit Jetty nachladen
author: admin
type: post
date: 2008-12-31T14:56:32+00:00
url: /2008/12/31/wicket-resourcen-mit-jetty-nachladen/
aktt_notify_twitter:
  - no
categories:
  - Maven
  - Wicket
tags:
  - hotdeploy
  - jetty
  - Maven
  - reload
  - resource
  - Wicket

---
 */
@Immutable
public abstract class MetaMarkdown {
	protected abstract String author();
	protected abstract String title();
	protected abstract String type();
	protected abstract String date();
	protected abstract String url();
	
	@Default
	protected boolean isDraft() {
		return false;
	}
	
	protected abstract ImmutableSet<String> categories();
	protected abstract ImmutableSet<String> tags();
	protected abstract ImmutableMultimap<String,String> meta();
	
	protected abstract String path();
	protected abstract String body();
	
	@Auxiliary
	public Document asDocument() {
		return Document.builder()
				.path(path())
				.content(Text.builder()
						.encoding(Charsets.UTF_8)
						.mimeType("text/markdown")
						.text(asTomlMarkdown())
						.build())
				.build();
	}
	
	
	@Auxiliary
	private String asTomlMarkdown() {
		StringBuilder sb=new StringBuilder();
		sb.append(tomlHeader());
		sb.append(body());
		return sb.toString();
	}

	@Auxiliary
	private String tomlHeader() {
		StringBuilder sb=new StringBuilder();
		sb.append("---\n");
		property(sb, "title", title());
		property(sb, "author", author());
		property(sb, "type", type());
		property(sb, "date", date());
		if (isDraft()) {
			property(sb, "draft", "true");
		}
		property(sb, "url", url());
		meta().asMap().forEach((key, values) -> {
			if (validMetaKey(key)) {
				property(sb, key, values);
			}
		});
		stringProperty(sb, "categories", categories());
		stringProperty(sb, "tags", tags());
		sb.append("\n---\n");
		return sb.toString();
	}
	private static final ImmutableSet<String> VALID_META_KEYS=ImmutableSet.of("aktt_tweeted", "aktt_notify_twitter");
	private boolean validMetaKey(String key) {
		return VALID_META_KEYS.contains(key);
	}
	
	private static final Pattern INVALID_YAML_CONTENT_PATTERN=Pattern.compile(":\\s+");
	
	@VisibleForTesting
	protected static void property(StringBuilder sb, String label, String value) {
		if (INVALID_YAML_CONTENT_PATTERN.matcher(value).find()) {
			value="'"+value+"'";
		}
		sb.append(label).append(": ").append(value).append("\n");
	}
	
	private static void property(StringBuilder sb, String label, Collection<String> values) {
		if (!values.isEmpty()) {
			sb.append(label).append(":").append("\n");
			values.forEach(v -> {
				sb.append("  - ").append(v).append("\n");
			});
		}
	}

	private static void stringProperty(StringBuilder sb, String label, Collection<String> values) {
		if (!values.isEmpty()) {
			sb.append(label).append(":").append("\n");
			values.forEach(v -> {
				sb.append("  - ").append(numberAsEscapedString(v)).append("\n");
			});
		}
	}
	
	private static String numberAsEscapedString(String src) {
		try {
			Double.parseDouble(src);
			return "\""+src+"\"";
		} catch (NumberFormatException nx) {
			// not a number
		}
		return src;
	}
	
	public static ImmutableMetaMarkdown.Builder builder() {
		return ImmutableMetaMarkdown.builder();
	}
	
}
