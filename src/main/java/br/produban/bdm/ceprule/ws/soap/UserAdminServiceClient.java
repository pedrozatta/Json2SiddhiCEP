package br.produban.bdm.ceprule.ws.soap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import br.produban.bdm.wso2.useradmin.FlaggedName;
import br.produban.bdm.wso2.useradmin.GetRolesOfUser;
import br.produban.bdm.wso2.useradmin.GetRolesOfUserResponse;
import br.produban.bdm.wso2.useradmin.ObjectFactory;

@Service
public class UserAdminServiceClient extends WebServiceGatewaySupport {

	protected String endpoint;

	public UserAdminServiceClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public List<String> getRolesOfUser(String user, String filter, int limit) {
		ObjectFactory factory = new ObjectFactory();

		GetRolesOfUser request = factory.createGetRolesOfUser();
		request.setUserName(factory.createGetRolesOfUserUserName(user));
		request.setFilter(factory.createGetRolesOfUserFilter(filter));
		request.setLimit(100);

		GetRolesOfUserResponse response = (GetRolesOfUserResponse) getWebServiceTemplate().marshalSendAndReceive(
				endpoint, request, new SoapActionCallback("http://admin.stream.event.carbon.wso2.org/getRolesOfUser"));

		List<String> result = new ArrayList<String>();
		for (FlaggedName role : response.getReturn()){
			if (role.isSelected()){
				result.add(role.getItemName().getValue());
			}
		}
		return result;
	}

}
