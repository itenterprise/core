package com.it.core.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.it.core.R;
import com.it.core.activity.ActivityBase;
import com.it.core.application.ApplicationBase;
import com.it.core.delegateduser.DelegatedUserRepository;
import com.it.core.eds.EdsRepository;
import com.it.core.model.UserInfo;
import com.it.core.model.UserInfo.Credentials;
import com.it.core.notifications.Dialog;
import com.it.core.notifications.PushNotificationsService;
import com.it.core.serialization.SerializeHelper;
import com.it.core.service.OnExecuteCompleted;
import com.it.core.service.WebServiceLoginByGoogleIdExecutor;
import com.it.core.service.WebServiceLoginExecutor;
import com.it.core.service.exception.WebServiceException;
import com.it.core.session.Session;

import java.io.IOException;

public class LoginService implements ILoginService{
	private final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.email";
    private final String REQUEST_CODE_RECOVER_KEY = "REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES";
	
	public LoginService (Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * Деятельность с которой пытаемся выполнить вход
	 */
	private Activity mActivity;
	
	/**
	 * Обработчик результатов входа
	 */
	private OnLoginCompleted handler;
	
	/**
	 * Установить обработчик
	 */
	public void setOnLoginSuccessHandler(OnLoginCompleted handler){
		this.handler = handler;
	}
	
	/**
	 * Попытаться выполнить вход по сохраненным учетным данным
	 */
	public void loginFromStoredCredentials(){
		Credentials cred = ApplicationBase.getInstance().getCredentials();
		if (cred == null) {
			cred = UserInfo.getCredentials();
		}
		// Попытаться войти, если есть логин google
		if (cred != null && !TextUtils.isEmpty(cred.getGoogleLogin())) {
			login(cred.getGoogleLogin());
		} else
		// Попытаться войти, если есть логин и пароль
		if (cred != null && !TextUtils.isEmpty(cred.getLogin()) && !TextUtils.isEmpty(cred.getPassword())) {
			login(cred.getLogin(), cred.getPassword());
		} else {
			onLoginFail(null);
		}
	}
	
	/**
	 * Войти
	 */
	public void login(String login, String password){
		login(login, password, false);
	}
	
	/**
	 * Войти
	 * @param login Логин
	 * @param password Пароль
	 * @param remember Сохранить учетные данные, если вход выполниться успешно
	 */
	public void login(final String login, final String password, final boolean remember) {
		try {
			if (ApplicationBase.getInstance().isAuthRequested()) {
				onLoginSuccess(false, false);
				return;
			}
			ApplicationBase.getInstance().setAuthRequested(true);
			// Отобразить процесс входа
			final ProgressDialog progress = Dialog.showProgressDialog(mActivity, mActivity.getString(R.string.progress_dialog_loading_message));
			// Выполнить запрос на аутентификацию по логину google
			WebServiceLoginExecutor exec = new WebServiceLoginExecutor(login, password);
			exec.setOnExecuteCompletedListener(new OnExecuteCompleted() {

				/**
				 * Получили ответ от веб-сервиса
				 */
				@Override
				public void OnCompleted(String result) {
					ApplicationBase.getInstance().setAuthRequested(false);
					Dialog.hideProgress(progress);
					// Недопустимый результат аутентификации
					if (result == null) {
						onLoginError();
						return;
					}
					// Десериализировать JSON
					LoginResult res = SerializeHelper.deserialize(result, LoginResult.class);
					if (res == null) {
						onLoginError();
						return;
					}
					// Успех
					if (res.getSuccess()) {
						String ticket = res.getTicket();
						String userName = res.getUserName();
						UserInfo.putTicketAndName(ticket, userName);
                        if (Session.getId() == null || Session.getId().isEmpty()) {
                            Session.setId(ticket);
                        }
						Credentials cred = new Credentials();
						cred.setLogin(login);
						cred.setPassword(password);
						ApplicationBase.getInstance().putCredentials(cred);
						if (remember) {
							UserInfo.putCredentials(cred);
						}
						onLoginSuccess(true, remember);
					}
					// Неуспех
					else {
						onLoginFail(res.getFailReason());
					}
				}

                @Override
                public void OnError(WebServiceException exception) {
	                ApplicationBase.getInstance().setAuthRequested(false);
                    Dialog.hideProgress(progress);
                    onLoginError();
                }
            });
			exec.execute();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Войти
	 * @param googleLogin Логин google
	 */
	public void login(final String googleLogin) {
		try {
			AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
	            @Override
	            protected String doInBackground(Void... params) {
	                String token = null;

	                try {
	                    token = GoogleAuthUtil.getToken(
			                    mActivity,
	                    		googleLogin,
	                    		SCOPE);
	                } catch (IOException transientEx) {
	                    // Network or server error, try later
	                    Log.e("Core", transientEx.toString());
	                } catch (UserRecoverableAuthException e) {
	                    // Recover (with e.getIntent())
		                mActivity.startActivityForResult(e.getIntent(), ActivityBase.REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
                        token = REQUEST_CODE_RECOVER_KEY;
	                	//REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR
	                } catch (GoogleAuthException authEx) {
	                    // The call is not ever expected to succeed
	                    // assuming you have already verified that 
	                    // Google Play services is installed.
	                    Log.e("Core", authEx.toString());
	                }
	                return token;
	            }

	            @Override
	            protected void onPostExecute(String token) {
	            	if (token == null) {
            			onLoginError();
	            		return;
	            	}
                    if (token.equals(REQUEST_CODE_RECOVER_KEY)) {
                        return;
                    }
					// Отобразить процесс входа
					final ProgressDialog progress = Dialog.showProgressDialog(mActivity, mActivity.getString(R.string.progress_dialog_loading_message));
					// Выполнить запрос на аутентификацию по логину google
					WebServiceLoginByGoogleIdExecutor exec = new WebServiceLoginByGoogleIdExecutor(token);
					exec.setOnExecuteCompletedListener(new OnExecuteCompleted() {
						/**
						 * Получили ответ от веб-сервиса
						 */
						@Override
						public void OnCompleted(String result) {
							Dialog.hideProgress(progress);
							// Недопустимый результат аутентификации
							if (result == null) {
								onLoginError();
								return;
							}
							// Десериализировать JSON
							LoginResult res = SerializeHelper.deserialize(result, LoginResult.class);
							if (res == null) {
								onLoginError();
								return;
							}
							// Успех
							if (res.getSuccess()) {
								String ticket = res.getTicket();
								String userName = res.getUserName();
								UserInfo.putTicketAndName(ticket, userName);
								if (Session.getId() == null || Session.getId().isEmpty()) {
									Session.setId(ticket);
								}
								Credentials cred = new Credentials();
								cred.setGoogleLogin(googleLogin);
								ApplicationBase.getInstance().putCredentials(cred);
								UserInfo.putCredentials(cred);
								onLoginSuccess(true, true);
							}
							// Неуспех
							else {
								onLoginFail(res.getFailReason());
							}
						}

						@Override
						public void OnError(WebServiceException exception) {
							Dialog.hideProgress(progress);
							onLoginError();
						}
					});
					exec.execute();
				}
			};
			task.execute();
		} catch (Exception e) {
			@SuppressWarnings("unused")
			Exception ex = e;
		}
	}

	private void onLoginSuccess(boolean updateSession, boolean remember) {
		if (handler != null) {
			handler.onSuccess(updateSession);
		}
		// Зарегистрировать приложение на получение push-уведомлений, если установлено "Запомнить меня"
		ApplicationBase applicationBase = ApplicationBase.getInstance();
		if (remember && !TextUtils.isEmpty(applicationBase.getApplicationID())
				&& !TextUtils.isEmpty(applicationBase.getPushNotificationSenderId())) {
			new PushNotificationsService().register(mActivity, applicationBase.getApplicationID(), applicationBase.getPushNotificationSenderId());
			applicationBase.setRegisterForPush(true);
		}
		// Инициализировать пользовательские настройки
		if (applicationBase.hasCustomSettings()) {
			applicationBase.initCustomSettings();
		}
		// Очистить значение делегированного пользователя
		DelegatedUserRepository.getInstance().clearCurrentDelegatedUser();
	}

	private void onLoginFail(String failReason) {
		if (handler != null) {
			handler.onFail(failReason);
		}
	}

	private void onLoginError() {
		if (handler != null) {
			handler.onError();
		}
	}

	public void logout() {
		ApplicationBase applicationBase = ApplicationBase.getInstance();
		applicationBase.putTicket(new String(UserInfo.getTicket() + ""));
		// Отписаться от push-уведомлений если были подписаны
		if (applicationBase.isRegisterForPush() && !TextUtils.isEmpty(applicationBase.getApplicationID())) {
			new PushNotificationsService().unregister(mActivity, applicationBase.getApplicationID()//, new OnTaskCompleted() {
//				@Override
//				public void onTaskCompleted(Object result) {
//					clearCredentials();
//				}
//			}
			);
			applicationBase.setRegisterForPush(false);
//			return;  TODO:remove - відписуватись від сповіщень і видаляти credentials
		}
		clearCredentials();
	}

	private void clearCredentials() {
		UserInfo.removeTicketAndName();
		UserInfo.removeCredentials();
		ApplicationBase applicationBase = ApplicationBase.getInstance();
		applicationBase.putCredentials(null);
		Session.setId(null);
		if (applicationBase.isUsingEds()) {
			EdsRepository.getInstance().clearEdsCredentials();
		}
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	private static class LoginResult {
		@JsonProperty("Success")
		private boolean success;
		@JsonProperty("UserName")
		private String userName;
		@JsonProperty("Ticket")
		private String ticket;
		@JsonProperty("FailReason")
		private String failReason;

		public boolean getSuccess(){
			return success;
		}
		public void setSuccess(boolean val){
			success = val;
		}

		public String getUserName(){
			return userName;
		}
		public void setUserName(String val){
			userName = val;
		}

		public String getTicket(){
			return ticket;
		}
		public void setTicket(String val){
			ticket = val;
		}

		public String getFailReason(){
			return failReason;
		}
		public void setFailReason(String failReason){
			this.failReason = failReason;
		}
	}
}