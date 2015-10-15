package com.it.core.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Описание сохраненных данных ЕПЦ
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class EdsCredentials {

	public EdsCredentials() {}

	public EdsCredentials(String keyPath, String password) {
		this.keyPath = keyPath;
		this.password = password;
	}

	/**
	 * Путь к файлу приватного ключа
	 */
	@JsonProperty("KEYPATH")
	private String keyPath;

	/**
	 * Пароль приватного ключа
	 */
	@JsonProperty("PASSWORD")
	private String password;

	//region Standard getters & setters
	public String getKeyPath() { return keyPath; }
	public void setKeyPath(String keyPath) { this.keyPath = keyPath; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	//endregion
}
