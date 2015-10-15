package com.it.core.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Информация о пользователе
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Contact implements Serializable {

	public Contact(){}

	public Contact(String id){
		this.id = id;
		this.hash = "";
	}

	/**
	 * Уникальный идентификатор
	 */
	@JsonProperty("USERID")
	private String id;

	/**
	 * Телефон
	 */
	@JsonProperty("PHONE")
	private String phone;

	/**
	 * Електронная почта
	 */
	@JsonProperty("EMAIL")
	private String email;

	/**
	 * Фото (имя в Temp каталоге)
	 */
	@JsonProperty("PHOTO")
	private String photoName;

	/**
	 * Хеш
	 */
	@JsonProperty("HASH")
	private String hash;

	//region Standard getters & setters
	public String getId() { return id.toUpperCase(); }
	public void setId(String id) { this.id = id; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhotoName() { return photoName; }
	public void setPhotoName(String photoName) { this.photoName = photoName; }
	public String getHash() { return hash; }
	public void setHash(String hash) { this.hash = hash; }
	//endregion
}
