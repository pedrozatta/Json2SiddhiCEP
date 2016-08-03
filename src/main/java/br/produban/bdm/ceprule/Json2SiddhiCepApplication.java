package br.produban.bdm.ceprule;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.gemstone.gemfire.cache.GemFireCache;

import br.produban.bdm.ceprule.model.CepRule;

/**
 * Created by pedrozatta
 */

@SpringBootApplication
@EnableResourceServer
@EnableCaching
@EnableGemfireRepositories
public class Json2SiddhiCepApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
		SpringApplication.run(Json2SiddhiCepApplication.class, args);
		SSLUtil.turnOffSslChecking();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Json2SiddhiCepApplication.class);
	}

	@Bean
	Properties gemfireProperties() {
		Properties gemfireProperties = new Properties();
		gemfireProperties.setProperty("name", "DataGemFireApplication");
		gemfireProperties.setProperty("mcast-port", "0");
		gemfireProperties.setProperty("log-level", "config");
		return gemfireProperties;
	}

	@Bean
	CacheFactoryBean gemfireCache() {
		CacheFactoryBean gemfireCache = new CacheFactoryBean();
		gemfireCache.setClose(true);
		gemfireCache.setProperties(gemfireProperties());
		
		return gemfireCache;
	}

	@Bean
	LocalRegionFactoryBean<String, CepRule> helloRegion(@Value("${br.produban.gemfire.persistent}") Boolean persistent,
			final GemFireCache cache) {
		LocalRegionFactoryBean<String, CepRule> region = new LocalRegionFactoryBean<>();
		region.setCache(cache);
		region.setClose(false);
		region.setName("CepRule");
		region.setPersistent(persistent);
		return region;
	}

}
