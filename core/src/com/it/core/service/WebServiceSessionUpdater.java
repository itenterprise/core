package com.it.core.service;

public class WebServiceSessionUpdater extends WebServiceExecutor{

    private String mTicket;
    private String mSessionId;

    public WebServiceSessionUpdater(String ticket, String sessionId) {
        super("UpdateSession");
        mTicket = ticket;
        mSessionId = sessionId;
    }

    @Override
    protected String getJSONRequestParams() {
        return String.format("ticket=%s&sessionId=%s", mTicket, mSessionId);
    }
}