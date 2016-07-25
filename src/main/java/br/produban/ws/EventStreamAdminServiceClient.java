package br.produban.ws;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import cep.wsdl.EventStreamInfoDto;
import cep.wsdl.GetAllEventStreamDefinitionDto;
import cep.wsdl.GetAllEventStreamDefinitionDtoResponse;
import cep.wsdl.GetStreamNames;
import cep.wsdl.GetStreamNamesResponse;

@Service
public class EventStreamAdminServiceClient extends WebServiceGatewaySupport {

	// String endpoint = "http://localhost:50004";
	String endpoint = "https://srvbigpvlbr12.bs.br.bsch:9443/services/EventStreamAdminService.EventStreamAdminServiceHttpsSoap11Endpoint";

	public List<String> getStreamNames() {
		GetStreamNames request = new GetStreamNames();

		GetStreamNamesResponse response = (GetStreamNamesResponse) getWebServiceTemplate().marshalSendAndReceive(
				endpoint, request, new SoapActionCallback("http://admin.stream.event.carbon.wso2.org/getStreamNames"));

		return response.getReturn();
	}

	public List<EventStreamInfoDto> getAllEventStreamDefinitionDto() {

		GetAllEventStreamDefinitionDto request = new GetAllEventStreamDefinitionDto();

		GetAllEventStreamDefinitionDtoResponse response = (GetAllEventStreamDefinitionDtoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(endpoint, request, new SoapActionCallback(
						"http://admin.stream.event.carbon.wso2.org/getAllEventStreamDefinitionDto"));

		return response.getReturn();
	}

}
