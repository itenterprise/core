package com.it.core.session;

import android.content.SharedPreferences;

import com.it.core.application.ApplicationBase;
import com.it.core.security.ObscuredSharedPreferences;

public class Session {

    /**
     * Ключ для хранения идентификатора сессии
     */
    private static final String SESSION_ID_KEY = "SESSION_ID";

    /**
     * Идентификатор сессии
     */
    private static String id;

    /**
     * Получить идентификатор сессии
     */
    public static String getId() {
        if (id == null) {
            id = ObscuredSharedPreferences.getSharedPreferences(
                    ApplicationBase.getInstance()).getString(SESSION_ID_KEY, null);
        }
        return id;
    }

    /**
     * Сохранить идентификатор сессии
     */
    public static void setId(String sessionId) {
        id = sessionId;
        SharedPreferences.Editor prefs = ObscuredSharedPreferences.getSharedPreferences(
                ApplicationBase.getInstance()).edit();
        prefs.putString(SESSION_ID_KEY, id);
        prefs.commit();
    }
}