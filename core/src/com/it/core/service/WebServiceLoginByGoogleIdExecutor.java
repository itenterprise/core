package com.it.core.service;

public class WebServiceLoginByGoogleIdExecutor extends WebServiceExecutor {
	private String token;
	
	public WebServiceLoginByGoogleIdExecutor(String token) {
		super("LoginByGoogleId");
		this.token = token;
	}

    @Override
    protected String getJSONRequestParams() {
        return String.format("token=%s", token);
    }
}