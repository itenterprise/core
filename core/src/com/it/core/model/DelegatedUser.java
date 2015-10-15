package com.it.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Описание пользователя делегирующего права
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DelegatedUser {

	/**
	 * Идентификатор пользователя
	 */
	@JsonProperty("USERID")
	private String userId;

	/**
	 * ФИО пользователя
	 */
	@JsonProperty("USERNAME")
	private String name;


	//region Standard getters & setters
	public String getUserId() { return userId; }
	public void setUserID(String userId) { this.userId = userId; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	//endregion
}
