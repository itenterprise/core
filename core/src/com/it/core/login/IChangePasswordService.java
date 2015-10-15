package com.it.core.login;

/**
 * Выполнение методов для смены пароля пользователя в системе
 */
public interface IChangePasswordService {
	/**
	 * Установить обработчик результата смены пароля
	 * @param handler Обработчик
	 */
	void setOnPasswordChangedHandler(OnPasswordChanged handler);

	/**
	 * Изменить пароль пользователя
	 * @param oldPassword Старый пароль
	 * @param newPassword Новый пароль
	 */
	void changePassword(String oldPassword, String newPassword);
}