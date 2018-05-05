package de.flapdoodle.solid.theme;

import com.google.common.annotations.VisibleForTesting;

import de.flapdoodle.solid.parser.path.AbsoluteUrl;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Links {

	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";

	public static String renderLink(String baseUrl, String path, String currentUrl, boolean relative) {
		String absoluteUrl = baseUrl+path;
		if (relative) {
			Tuple2<String, String> currentDomainAndPath = splitDomainPart(currentUrl);
			Tuple2<String, String> destDomainAndPath = splitDomainPart(absoluteUrl);
			if (!currentDomainAndPath._1().equals(destDomainAndPath._1())) {
				return absoluteUrl;
			}
			String result = AbsoluteUrl.parse(currentDomainAndPath._2())
					.relativePathTo(AbsoluteUrl.parse(destDomainAndPath._2()));
			System.out.println(" ? "+currentUrl+" --> "+absoluteUrl+" = "+result);
			return result;
		}
		return absoluteUrl;
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
