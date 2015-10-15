package com.it.core.service;

import com.it.core.application.ApplicationBase;
import com.it.core.model.UserInfo;

public class WebServiceChangePasswordExecutor extends WebServiceExecutor {

	private String mOldPassword;
	private String mNewPassword;

	public WebServiceChangePasswordExecutor(String oldPassword, String newPassword) {
		super("ChangePassword");
		mOldPassword = oldPassword;
		mNewPassword = newPassword;
	}

    @Override
    protected String getJSONRequestParams() {
		return String.format("login=%s&oldPassword=%s&newPassword=%s&ticket=%s",
				ApplicationBase.getInstance().getCredentials().getLogin().toUpperCase(),
				mOldPassword,
				mNewPassword,
				UserInfo.getTicket() != null ? UserInfo.getTicket() : "");
	}
}