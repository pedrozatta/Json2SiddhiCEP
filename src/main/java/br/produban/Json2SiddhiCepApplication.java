package br.produban;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Created by pedrozatta
 */

@SpringBootApplication
@EnableResourceServer
@EnableCaching
public class Json2SiddhiCepApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
		SpringApplication.run(Json2SiddhiCepApplication.class, args);
		SSLUtil.turnOffSslChecking();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Json2SiddhiCepApplication.class);
	}

}
