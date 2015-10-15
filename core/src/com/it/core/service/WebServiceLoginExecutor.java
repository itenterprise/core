package com.it.core.service;

import com.it.core.tools.PreferenceHelper;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class WebServiceLoginExecutor extends WebServiceExecutor {

	private String login;
	private String password;
	
	public WebServiceLoginExecutor(String login, String password) {
		super("LoginEx");
		this.login = login;
		this.password = password;
	}

	protected void fillRequestHeaders(HttpURLConnection connection){
		connection.setRequestProperty("Accept-Language", PreferenceHelper.getAppLanguage());
	}

	@Override
	protected String getJSONRequestParams() {
		try {
			return String.format("login=%s&password=%s", URLEncoder.encode(login, CHARSET), URLEncoder.encode(password, CHARSET));
		} catch (UnsupportedEncodingException e) {
			return String.format("login=%s&password=%s", login, password);
		}
	}
}