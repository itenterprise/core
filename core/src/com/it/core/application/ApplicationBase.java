package com.it.core.application;

import java.io.Serializable;
import java.util.Map;

import com.it.core.R;
import com.it.core.activity.ActivityBase;
import com.it.core.activity.LoginActivity;
import com.it.core.model.UserInfo;
import com.it.core.notifications.NotificationProperties;
import com.it.core.model.UserInfo.Credentials;
import com.it.core.tools.CrashReportSender;
import com.it.core.tools.LocaleHelper;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConfigurationException;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
		formKey = "", // This is required for backward compatibility but not used
		socketTimeout = 20000,
		connectionTimeout = 20000,
		mode = ReportingInteractionMode.TOAST,
		forceCloseDialogAfterToast = false // optional, default false
)

public abstract class ApplicationBase extends Application {

	public static final String MAIN_ACTIVITY = "main";

	/**
	 * Данные, которые использовались для входа
	 */
	private Credentials credentials;
	/**
	 * Тикет
	 * Необходимо для отписки от push-уведомлений
	 */
	private String mTicket;
	/**
	 * Экземпляр приложения
	 */
	private static ApplicationBase instance;
	/**
	 * Текущая деятельность
 	 */
	private ActivityBase mActiveActivity;

	/**
	 * Признак необходимости пройти авторизацию
	 */
	private boolean mRequireAuth;

	private boolean mInited;

	/**
	 * Признак нехватки лицензий
	 */
	private boolean mNoLicense;

	/**
	 * Признак необходимости регистрации на push-уведомления
	 */
	private boolean mRegisterForPush;

	/**
	 * Признак вызова сервиса аутентификации
	 */
	private boolean mAuthRequested;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// The following lines triggers the initialization of ACRA
		ACRAConfiguration config = ACRA.getConfig();
		config.setForceCloseDialogAfterToast(false);
		config.setResToastText(R.string.crash_toast_text);
		try	{
			config.setMode(ReportingInteractionMode.TOAST);
		}
		catch (ACRAConfigurationException e) {
			e.printStackTrace();
			return;
		}
		config.setConnectionTimeout(15000);
		config.setSocketTimeout(20000);
		ACRA.setConfig(config);
		ACRA.init(this);
		CrashReportSender reportSender = new CrashReportSender();
		ACRA.getErrorReporter().setReportSender(reportSender);
		ACRA.getErrorReporter().checkReportsOnApplicationStart();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LocaleHelper.refreshLocale();
	}

	public static ApplicationBase getInstance(){
		return instance;
	}

	public void startNewActivity(Activity loginActivity, String category) {
		if (MAIN_ACTIVITY.equals(category)){
            Intent i = new Intent(this, getMainActivityClass());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtras(loginActivity.getIntent());
            startActivity(i);
		}
	}

	public void setActiveActivity(ActivityBase activity){
		mActiveActivity = activity;
	}

	public ActivityBase getActiveActivity(){
		return mActiveActivity;
	}

	public void putCredentials(Credentials cred){
		credentials = cred;
	}

	public Credentials getCredentials() {
		if (credentials == null){
			credentials = UserInfo.getCredentials();
		}
		return credentials;
	}

	public void putTicket(String ticket){
		mTicket = ticket;
	}

	public String getTicket() {
		return mTicket;
	}

	public void navigateToLogin() {
		Intent login = new Intent(this, LoginActivity.class);
		login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(login);
	}

	/**
	 * Имя пакета приложения по умолчанию
	 * Необходимо для возможности использовать стандартные настройки
	 * @return Имя пакета приложения
	 */
	public String getDefaultPackage(){
		return null;
	}

	/**
	 * Идентификатор приложения отправителя
	 * Project Number в Google Developers Console
	 * Необходимо для регистристрации на push-уведомления
	 * @return Идентификатор отправителя
	 */
	public String getPushNotificationSenderId() {
		return null;
	}


	/**
	 * Параметры уведомления (метод должен объязательно возвращать экземпляр объекта NotificationProperties)
	 * @param additional Дополнительные параметры уведомления (например, тип уведомления)
	 * @return Параметры уведомления
	 */
	public NotificationProperties getNotificationProperties(Map<String, Serializable> additional) {
		return new NotificationProperties();
	}

	public void setInited(boolean value) {
		mInited = value;
	}

	public boolean isInited(){
		return mInited;
	}

	public void setNoLicense(boolean value) {
		mNoLicense = value;
	}

	public boolean isNoLicense() {
		return mNoLicense;
	}

	public void setRegisterForPush(boolean value) {
		mRegisterForPush = value;
	}

	public boolean isRegisterForPush() {
		return mRegisterForPush;
	}

	public void setAuthRequested(boolean value) {
		mAuthRequested = value;
	}

	public boolean isAuthRequested() {
		return mAuthRequested;
	}
	
	/**
	 * Получить класс главной деятельности
	 */
	public abstract Class<?> getMainActivityClass();

	/**
	 * Получить класс настроек приложения
	 * @return Класс настроек приложения
	 */
	public Class<?> getSettingsActivityClass() {
		return SettingsActivityBase.class;
	}
	
    /**
     * Нужно ли проверять наличие VPN соединения
     */
    public boolean needVpnConnection(){
        return false;
    }

	/**
	 * В приложении используется вход в систему с помощью учетных данных
	 */
	public boolean isRequireAuth(){
		return true;
	}

	/**
	 * Отображается ли выезжающее меню на на форме входа
	 */
	public boolean hasLoginSlideMenu() {
		return true;
	}

	/**
	 * В приложении пользовательские настройки
	 */
	public boolean hasCustomSettings() {
		return false;
	}

	/**
	 * Инициализация пользовательских настроек
	 */
	public void initCustomSettings() {}

	public boolean isUsingEds() {
		return false;
	}

	/**
	 * Модуль IT-Enterprise, к которому относится приложение
	 */
	public String getSystemModule(){
		return "";
	}

	public String getCurrentObject(){
		return "";
	}

	/**
	 * Идентификатор приложения по умолчанию
	 * Необходимо для регистристрации на push-уведомления
	 * @return Идентификатор приложения
	 */
	public String getApplicationID() {
		return "";
	}


	private boolean mActivityVisible;

	public boolean isActivityVisible() {
		return mActivityVisible;
	}

	public void activityResumed() {
		mActivityVisible = true;
	}

	public void activityPaused() {
		mActivityVisible = false;
	}
}