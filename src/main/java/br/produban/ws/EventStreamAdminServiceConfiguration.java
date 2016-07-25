package br.produban.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import br.produban.WebServiceMessageSenderWithAuth;

@Configuration
public class EventStreamAdminServiceConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("cep.wsdl");
		return marshaller;
	}

	@Bean
	public EventStreamAdminServiceClient eventStreamAdminServiceClient(Jaxb2Marshaller marshaller) {
		EventStreamAdminServiceClient client = new EventStreamAdminServiceClient();
		client.setDefaultUri("http://admin.stream.event.carbon.wso2.org");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		
		WebServiceTemplate template = client.getWebServiceTemplate();
		template.setMessageSender(new WebServiceMessageSenderWithAuth());
		
		
		return client;
	}

}