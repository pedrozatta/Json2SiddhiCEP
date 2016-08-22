package br.produban.bdm.ceprule.ws.soap;

import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import br.produban.bdm.ceprule.model.ExecutionPlan;
import br.produban.bdm.wso2.eventprocessoradmin.ObjectFactory;
import br.produban.bdm.wso2.eventprocessoradmin.ValidateExecutionPlan;
import br.produban.bdm.wso2.eventprocessoradmin.ValidateExecutionPlanResponse;

@Service
public class EventProcessorAdminServiceClient extends WebServiceGatewaySupport {

	protected String endpoint;

	public EventProcessorAdminServiceClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public ExecutionPlan validateExecutionPlan(String plan) {
		ObjectFactory factory = new ObjectFactory();

		ValidateExecutionPlan request = factory.createValidateExecutionPlan();
		request.setExecutionPlan(factory.createValidateExecutionPlanExecutionPlan(plan));

		ValidateExecutionPlanResponse response = (ValidateExecutionPlanResponse) getWebServiceTemplate()
				.marshalSendAndReceive(endpoint, request,
						new SoapActionCallback("http://admin.stream.event.carbon.wso2.org/validateExecutionPlan"));

		ExecutionPlan executionPlan = new ExecutionPlan();
		executionPlan.setPlan(plan);
		executionPlan.setMessage(response.getReturn().getValue());
		if ("success".equals(executionPlan.getMessage())) {
			executionPlan.setValid(true);
		} else {
			executionPlan.setValid(false);
		}
		logger.info(executionPlan.getMessage());
		return executionPlan;
	}

}
