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

	// @Configuration
	// protected static class SecurityConfiguration extends
	// OAuth2SsoConfigurerAdapter {
	//
	// @Override
	// public void match(RequestMatchers matchers) {
	// matchers.anyRequest();
	// }
	//
	// @Override
	// public void configure(HttpSecurity http) throws Exception {
	// http.authorizeRequests().antMatchers("/index.html", "/home.html", "/")
	// .permitAll().anyRequest().authenticated().and().csrf()
	// .csrfTokenRepository(csrfTokenRepository()).and()
	// .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
	// }
	//
	// }

}
