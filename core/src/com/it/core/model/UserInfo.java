package com.it.core.model;

import com.it.core.application.ApplicationBase;
import com.it.core.security.ObscuredSharedPreferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Управление учетными данными пользователя
 * @author bened
 *
 */
public class UserInfo {

	/**
	 * Ключ для хранения тикета
	 */
	private static final String TICKET_KEY = "TICKET";
	/**
	 * Ключ для хранения логина
	 */
	private static final String LOGIN_KEY = "LOGIN";
	/**
	 * Ключ для хранения пароля
	 */
	private static final String PASSWORD_KEY = "PASSWORD";
	/**
	 * Ключ для хранения google логина
	 */
	private static final String GOOGLELOGIN_KEY = "GOOGLELOGIN";
	/**
	 * Ключ для хранения ФИО пользователя
	 */
	private static final String USERNAME_KEY = "USERNAME";

	/**
	 * Тикет
	 */
	private static String ticket;
	
	/**
	 * ФИО пользователя
	 */
	private static String userName;

	/**
	 * Удалить тикет
	 */
	public static void removeTicketAndName() {
		putTicketAndName(null, null);
	}
	
	/**
	 * Удалить учетные данные
	 */
	public static void removeCredentials() {
		putCredentials(new Credentials());
	}

	/**
	 * Сохранить тикет
	 */
	public static void putTicketAndName(String ticket, String userName) {
		UserInfo.ticket = ticket;
		UserInfo.userName = userName;
		Editor prefs = ObscuredSharedPreferences.getSharedPreferences(
				ApplicationBase.getInstance()).edit();
		prefs.putString(TICKET_KEY, ticket);
		prefs.putString(USERNAME_KEY, userName);
		prefs.commit();
	}

	/**
	 * Сохранить учетные данные
	 */
	public static void putCredentials(Credentials credentials) {
		Editor prefs = ObscuredSharedPreferences.getSharedPreferences(
				ApplicationBase.getInstance()).edit();
		prefs.putString(LOGIN_KEY, credentials.getLogin());
		prefs.putString(PASSWORD_KEY, credentials.getPassword());
		prefs.putString(GOOGLELOGIN_KEY, credentials.getGoogleLogin());
		prefs.commit();
	}

	/**
	 * Получить тикет
	 */
	public static String getTicket() {
		if (ticket == null) {
			ticket = ObscuredSharedPreferences.getSharedPreferences(
					ApplicationBase.getInstance()).getString(TICKET_KEY, null);
		}
		return ticket;
	}
	
	/**
	 * Получить ФИО
	 */
	public static String getUserName() {
		if (userName == null) {
			userName = ObscuredSharedPreferences.getSharedPreferences(
					ApplicationBase.getInstance()).getString(USERNAME_KEY, null);
		}
		return userName;
	}

	/**
	 * Получить учетные данные
	 * @return Учетные данные пользователя
	 */
	public static Credentials getCredentials() {
		SharedPreferences prefs = ObscuredSharedPreferences
				.getSharedPreferences(ApplicationBase.getInstance());
		Credentials cred = new Credentials();
		cred.setLogin(prefs.getString(LOGIN_KEY, ""));
		cred.setPassword(prefs.getString(PASSWORD_KEY, ""));
		cred.setGoogleLogin(prefs.getString(GOOGLELOGIN_KEY, ""));
		return cred;
	}

	/**
	 * Признак аутентификации пользователя
	 * @return
	 */
	public static boolean isAuthenticated() {
		return getTicket() != null && !getTicket().isEmpty(); //добавил проверку на isEmpty(), была ситуация когда ticket == ""
	}

	/**
	 * Учетные данные пользователя
	 * @author bened
	 *
	 */
	public static class Credentials {
		private String login = "";
		private String password = "";
		private String googleLogin = "";

		public String getLogin() {
			return login;
		}

		public String getPassword() {
			return password;
		}
		
		public String getGoogleLogin() {
			return googleLogin;
		}

		public void setLogin(String val) {
			login = val;
		}

		public void setPassword(String val) {
			password = val;
		}
		
		public void setGoogleLogin(String val) {
			googleLogin = val;
		}
	}
}