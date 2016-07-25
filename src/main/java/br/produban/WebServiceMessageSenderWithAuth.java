package br.produban;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.util.Base64Utils;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

public class WebServiceMessageSenderWithAuth extends HttpUrlConnectionMessageSender {

	@Override
	protected void prepareConnection(HttpURLConnection connection) throws IOException {

		String userpassword = "operador@produban.com.br:operador";
		String encodedAuthorization = Base64Utils.encodeToString(userpassword.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

		super.prepareConnection(connection);
	}
}