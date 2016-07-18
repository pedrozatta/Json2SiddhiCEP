package br.produban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Created by pedrozatta
 */

@SpringBootApplication
@EnableResourceServer
public class Json2SiddhiCepApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {

		SpringApplication.run(Json2SiddhiCepApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Json2SiddhiCepApplication.class);
	}

}
