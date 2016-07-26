package br.produban.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import br.produban.WebServiceMessageSenderWithAuth;

@Configuration
public class EventStreamAdminServiceConfiguration {

	@Value("${br.produban.wso2.user}")
	public String user;

	@Value("${br.produban.wso2.pass}")
	public String pass;

	@Value("${br.produban.wso2.endpoint.EventStreamAdminServiceSoap}")
	public String endpoint;

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("cep.wsdl");
		return marshaller;
	}

	@Bean
	public EventStreamAdminServiceClient eventStreamAdminServiceClient(Jaxb2Marshaller marshaller) {
		EventStreamAdminServiceClient client = new EventStreamAdminServiceClient(endpoint);
		client.setDefaultUri("http://admin.stream.event.carbon.wso2.org");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		WebServiceTemplate template = client.getWebServiceTemplate();
		template.setMessageSender(new WebServiceMessageSenderWithAuth(this.user, this.pass));

		return client;
	}

}