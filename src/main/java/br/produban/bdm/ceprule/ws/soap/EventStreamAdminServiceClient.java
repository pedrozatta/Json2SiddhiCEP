package br.produban.bdm.ceprule.ws.soap;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import br.produban.bdm.wso2.eventstreamadmin.EventStreamInfoDto;
import br.produban.bdm.wso2.eventstreamadmin.GetAllEventStreamDefinitionDto;
import br.produban.bdm.wso2.eventstreamadmin.GetAllEventStreamDefinitionDtoResponse;
import br.produban.bdm.wso2.eventstreamadmin.GetStreamDetailsForStreamId;
import br.produban.bdm.wso2.eventstreamadmin.GetStreamDetailsForStreamIdResponse;
import br.produban.bdm.wso2.eventstreamadmin.GetStreamNames;
import br.produban.bdm.wso2.eventstreamadmin.GetStreamNamesResponse;
import br.produban.bdm.wso2.eventstreamadmin.ObjectFactory;

@Service
public class EventStreamAdminServiceClient extends WebServiceGatewaySupport {

	protected String endpoint;

	public EventStreamAdminServiceClient(String endpoint) {
		this.endpoint = endpoint;
	}

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

	public String getStreamDetailsForStreamId(String id) {
		ObjectFactory factory = new ObjectFactory();

		GetStreamDetailsForStreamId request = factory.createGetStreamDetailsForStreamId();
		request.setStreamId(factory.createGetStreamDetailsForStreamIdStreamId(id));

		GetStreamDetailsForStreamIdResponse response = (GetStreamDetailsForStreamIdResponse) getWebServiceTemplate()
				.marshalSendAndReceive(endpoint, request, new SoapActionCallback(
						"http://admin.stream.event.carbon.wso2.org/getStreamDetailsForStreamId"));

		return response.getReturn().get(0);
	}

}
