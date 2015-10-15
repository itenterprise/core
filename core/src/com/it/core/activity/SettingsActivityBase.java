package com.it.core.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.zxing.client.android.CaptureActivity;
import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.model.WebServiceAddress;
import com.it.core.serialization.SerializeHelper;
import com.it.core.tools.LocaleHelper;
import com.it.core.tools.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivityBase extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = true;
	private static final int SCAN_BARCODE_REQUEST_CODE = 0;
	private static final int WEB_SERVICE_ADDRESS_REQUEST_CODE = 1;

	public static final String WEB_SERVICE_URL_PREFERENCE = "webServiceAddressPref";
	public static final String WEB_SERVICES_LIST_KEY = "WebServiceAddressList";
	public static final String APP_LANGUAGE_PREFERENCE = "appLanguage";

	public static final String WEB_SERVICE_URL_CHANGED_KEY = "WebServiceAddressChanged";
	public static final String LOCALE_CHANGED_KEY = "LocaleChanged";

	private static String[] sLocaleCodes = {"ru", "uk", "en"};
	private static String[] sLocaleNames = {"Русский", "Українська", "English"};
	private String mOldUrl;
	private String mOldLocale;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		initOldValues(savedInstanceState);
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(R.string.settings);
			// Включить навигацию Вверх/Назад
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		setupPreferencesScreen();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	private void initOldValues(Bundle bundle) {
		if (bundle != null) {
			mOldLocale = bundle.getString("OldLocale");
			mOldUrl = bundle.getString("OldUrl");
		}
		if (mOldLocale == null) {
			mOldLocale = PreferenceHelper.getAppLanguage();
		}
		if (mOldUrl == null) {
			mOldUrl = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance()).getString(WEB_SERVICE_URL_PREFERENCE, "").toLowerCase();
		}
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	public void setupPreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		addPreferencesFromResource(R.xml.preferences);
		for (String s: getPreferenceScreen().getSharedPreferences().getAll().keySet()) {
			bindPreferenceSummaryToValue(findPreference(s));
		}
		Preference webServiceAddress = findPreference(WEB_SERVICE_URL_PREFERENCE);
		if (webServiceAddress != null) {
			webServiceAddress.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(SettingsActivityBase.this, WebServiceAddressActivity.class);
					startActivityForResult(intent, WEB_SERVICE_ADDRESS_REQUEST_CODE);
					return false;
				}
			});
			if (mOldUrl.isEmpty()) {
				mOldUrl = webServiceAddress.getSummary().toString();
				PreferenceHelper.putValue(WEB_SERVICE_URL_PREFERENCE, mOldUrl);
			}
		}
		Preference scanQr = findPreference("scanQr");
		if (scanQr != null) {
			scanQr.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(SettingsActivityBase.this, CaptureActivity.class);
					intent.setAction("com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					startActivityForResult(intent, SCAN_BARCODE_REQUEST_CODE);
					return false;
				}
			});
		}
		ListPreference appLanguage = (ListPreference)findPreference(APP_LANGUAGE_PREFERENCE);
		if (appLanguage != null) {
			appLanguage.setEntries(sLocaleNames);
			appLanguage.setEntryValues(sLocaleCodes);
			appLanguage.setValue(PreferenceHelper.getAppLanguage());
			bindPreferenceSummaryToValue(appLanguage);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		bindPreferenceSummaryToValue(findPreference(key));
		if (key.equals(WEB_SERVICE_URL_PREFERENCE)) {
			ApplicationBase.getInstance().setInited(false);
//			String newUrl = sharedPreferences.getString(key, "").toLowerCase();
//			SettingsActivityBase.this.setResult(newUrl.equals(mOldUrl) ? RESULT_CANCELED : RESULT_OK);
		}
		if (key.equals(APP_LANGUAGE_PREFERENCE)) {
			LocaleHelper.refreshLocale();
			this.recreate();
		}
	}

	@Override
	protected void onSaveInstanceState (Bundle outState) {
		if (outState != null) {
			outState.putString("OldLocale", mOldLocale);
			outState.putString("OldUrl", mOldUrl);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SCAN_BARCODE_REQUEST_CODE) {
				final String newUrl = data.getStringExtra("SCAN_RESULT");
				PreferenceHelper.addWebServiceAddress(this, new WebServiceAddress(newUrl, newUrl, true), null, true);
//				NetworkParams.pingWebServices(this, newUrl, new OnGetPingResponse() {
//					@Override
//					public void onGetPingResponse(boolean response) {
//						if (!response) {
//							Dialog.showPopup(SettingsActivityBase.this, R.string.no_connection_with_server, R.string.scanned_link_not_correct);
//							return;
//						}
//						updateWebServiceUrlList(newUrl);
//						fillWebServiceUrl(newUrl);
//					}
//				});
			}
		}
		if (requestCode == WEB_SERVICE_ADDRESS_REQUEST_CODE) {
//				String newUrl = data.getStringExtra(SettingsActivityBase.WEB_SERVICE_URL_PREFERENCE);
//				fillWebServiceUrl(newUrl);
			bindPreferenceSummaryToValue(findPreference(SettingsActivityBase.WEB_SERVICE_URL_PREFERENCE));
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
		String stringValue = value.toString();
			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
			} else if (preference instanceof EditTextPreference) {
				EditText edit = ((EditTextPreference) preference).getEditText();
				String maskedValue = edit.getTransformationMethod().getTransformation(stringValue, edit).toString();
				preference.setSummary(maskedValue);
			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	public static void bindPreferenceSummaryToValue(Preference preference) {
		if (preference == null || preference instanceof CheckBoxPreference) {
			return;
		}
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),	""));
	}

	/**
	 * Заполнить адрес веб-сервиса
	 * @param url Адрес
	 */
	private void fillWebServiceUrl(String url) {
		if (url == null || url.isEmpty()) {
			return;
		}
		PreferenceHelper.putValue(WEB_SERVICE_URL_PREFERENCE, url);
		refreshPreferenceSummary(WEB_SERVICE_URL_PREFERENCE, url);
	}

	/**
	 * Обновить текущий адрес веб-сервиса в списке
	 * @param newUrl Новый адрес
	 */
	private void updateWebServiceUrlList(String newUrl) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		String jsonList = sharedPref.getString(WEB_SERVICES_LIST_KEY, "");
		ArrayList <WebServiceAddress> addresses = SerializeHelper.deserializeList(jsonList, WebServiceAddress.class);
		if (addresses == null || addresses.isEmpty()) {
			return;
		}
		boolean updated = false;
		for (WebServiceAddress address: addresses) {
			if (address.isCurrent()) {
				address.setUrl(newUrl);
				updated = true;
				break;
			}
		}
		if (updated) {
			sharedPref.edit().putString(SettingsActivityBase.WEB_SERVICES_LIST_KEY, SerializeHelper.serialize(addresses)).apply();
		}
	}

	protected void refreshPreferenceSummary(String key, String value) {
		Preference myPrefText = findPreference(key);
		myPrefText.setSummary(value);
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class WebServiceAddressPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("webServiceAddressPref"));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Обработка нажатия по кнопке "Back"
			case android.R.id.home:
				setActivityResult();
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		setActivityResult();
		super.onBackPressed();
	}

	private void setActivityResult() {
		Intent intent = new Intent();
		int result = RESULT_CANCELED;
		String newUrl = ((String) PreferenceHelper.getValue(WEB_SERVICE_URL_PREFERENCE, "")).toLowerCase();
		String newLocale = PreferenceHelper.getAppLanguage();
		if (!newUrl.equals(mOldUrl)) {
			intent.putExtra(WEB_SERVICE_URL_CHANGED_KEY, true);
			result = RESULT_OK;
		}
		if (!newLocale.equals(mOldLocale)) {
			intent.putExtra(LOCALE_CHANGED_KEY, true);
			result = RESULT_OK;
		}
		SettingsActivityBase.this.setResult(result, intent);
	}
}