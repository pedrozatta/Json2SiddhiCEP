package br.produban.bdm.ceprule;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import br.produban.bdm.ceprule.commons.SSLUtil;

/**
 * Created by pedrozatta
 */

@SpringBootApplication
@EnableResourceServer
@EnableAspectJAutoProxy
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
