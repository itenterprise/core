package com.it.core.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.service.IService;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;
import com.it.core.tools.PreferenceHelper;

public class NetworkParams {
	
	/**
	 * Проверка интернет соединения (наследуется ActivityBase)
	 */
	public static boolean isNetworkConnected() {
        return isNetworkConnected(ApplicationBase.getInstance());
	}

    /**
     * Проверка интернет соединения
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    return activeNetwork != null && activeNetwork.isConnected();
    }

	/**
	 * Проверка соединения с сервером приложений
	 * @param activity Активность
	 * @param newUrl Новый адрес веб-сервисов
	 * @param pingListener Обработчик проверки соединения
	 */
	public static void pingWebServices(Activity activity, String newUrl, final OnGetPingResponse pingListener) {
		pingWebServices(activity, newUrl, true, pingListener);
	}

	/**
	 * Проверка соединения с сервером приложений
	 * @param activity Активность
	 * @param newUrl Новый адрес веб-сервисов
	 * @param showProgress Признак отображения прогресса
	 * @param pingListener Обработчик проверки соединения
	 */
	public static void pingWebServices(Activity activity, String newUrl, boolean showProgress, final OnGetPingResponse pingListener) {
		final String oldUrl = PreferenceHelper.getPureWebServiceUrl();
		PreferenceHelper.putWebServiceUrl(newUrl);
		ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(activity);
		if (showProgress) {
			p.setProgressParams(new ServiceFactory.ProgressParams(activity.getString(R.string.web_services_url_validation)));
		}
		p.setIsAnonymous(true);
		IService service = ServiceFactory.createService(p);
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object response) {
				boolean result = response != null && ((PingResponse) response).Result;
				PreferenceHelper.putWebServiceUrl(oldUrl);
				pingListener.onGetPingResponse(result);
			}
		});
		service.ExecObject("PING", new Object(), PingResponse.class, activity);
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class PingResponse {

		@JsonProperty("RESULT")
		public boolean Result;
	}
}