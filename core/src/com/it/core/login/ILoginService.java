package com.it.core.login;

/**
 * Выполнение методов для входа в систему
 * @author bened
 *
 */
public interface ILoginService {
	/**
	 * Установить обработчик результата входа
	 * @param handler обработчик
	 */
	void setOnLoginSuccessHandler(OnLoginCompleted handler);
	
	/**
	 * Выполнить вход
	 * @param login логин 
	 * @param password пароль
	 */
	void login(String login, String password);
	
	/**
	 * Выполнить вход
	 * @param login логин 
	 * @param password пароль
	 * @param remember заполнить логин и пароль в случае успешного входа
	 */
	void login(String login, String password, boolean remember);
	
	/**
	 * Выполнить вход
	 * @param googleLogin логин Google
	 */
	void login(String googleLogin);
}
