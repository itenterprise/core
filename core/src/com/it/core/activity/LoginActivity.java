package com.it.core.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.fragments.Section;
import com.it.core.login.ILoginService;
import com.it.core.login.LoginByGoogle;
import com.it.core.login.OnLoginCompleted;
import com.it.core.network.NetworkParams;
import com.it.core.notifications.Dialog;
import com.it.core.service.ServiceFactory;

import java.util.ArrayList;

/**
 * Форма входа
 */
public class LoginActivity extends ActivityBase implements OnLoginCompleted {
	// Введенные логин и пароль
	private String login;
	private String password;
	
	// Ссылки на контролы ввода логина и пароля
	private EditText loginView;
	private EditText passwordView;

	@Override
	protected boolean needsCheckVersion() {return false;}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Установить layout
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onAfterCreate() {
		if(ApplicationBase.getInstance().hasLoginSlideMenu()){
			// Создать выезжающее меню
			createSlidingMenu();
		}
		final Context context = this;

		// Получить поля логина и пароля
		loginView = (EditText) findViewById(R.id.login);
		passwordView = (EditText) findViewById(R.id.password);
		// Добавить обработку клика по кнопке "войти"
		findViewById(R.id.sign_in_button).setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(NetworkParams.isNetworkConnected()){
						attemptLogin();
					} else {
						Dialog.showPopup(context, R.string.no_connection, R.string.cant_authenticate);
					}
				}
			});
		findViewById(R.id.sign_in_with_google).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptLoginByGoogle();
			}
		});
	}

	private final OnLoginCompleted onGoogleLoginCompleted = new OnLoginCompleted() {
		@Override
		public void onSuccess(boolean needUpdateSession) {
			ApplicationBase.getInstance().startNewActivity(LoginActivity.this, "main");
		}

		@Override
		public void onFail(String failReason) {
			showErrorMessage(failReason);
		}

		@Override
		public void onError() {
			showErrorMessage(null);
		}

		private void showErrorMessage(String failReason) {
			Dialog.showPopup(LoginActivity.this, getString(R.string.cannot_login_with_google_title),
					TextUtils.isEmpty(failReason) ? getString(R.string.cannot_login_with_google_message) : failReason);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (ApplicationBase.getInstance().hasLoginSlideMenu()) {
			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.title_activity_login);
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}
		super.onCreateOptionsMenu(menu);
		return true;
	}

	/**
	 * Создание пунктов выезжающего меню
	 * @param sections Секции
	 */
	@Override
	public void createMenu(ArrayList<Section> sections) {
		Section s = new Section(getString(R.string.general));
		s.addSectionItem(998, getString(R.string.settings), null);
		sections.add(s);
	}

	/**
	 * Обработчик клика на пункте меню
	 * @param id ID пункта меню
	 */
	@Override
	public void onMenuItemClick(long id) {
		Intent i;
		switch ((int) id) {
		case 998:
			i = new Intent(this, SettingsActivityBase.class);
			startActivityForResult(i, REQUEST_CODE_URL_CHANGED);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR && resultCode == RESULT_OK) {
			attemptLoginByGoogle();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Попытаться выполнить вход
	 */
	public void attemptLogin() {
		// Очистить ошибки
		loginView.setError(null);
		passwordView.setError(null);
		// Получить значения логина и пароля
		login = loginView.getText().toString();
		password = passwordView.getText().toString();
		View focusView = null;
		boolean cancel = false;
		// Проверить логин/пароль
		if (TextUtils.isEmpty(password)) {
			passwordView.setError(getString(R.string.error_field_required));
			focusView = passwordView;
			cancel = true;
		}
		if (TextUtils.isEmpty(login)) {
			loginView.setError(getString(R.string.error_field_required));
			focusView = loginView;
			cancel = true;
		}
		// Если есть ошибки, перейкинуть фокус на необходимый элемент
		if (cancel) {
			focusView.requestFocus();
		} else {
			// Попытаться выполнить вход
			CheckBox remember = (CheckBox)findViewById(R.id.remember_me_checkbox);
			ILoginService loginService = ServiceFactory.createLoginService(this);
			loginService.setOnLoginSuccessHandler(this);
			loginService.login(login, password, remember.isChecked());
		}
	}

	/**
	 * Попытаться выполнить вход по GoogleId
	 */
	public void attemptLoginByGoogle() {
		new LoginByGoogle(LoginActivity.this, onGoogleLoginCompleted).loginByGoogleId();
	}

	/**
	 * В случае успешного входа перейти на Activity
	 */
	@Override
	public void onSuccess(boolean needUpdateSession) {
		((ApplicationBase) getApplication()).startNewActivity(this, "main");
	}

	/**
	 * В случае неуспешного входа показать сообщение об ошибке 
	 */
	@Override
	public void onFail(String failReason) {
		Dialog.showPopup(this, getString(R.string.authentication_failed),
				TextUtils.isEmpty(failReason) ? getString(R.string.wrong_login_password) : failReason);
		passwordView.setText("");
		passwordView.requestFocus();
	}

	/**
	 * В случае возникновения ошибки показать сообщение 
	 */
	@Override
	public void onError() {
		Dialog.showPopup(this, R.string.authentication_failed, R.string.no_connection_to_application_server);
	}

	//Перехватывание нажатия кнопки "back"
	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
}