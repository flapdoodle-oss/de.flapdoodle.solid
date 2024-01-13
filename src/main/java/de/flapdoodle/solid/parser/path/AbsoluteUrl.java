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
package de.flapdoodle.solid.parser.path;

import com.google.common.collect.ImmutableList;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Check;
import org.immutables.value.Value.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

// /foo/bar/stuff -->  [/foo,/bar,/stuff]
@Immutable
public abstract class AbsoluteUrl {
	public abstract List<String> parts();
	
	@Check
	protected void check() {
		Preconditions.checkArgument(!parts().isEmpty(), "is not an absolute url: <empty>");
		parts().forEach(part -> {
			Preconditions.checkArgument(!part.equals(".."), "path must not contain '..': %s",parts());
			Preconditions.checkArgument(!part.equals("."), "path must not contain '.': %s",parts());
		});
	}
	
	@Auxiliary
	public boolean endsWithSlash() {
		return last(parts()).equals("/");
	}

	@Auxiliary
	public String relativePathTo(AbsoluteUrl destination) {
		if (parts().equals(destination.parts())) {
			return last(destination.parts()).substring(1);
		}
		int firstDifference = firstDifference(parts(), destination.parts());
		List<String> leftParts = destination.parts().subList(firstDifference, destination.parts().size());
		java.util.List<String> fixedLeftParts = ImmutableList.<String>builder()
			.add(leftParts.get(0).substring(1))
			.addAll(leftParts.subList(1, leftParts.size()))
			.build();
		String changeDirPart = Joiner.on('/').join(Collections.nCopies(parts().size()-firstDifference-1,".."));
		return changeDirPart+fixedLeftParts.stream().reduce(changeDirPart.isEmpty() ? "" : "/", (a,b) -> a+b);
	}

	public static AbsoluteUrl parse(String src) {
		Preconditions.checkArgument(!src.isEmpty(), "is not an absolute url: <empty>", src);
		Preconditions.checkArgument(src.charAt(0) == '/', "is not an absolute url: %s", src);
		ImmutableAbsoluteUrl.Builder builder = ImmutableAbsoluteUrl.builder();
		int current=0;
		do {
			int indexOfSlash = src.indexOf('/', current+1);
			if (indexOfSlash!=-1) {
				builder.addParts(src.substring(current, indexOfSlash));
				current=indexOfSlash;
			} else {
				builder.addParts(src.substring(current));
				current=src.length();
			}
		} while (current<src.length());
		
		return builder.build();
	}

	private static <T> int firstDifference(java.util.List<T> a, java.util.List<T> b) {
		int len=Math.min(a.size(), b.size());
		Preconditions.checkArgument(len>0,"both list must contain at least one element");
		for (int i=0;i<len;i++) {
			if (!Objects.equals(a.get(i), b.get(i))) {
				return i;
			}
		}
		return len;
	}

	private static <T> T last(java.util.List<T> list) {
		Preconditions.checkArgument(!list.isEmpty(),"list is empty");
		return list.get(list.size()-1);
	}

}
