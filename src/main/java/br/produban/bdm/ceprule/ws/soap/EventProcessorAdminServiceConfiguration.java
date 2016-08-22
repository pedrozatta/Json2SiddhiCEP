package br.produban.bdm.ceprule.ws.soap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import br.produban.bdm.commons.WebServiceMessageSenderWithAuth;

@Configuration
public class EventProcessorAdminServiceConfiguration {

	@Value("${br.produban.wso2.user}")
	public String user;

	@Value("${br.produban.wso2.pass}")
	public String pass;

	@Value("${br.produban.wso2.endpoint.EventProcessorAdminServiceSoap}")
	public String endpoint;

	@Bean
	public Jaxb2Marshaller eventProcessorAdminServiceMarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("br.produban.bdm.wso2.eventprocessoradmin");
		return marshaller;
	}

	@Bean
	public EventProcessorAdminServiceClient eventProcessorAdminServiceClient(
			Jaxb2Marshaller eventProcessorAdminServiceMarshaller) {
		EventProcessorAdminServiceClient client = new EventProcessorAdminServiceClient(endpoint);
		client.setDefaultUri("http://admin.stream.event.carbon.wso2.org");
		client.setMarshaller(eventProcessorAdminServiceMarshaller);
		client.setUnmarshaller(eventProcessorAdminServiceMarshaller);

		WebServiceTemplate template = client.getWebServiceTemplate();
		template.setMessageSender(new WebServiceMessageSenderWithAuth(this.user, this.pass));
		return client;
	}

}