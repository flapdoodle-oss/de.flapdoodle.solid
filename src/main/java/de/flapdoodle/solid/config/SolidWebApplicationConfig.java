package de.flapdoodle.solid.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.guice.GuiceWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.servlet.ServletModule;

public class SolidWebApplicationConfig extends ServletModule {
	@Override
	protected void configureServlets() {
		String filterMapping = "/*";

		Map<String, String> params = new HashMap<>();
		params.put(WicketFilter.APP_FACT_PARAM, GuiceWebApplicationFactory.class.getName());
		params.put(WicketFilter.FILTER_MAPPING_PARAM, filterMapping);
		// params.put("injectorContextAttribute", "GuiceInjector");
		params.put("module", SolidWebModule.class.getName());
		params.put("configuration", RuntimeConfigurationType.DEPLOYMENT.toString());
		params.put(GuiceWebApplicationFactory.STAGE_PARAMETER, Stage.DEVELOPMENT.toString());
		filter(filterMapping).through(SolidWebFilter.class, params);
	}

	@Singleton
	public static class SolidWebFilter extends WicketFilter {

	}

	public static class SolidWebModule extends AbstractModule {

		@Override
		protected void configure() {
			// install(new ApplicationConfig());
			bind(WebApplication.class).to(SolidWebApplication.class);
		}
	}

}
