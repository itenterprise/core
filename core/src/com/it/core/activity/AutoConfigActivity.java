package com.it.core.activity;

import java.util.ArrayList;
import java.util.List;

import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.model.WebServiceAddress;
import com.it.core.tools.LocaleHelper;
import com.it.core.tools.PreferenceHelper;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.Menu;

/**
 * Автоконфигурация ссылки для доступа к веб-сервисам
 * @author bened
 *
 */
public class AutoConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_config);
		// Получить URI по которому вызвалось событие
		Intent i = getIntent();
		Uri data = i.getData();
		String scheme = data.getScheme();
		boolean useSecure = scheme.endsWith("s");
		// Создать builder для построения нового URL
		Builder builder = new Builder();
		// Схема
		builder.scheme(useSecure ? "https" : "http");
		List<String> fragments = data.getPathSegments();
		ArrayList<String> list = new ArrayList<String>(fragments);
		String host = data.getAuthority();
		// Хост
		builder.encodedAuthority(host);
		// Получить фрагменты пути и добавить все к URI, кроме первого
		// Первый фрагмент - хост
		for (String fragment: list) {
			builder.appendPath(fragment);
		}
		String lang = data.getQueryParameter("lang");
		if (!TextUtils.isEmpty(lang)) {
			PreferenceHelper.putValue(SettingsActivityBase.APP_LANGUAGE_PREFERENCE, lang);
			LocaleHelper.refreshLocale();
		}
		String db = data.getQueryParameter("db");
		if (!TextUtils.isEmpty(db)) {
			builder.appendQueryParameter("db", db);
		}
		String path = builder.build().toString();
		// Сохранить URL
		PreferenceHelper.addWebServiceAddress(this, new WebServiceAddress(host, path, true), null, false);
//		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
//		editor.putString("webServiceAddressPref", path);
//		editor.commit();

		//TextView url = (TextView)findViewById(R.id.auto_config_url);
		//url.setText(path);
		ApplicationBase.getInstance().putCredentials(null);
		Class mainActivityClass = ApplicationBase.getInstance().getMainActivityClass();
		Intent mainActivity = new Intent(this, mainActivityClass);
		mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mainActivity);
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.auto_config, menu);
		return true;
	}
}
