package com.it.core.login;

/**
 * Должен реализовывать класс, который будет обрабатывать результат попытки смены пароля пользователя в системе
 * @author bened
 *
 */
public interface OnPasswordChanged {
	/**
	 * Будет вызван в случае успешной смены пароля
	 */
	void onSuccess();
	
	/**
	 * Будет вызван в случае неудачной смены пароля
	 */
	void onFail(String message);
	
	/**
	 * Будет вызван в случае получения ошибки
	 */
	void onError();
}