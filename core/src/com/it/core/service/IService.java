package com.it.core.service;

import android.app.Activity;

public interface IService {
	/**
	 * Вызвать расчет, который вернет объект
	 * @param method Наименование расчета
	 * @param params Параметры для вызова расчета
	 * @param type Тип объекта, который будет возвращен
	 * @param activity Текущая деятельность
	 */
	void ExecObject(String method, Object params, Class<?> type, Activity activity);

	/**
	 * Вызвать расчет, который вернет список
	 * @param method Наименование расчета
	 * @param params Параметры для вызова расчета
	 * @param type Тип объекта списка
	 * @param activity Текущая деятельность
	 */
	void ExecObjects(String method, Object params, Class<?> type, Activity activity);

	/**
	 * Вызвать расчет
	 * @param method Наименование расчета
	 * @param params Параметры для вызова расчета
	 * @param activity Текущая деятельность
	 */
	void Exec(String method, Object params, Activity activity);
	
	/**
	 * Установить обработчик результата выполнения метода
	 * @param handler обработчик
	 */
	void setOnExecuteCompletedHandler(OnTaskCompleted handler);
	
	/**
	 * Установить обработчик, который получит "сырой" json для возможности сохранить в кеше
	 * @param handler обработчик
	 */
	void setOnExecuteCompletedHandlerCaching(OnTaskCompleted handler);
}
