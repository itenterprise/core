package com.it.core.login;

import android.app.Activity;
import android.app.ProgressDialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.it.core.R;
import com.it.core.model.UserInfo;
import com.it.core.notifications.Dialog;
import com.it.core.serialization.SerializeHelper;
import com.it.core.service.OnExecuteCompleted;
import com.it.core.service.WebServiceChangePasswordExecutor;
import com.it.core.service.exception.WebServiceException;

public class ChangePasswordService implements IChangePasswordService {

	public ChangePasswordService(Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * Деятельность с которой пытаемся сменить пароль
	 */
	private Activity mActivity;
	
	/**
	 * Обработчик результатов смены пароля
	 */
	private OnPasswordChanged mHandler;
	
	/**
	 * Установить обработчик
	 */
	@Override
	public void setOnPasswordChangedHandler(OnPasswordChanged handler) {
		mHandler = handler;
	}

	@Override
	public void changePassword(String oldPassword, final String newPassword) {
		try {
			// Отобразить прогресс смены пароля
			final ProgressDialog progress = Dialog.showProgressDialog(mActivity, mActivity.getString(R.string.password_change_processing));
			// Выполнить запрос на смену пароля текущего пользователя
			WebServiceChangePasswordExecutor exec = new WebServiceChangePasswordExecutor(oldPassword, newPassword);
			exec.setOnExecuteCompletedListener(new OnExecuteCompleted() {
				@Override
				public void OnCompleted(String result) {
					Dialog.hideProgress(progress);
					// Недопустимый результат
					if (result == null) {
						onChangePasswordError();
						return;
					}
					// Десериализировать JSON
					ChangePasswordResult res = SerializeHelper.deserialize(result, ChangePasswordResult.class);
					// Успех
					if (res.getSuccess()) {

						UserInfo.Credentials credentials = UserInfo.getCredentials();
						if (credentials != null && credentials.getPassword() != null && !credentials.getPassword().isEmpty()) {
							credentials.setPassword(newPassword);
							UserInfo.putCredentials(credentials);
						}
						onChangePasswordSuccess();
					}
					// Неуспех
					else {
						onChangePasswordFail(res.getFailReason());
					}
				}

				@Override
				public void OnError(WebServiceException exception) {
					Dialog.hideProgress(progress);
					onChangePasswordError();
				}
			});
			exec.execute();
		} catch (Exception e) {
		}
	}

	private void onChangePasswordSuccess() {
		if (mHandler != null){
			mHandler.onSuccess();
		}
	}
	
	private void onChangePasswordFail(String failReason) {
		if (mHandler != null){
			mHandler.onFail(failReason);
		}
	}
	
	private void onChangePasswordError() {
		if (mHandler != null){
			mHandler.onError();
		}
	}

	private static class ChangePasswordResult {
		@JsonProperty("SUCCESS")
		private boolean success;
		@JsonProperty("FAILREASON")
		private String failReason;
		
		public boolean getSuccess(){
			return success;
		}
		
		public String getFailReason(){
			return failReason;
		}
		
		public void setSuccess(boolean val){
			success = val;
		}
		
		public void setFailReason(String val){
			failReason = val;
		}
	}
}