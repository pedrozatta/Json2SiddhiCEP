package br.produban;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.util.Base64Utils;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

public class WebServiceMessageSenderWithAuth extends HttpUrlConnectionMessageSender {

	protected String user;

	public String pass;

	public WebServiceMessageSenderWithAuth(String user, String pass) {
		this.user = user;
		this.pass = pass;

	}

	@Override
	protected void prepareConnection(HttpURLConnection connection) throws IOException {
		String userpassword = this.user + ":" + this.pass;

		String encodedAuthorization = Base64Utils.encodeToString(userpassword.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

		super.prepareConnection(connection);
	}
}