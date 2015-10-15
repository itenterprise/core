package com.it.core.notifications;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.it.core.network.NetworkParams;
import com.it.core.service.IService;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;

public class PushNotificationsService {

	private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PREFERENCES_FILE = "pushNotificationsPrefs";
	private static final String MOBILE_OS = "ANDROID";
	
	/**
     * Зарегистрировать приложение на серверах GCM
	 * @param activity Активность
	 * @param appId Активность
	 * @param senderId Активность
     */
	public void register(Activity activity, String appId, String senderId) {
		// Если есть соединение - зарегистрировать устройство
		if (!NetworkParams.isNetworkConnected()) { return; }
		registerInBackground(activity, appId, senderId);
	}

	/**
	 * Отписаться от отправки push-уведомлений
	 * @param activity Активность
	 * @param appId Идентификатор приложения
	 */
	public void unregister(final Activity activity, final String appId) {//}, OnTaskCompleted handler) {
		IService service = ServiceFactory.createService();
//		service.setOnExecuteCompletedHandler(handler);
		service.Exec("UNREGUSER", new Object() {
			public String appid = appId;
			public String deviceid = getRegistrationId(activity);
		}, activity);
		clearRegistrationId(activity);
	}

    /**
     * Получить registration ID
     * @param activity Активность
     */
    private void registerInBackground(final Activity activity, final String appId, final String senderId) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
                	final String regid = gcm.register(senderId);
                    if(regid == null){
                        msg = "Error : RegId is null";
                    	return msg;
                    }
                    final String oldRegId = getRegistrationId(activity);
                    if (!oldRegId.isEmpty() && oldRegId.equals(regid)) {
                    	msg = "Error : Old RegId is actual";
                    	return msg;
                    }
                    IService service = ServiceFactory.createService();
            		service.Exec("REGDEVICE", new Object(){
						public String appid = appId;
            			public String deviceid = regid;
            			public String prevdeviceid = oldRegId;
            			public String mobileos = MOBILE_OS;
            			}, activity);
            		storeRegistrationId(activity, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) { }

        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        SharedPreferences prefs = getGcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }
    
    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID.
     */
    @SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        return prefs.getString(PROPERTY_REG_ID, "");
    }

	private void clearRegistrationId(Context context) {
		SharedPreferences prefs = getGcmPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PROPERTY_REG_ID);
		editor.apply();
	}

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }
}