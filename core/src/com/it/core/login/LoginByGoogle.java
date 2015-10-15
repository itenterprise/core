package com.it.core.login;

import java.util.ArrayList;
import java.util.Arrays;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.it.core.service.ServiceFactory;

public class LoginByGoogle implements OnLoginCompleted {
	OnLoginCompleted mHandler;
	ArrayList<String> googleIds;
	Activity mActivity;

	public LoginByGoogle (Activity activity, OnLoginCompleted handler) {
		mHandler = handler;
		mActivity = activity;
	}

	public void loginByGoogleId() {
		String[] googleLogin = getGoogleLogin();
		if (googleLogin == null) {
			mHandler.onFail(null);
			return;
		}
		googleIds = new ArrayList<String>(Arrays.asList(googleLogin));
		ILoginService loginService = ServiceFactory.createLoginService(mActivity);
		loginService.setOnLoginSuccessHandler(this);
		String googleId = googleIds.remove(0);
		loginService.login(googleId);
	}

	private String[] getGoogleLogin() {
		AccountManager mAccountManager = AccountManager.get(mActivity);
		Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		if (accounts.length == 0) {
			return null;
		}
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	@Override
	public void onSuccess(boolean needUpdateSession) {
		mHandler.onSuccess(needUpdateSession);
	}

	@Override
	public void onFail(String failReason) {
		if (googleIds.size() > 0) {
			ILoginService loginService = ServiceFactory.createLoginService(mActivity);
			loginService.setOnLoginSuccessHandler(this);
			String googleId = googleIds.remove(0);
			loginService.login(googleId);
			return;
		}
		mHandler.onFail(null);
	}

	@Override
	public void onError() {
		mHandler.onError();
	}
}