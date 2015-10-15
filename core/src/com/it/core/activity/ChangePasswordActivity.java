package com.it.core.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.it.core.R;
import com.it.core.login.IChangePasswordService;
import com.it.core.login.OnPasswordChanged;
import com.it.core.network.NetworkParams;
import com.it.core.notifications.Dialog;
import com.it.core.service.ServiceFactory;

public class ChangePasswordActivity extends Activity implements OnPasswordChanged {

	// Введенные старый пароль, новый пароль и подтверждение
	private String mOldPassword;
	private String mNewPassword;
	private String mNewPasswordRepeat;

	// Ссылки на контролы ввода старого пароля, нового пароля и подтверждения
	private EditText oldPasswordView;
	private EditText newPasswordView;
	private EditText newPasswordRepeatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Установить layout
		setContentView(R.layout.activity_change_password);
		initView();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_change_password, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case android.R.id.home:
				super.onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void initView() {
		oldPasswordView = (EditText) findViewById(R.id.activity_change_password_old_password);
		newPasswordView = (EditText) findViewById(R.id.activity_change_password_new_password);
		newPasswordRepeatView = (EditText) findViewById(R.id.activity_change_password_new_password_repeat);
		final Context context = this;
		findViewById(R.id.activity_change_password_change_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(NetworkParams.isNetworkConnected()){
					attemptChangePassword();
				} else {
					Dialog.showPopup(context, R.string.no_connection, R.string.cant_change_password);
				}
			}
		});
	}

	private void attemptChangePassword() {
		// Очистить ошибки
		oldPasswordView.setError(null);
		newPasswordView.setError(null);
		newPasswordRepeatView.setError(null);
		// Получить значения логина и пароля
		mOldPassword = oldPasswordView.getText().toString();
		mNewPassword = newPasswordView.getText().toString();
		mNewPasswordRepeat = newPasswordRepeatView.getText().toString();
		View focusView = null;
		boolean cancel = false;
		// Проверить логин/пароль
		if (TextUtils.isEmpty(mOldPassword)) {
			oldPasswordView.setError(getString(R.string.error_field_required));
			focusView = oldPasswordView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mNewPassword)) {
			newPasswordView.setError(getString(R.string.error_field_required));
			focusView = newPasswordView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mNewPasswordRepeat)) {
			newPasswordRepeatView.setError(getString(R.string.error_field_required));
			focusView = newPasswordRepeatView;
			cancel = true;
		}
		if (!mNewPassword.equals(mNewPasswordRepeat)) {
			newPasswordRepeatView.setError(getString(R.string.passwords_do_not_match));
			focusView = newPasswordRepeatView;
			cancel = true;
		}
		// Если есть ошибки, перейкинуть фокус на необходимый элемент
		if (cancel) {
			focusView.requestFocus();
		} else {
			// Попытаться выполнить вход
			IChangePasswordService changePasswordService = ServiceFactory.createChangePasswordService(this);
			changePasswordService.setOnPasswordChangedHandler(this);
			changePasswordService.changePassword(mOldPassword, mNewPassword);
		}
	}

	@Override
	public void onSuccess() {
		Dialog.showPopupWithBack(this, R.string.password_changed);
	}

	@Override
	public void onFail(String message) {
		Dialog.showPopup(this, getString(R.string.password_change_failed), message);
	}

	@Override
	public void onError() {
		Dialog.showPopup(this, R.string.password_change_failed, R.string.no_connection_to_application_server);
	}
}