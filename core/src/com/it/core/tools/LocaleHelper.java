package com.it.core.tools;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.it.core.application.ApplicationBase;

import java.util.Locale;

/**
 * Вспомогательный класс для работы с локализацией
 */
public class LocaleHelper {

	public static void refreshLocale() {
		Resources res = ApplicationBase.getInstance().getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = new Locale(PreferenceHelper.getAppLanguage());
		res.updateConfiguration(conf, dm);
	}
}
