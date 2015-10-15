package com.it.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * Класс результатов инициализации
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitResult {

	/**
	 * Константы, опеределяющие статусы
	 */
	public static final int SUCCESS_STATUS = 0;
	public static final int MODULE_ERROR = 1;
	public static final int PROJECT_ERROR = 2;
	public static final int NEED_NEW_VERSION = 3;
	public static final int NEED_UPDATE_APP = 4;


	private int mStatus;
	private HashMap<String, String> mAdditionalProps;

	/**
	 * Получить статус
	 * @return
	 */
	public int getStatus() {
		return mStatus;
	}

	/**
	 * Установить статус
	 * @param value
	 */
	@JsonProperty("STATUS")
	public void setStatus(int value) {
		mStatus = value;
	}

	/**
	 * Получить доп. инфо
	 * @return
	 */
	public HashMap<String, String> getAdditionalProps() {
		return mAdditionalProps;
	}

	/**
	 * Установить доп. инфо
	 * @param props
	 */
	@JsonProperty("ADDITIONAL")
	public void setAdditionalProps(HashMap<String, String> props){
		mAdditionalProps = props;
	}

	/**
	 * Получить ссылку на приложение в PlayMarket
	 * @return
	 */
	public String getStoreUrl() {
		if (mAdditionalProps != null && mAdditionalProps.containsKey("ANDROIDSTORE")) {
			return mAdditionalProps.get("ANDROIDSTORE");
		}
		return "";
	}

	/**
	 * Получить ссылку автоконфигурации
	 * @return
	 */
	public String getConfigUrl() {
		if (mAdditionalProps != null && mAdditionalProps.containsKey("URL")) {
			return mAdditionalProps.get("URL");
		}
		return "";
	}

	/**
	 * Получить имя файла обновления
	 * @return
	 */
	public String getUpdateFileName() {
		if (mAdditionalProps != null && mAdditionalProps.containsKey("FILENAME")) {
			return mAdditionalProps.get("FILENAME");
		}
		return "";
	}
}