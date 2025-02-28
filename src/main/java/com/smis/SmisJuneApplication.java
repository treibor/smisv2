package com.smis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;


@SpringBootApplication()
//@Theme (variant= Lumo.LIGHT)
//@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@Theme(value = "my-theme")
@PWA(name = "Smis", shortName = "SMIS", iconPath = "/images/plant.png")
public class SmisJuneApplication extends SpringBootServletInitializer implements AppShellConfigurator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public static void main(String[] args) {
		SpringApplication.run(SmisJuneApplication.class, args);
	}
	
	@Override
	public void configurePage(AppShellSettings settings) {
		settings.addFavIcon("icon", "/images/plant.png", "192x192");
		settings.addLink("shortcut icon", "/images/plant.png");
		settings.setPageTitle("SMIS");
	}
}
