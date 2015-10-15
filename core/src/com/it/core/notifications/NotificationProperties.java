package com.it.core.notifications;

import com.it.core.R;

public class NotificationProperties {
	private String applicationName;
	private int applicationIcon = R.drawable.ic_launcher;
	private Class<?> notificationStartActivity;
	
	/**
	 * Получить название приложения
	 * 
	 * @return
	 */
	public String getApplicationName() {
		return applicationName;
	}
	
	/**
	 * Задать название приложения
	 * 
	 * @param applicationName
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	/**
	 * Получить иконку приложения
	 * 
	 * @return
	 */
	public int getApplicationIcon() {
		return applicationIcon;
	}
	
	/**
	 * Задать иконку приложения
	 * 
	 * @param applicationIcon
	 */
	public void setApplicationIcon(int applicationIcon) {
		this.applicationIcon = applicationIcon;
	}
	
	/**
	 * Получить activity которую запускает уведомление
	 * 
	 * @return
	 */
	public Class<?> getNotificationStartActivity() {
		return notificationStartActivity;
	}
	
	/**
	 * Задать activity которую запускает уведомление
	 * 
	 * @param notificationStartActivity
	 */
	public void setNotificationStartActivity(Class<?> notificationStartActivity) {
		this.notificationStartActivity = notificationStartActivity;
	}
}
