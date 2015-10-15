package com.it.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс адреса веб-сервиса
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class WebServiceAddress {

	public WebServiceAddress() {}

	public WebServiceAddress(String title, String url, boolean isCurrent) {
		this.title = title;
		this.url = url;
		this.isCurrent = isCurrent;
	}

	public WebServiceAddress(String title, String url) {
		this(title, url, false);
	}

	/**
	 * Название
	 */
	@JsonProperty("TITLE")
	private String title;

	/**
	 * Адрес
	 */
	@JsonProperty("URL")
	private String url;

	/**
	 * Признак: текущий элемент
	 */
	@JsonProperty("IS_CURRENT")
	private boolean isCurrent;

	//region Standard getters & setters
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }

	public boolean isCurrent() {
		return isCurrent;
	}
	public void setIsCurrent(boolean isCurrent) { this.isCurrent = isCurrent; }
	//endregion
}