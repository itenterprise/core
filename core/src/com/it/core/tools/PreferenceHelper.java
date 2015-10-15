package com.it.core.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.it.core.R;
import com.it.core.activity.SettingsActivityBase;
import com.it.core.application.ApplicationBase;
import com.it.core.model.WebServiceAddress;
import com.it.core.network.NetworkParams;
import com.it.core.network.OnGetPingResponse;
import com.it.core.notifications.Dialog;
import com.it.core.serialization.SerializeHelper;

import java.util.ArrayList;
import java.util.Set;

/**
 * Вспомогательный класс для работы с настройками
 */
public class PreferenceHelper {

	/**
	 * Получить "чистое" значение адреса веб-сервисов
	 * @return Ссылка
	 */
	public static String getPureWebServiceUrl() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		return pref.getString(SettingsActivityBase.WEB_SERVICE_URL_PREFERENCE, "").toLowerCase();
	}

	/**
	 * Получить значение адреса веб-сервисов
	 * @return Ссылка
	 */
	public static String getWebServiceUrl() {
		String url = getPureWebServiceUrl();
		if (url.isEmpty()) {
			return url;
		}
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		String webServiceUrl = "ws/webservice.asmx";
		if (!url.contains(webServiceUrl)) {
			if (!url.endsWith("/")) {
				url = url + "/";
			}
			url = url + webServiceUrl;
		}
		return url;
	}

	/**
	 * Задать адрес веб-сервисов
	 * @param url Ссылка
	 */
	public static void putWebServiceUrl(String url) {
		if (url != null && !url.isEmpty()) {
			putValue(SettingsActivityBase.WEB_SERVICE_URL_PREFERENCE, url);
		}
	}

	/**
	 * Получить ссылку для выгрузки файлов
	 * @return Ссылка
	 */
	public static String getUploadUrl() {
		String url = getWebServiceUrl();
		return url.isEmpty() ? url : url.replace("webservice.asmx", "addfile.ashx");
	}

	/**
	 * Получить ссылку для загрузки файла
	 * @param tempFileName Имя файла во временном каталоге
	 * @return Ссылка
	 */
	public static String getFileUrl(String tempFileName) {
		String url = getWebServiceUrl();
		return url.isEmpty() ? url : url.replace("webservice.asmx", "getfile.ashx?file=") + tempFileName;
	}

	/**
	 * Задать версию приложения из файла Manifest
	 */
	public static void setApplicationVersion(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		try {
			pref.edit().putString("appVersion", ApplicationBase.getInstance().getPackageManager().getPackageInfo(ApplicationBase.getInstance().getPackageName(), 0).versionName).apply();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String getAppLanguage() {
		Context context = ApplicationBase.getInstance();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(SettingsActivityBase.APP_LANGUAGE_PREFERENCE,
				context.getResources().getConfiguration().locale.getLanguage()).toLowerCase();
	}

	/**
	 * Задать значение настройки
	 * @param key Ключ
	 * @param value Значение
	 */
	public static void putValue(String key, Object value) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		SharedPreferences.Editor prefEditor = sharedPref.edit();
		if (value instanceof String) {
			prefEditor.putString(key, (String)value);
		} else if (value instanceof Boolean) {
			prefEditor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Integer) {
			prefEditor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			prefEditor.putLong(key, (Long) value);
		} else if (value instanceof Float) {
			prefEditor.putFloat(key, (Float) value);
		} else if (value instanceof Set) {
			prefEditor.putStringSet(key, (Set<String>) value);
		}
		// Необходимо использовать commit - вносит изменения "синхронно"
		prefEditor.commit();
	}

	/**
	 * Получить значение настройки
	 * @param key Ключ
	 * @param defaultValue Значение по умолчанию
	 * @return Значение
	 */
	public static Object getValue(String key, Object defaultValue) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		Object value = null;
		if (defaultValue instanceof String) {
			value = sharedPref.getString(key, (String)defaultValue);
		} else if (defaultValue instanceof Boolean) {
			value = sharedPref.getBoolean(key, (Boolean)defaultValue);
		} else if (defaultValue instanceof Integer) {
			value = sharedPref.getInt(key, (Integer)defaultValue);
		} else if (defaultValue instanceof Long) {
			value = sharedPref.getLong(key, (Long)defaultValue);
		} else if (defaultValue instanceof Float) {
			value = sharedPref.getFloat(key, (Float)defaultValue);
		} else if (defaultValue instanceof Set) {
			value = sharedPref.getStringSet(key, (Set<String>)defaultValue);
		}
		return value;
	}

	public static void addWebServiceAddress(Activity activity, WebServiceAddress webServiceAddress,
	                                        OnWebServicesChanged changedListener, boolean withDialogs) {
		putWebServiceAddress(activity, webServiceAddress, -1, changedListener, withDialogs);
	}

	public static void editWebServiceAddress(Activity activity, WebServiceAddress webServiceAddress,
	                                         int itemPosition, OnWebServicesChanged changedListener, boolean withDialogs) {
		putWebServiceAddress(activity, webServiceAddress, itemPosition, changedListener, withDialogs);
	}

	public static void removeWebServiceAddress(int itemPosition, OnWebServicesChanged changedListener) {
		ArrayList<WebServiceAddress> addresses = getWebServiceAddresses();
		addresses.remove(itemPosition);
		putWebserviceAddresses(addresses);
		if (changedListener != null) {
			changedListener.onWebServicesChanged(addresses);
		}
	}

	public static void selectWebServiceAddress(int itemPosition, OnWebServicesChanged changedListener) {
		ArrayList<WebServiceAddress> addresses = getWebServiceAddresses();
		selectWebServiceAddress(addresses, itemPosition);
		putWebserviceAddresses(addresses);
		if (changedListener != null) {
			changedListener.onWebServicesChanged(addresses);
		}
	}

	private static void selectWebServiceAddress(ArrayList<WebServiceAddress> addresses, int itemPosition) {
		for (WebServiceAddress address: addresses) {
			address.setIsCurrent(false);
		}
		WebServiceAddress currentAddress = itemPosition < 0 ? addresses.get(addresses.size() -1) : addresses.get(itemPosition);
		currentAddress.setIsCurrent(true);
//		addresses.get(itemPosition).setIsCurrent(true);
		putWebServiceUrl(currentAddress.getUrl());
	}

	private static void putWebServiceAddress(final Activity activity, final WebServiceAddress webServiceAddress,
	                                         final int itemPosition, final OnWebServicesChanged changedListener, boolean withDialogs) {
		// Проверить задан ли адрес
		if (webServiceAddress.getUrl().isEmpty()) {
			Toast.makeText(activity, R.string.cant_add_empty_address, Toast.LENGTH_LONG).show();
//			Dialog.showPopup(activity, R.string.cant_add_empty_address);
			return;
		}
		ArrayList<WebServiceAddress> storedAddresses = getWebServiceAddresses();
		final ArrayList<WebServiceAddress> addresses = storedAddresses != null ? storedAddresses : new ArrayList<WebServiceAddress>();
		// Проверить на наличие такого же адреса
		for (int i = 0; i < addresses.size(); i++) {
			if (addresses.get(i).getUrl().equals(webServiceAddress.getUrl()) && i != itemPosition) {
				Toast.makeText(activity, R.string.address_already_at_list, Toast.LENGTH_LONG).show();
//				Dialog.showPopup(activity, R.string.address_already_at_list);
				return;
			}
		}
		// Проверить корректность адреса
		NetworkParams.pingWebServices(activity, webServiceAddress.getUrl(), withDialogs, new OnGetPingResponse() {
			@Override
			public void onGetPingResponse(boolean response) {
				if (!response) {
					//Dialog.showPopup(activity, R.string.no_connection_with_server, R.string.incorrect_link);
					Toast.makeText(activity, R.string.incorrect_link, Toast.LENGTH_LONG).show();
					return;
				}
				onUrlChecked(addresses, webServiceAddress, itemPosition, changedListener);
			}
		});
	}

	/**
	 * Проверка корректности ссылки
	 * @param addresses Список адресов из настроек
	 * @param webServiceAddress Адрес сервера приложений
	 * @param selectedPosition Позиция адреса в списке (если = -1, тогда это новый адрес)
	 */
	private static void onUrlChecked(ArrayList<WebServiceAddress> addresses, WebServiceAddress webServiceAddress, int selectedPosition, OnWebServicesChanged changedListener) {
		if (selectedPosition < 0) {
			// Добавить адрес
			addresses.add(webServiceAddress);
		} else {
			// Отредактировать адрес
			addresses.set(selectedPosition, webServiceAddress);
		}
		if (webServiceAddress.isCurrent()) {
			selectWebServiceAddress(addresses, selectedPosition);
		}
		// Обновить адреса в настройках
		putWebserviceAddresses(addresses);

		if (changedListener != null) {
			changedListener.onWebServicesChanged(addresses);
		}
	}

	private static ArrayList<WebServiceAddress> getWebServiceAddresses() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		String jsonList = sharedPref.getString(SettingsActivityBase.WEB_SERVICES_LIST_KEY, "");
		return SerializeHelper.deserializeList(jsonList, WebServiceAddress.class);
	}

	public static void putWebserviceAddresses(ArrayList<WebServiceAddress> addresses) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		sharedPref.edit().putString(SettingsActivityBase.WEB_SERVICES_LIST_KEY, SerializeHelper.serialize(addresses)).apply();
	}
}