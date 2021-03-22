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
package de.flapdoodle.solid.theme;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import de.flapdoodle.solid.parser.path.AbsoluteUrl;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Links {

	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";

	public static String renderLink(String baseUrl, String path, String currentUrl, boolean relative) {
		Tuple2<String, String> destDomainAndPath = splitDomainPart(path);
		if (destDomainAndPath._1().isEmpty()) {
			destDomainAndPath = splitDomainPart(baseUrl+path);
		}
		if (relative) {
			Tuple2<String, String> currentDomainAndPath = splitDomainPart(currentUrl);
			if (currentDomainAndPath._1().equals(destDomainAndPath._1())) {
				String result = AbsoluteUrl.parse(currentDomainAndPath._2())
						.relativePathTo(AbsoluteUrl.parse(destDomainAndPath._2()));
				Preconditions.checkArgument(!result.contains("http:"),"wrong: %s (%s -> %s)",result,currentDomainAndPath, destDomainAndPath);
				Preconditions.checkArgument(!result.contains("//"),"wrong: %s (%s -> %s)",result,currentDomainAndPath, destDomainAndPath);
//				System.out.println(" ? "+currentUrl+" --> "+path+" = "+result);
				return result;
			}
		}
		return destDomainAndPath._1()+destDomainAndPath._2();
	}

	@VisibleForTesting
	static Tuple2<String,String> splitDomainPart(String absoluteUrl) {
		if (!absoluteUrl.startsWith("/")) {
			int idx=-1;
			if (absoluteUrl.startsWith(HTTP)) {
				idx=absoluteUrl.indexOf('/',HTTP.length());
			}
			if (absoluteUrl.startsWith(HTTPS)) {
				idx=absoluteUrl.indexOf('/',HTTPS.length());
			}
			if (idx!=-1) {
				return Tuple.of(absoluteUrl.substring(0, idx), absoluteUrl.substring(idx));
			}
			return Tuple.of(absoluteUrl,"/");
		}
		return Tuple.of("",absoluteUrl);
	}
}
