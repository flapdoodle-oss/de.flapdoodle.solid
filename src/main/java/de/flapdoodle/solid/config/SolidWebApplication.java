package de.flapdoodle.solid.config;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import de.flapdoodle.solidwicket.HomePage;

public class SolidWebApplication extends WebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

}
