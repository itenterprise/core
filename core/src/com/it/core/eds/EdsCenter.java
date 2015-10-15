package com.it.core.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Описание ключа ЄЦП
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class EdsCenter implements Serializable {

	public EdsCenter() {}

	public EdsCenter(String name, boolean isCurrent) {
		this.name = name;
		this.isCurrent = isCurrent;
	}

	public EdsCenter(String name) {
		this(name, false);
	}

	/**
	 * Название
	 */
	@JsonProperty("NAME")
	private String name;

	/**
	 * Адрес cmp
	 */
	@JsonProperty("CMP_ADDRESS")
	private String cmpAddress;

	/**
	 * Порт cmp
	 */
	@JsonProperty("CMP_PORT")
	private String cmpPort;

	/**
	 * Адрес tsp
	 */
	@JsonProperty("TSP_ADDRESS")
	private String tspAddress;

	/**
	 * Порт tsp
	 */
	@JsonProperty("TSP_PORT")
	private String tspPort;

	/**
	 * Адрес ocsp
	 */
	@JsonProperty("OCSP_ADDRESS")
	private String ocspAddress;

	/**
	 * Порт ocsp
	 */
	@JsonProperty("OCSP_PORT")
	private String ocspPort;

	/**
	 * Признак: текущий элемент
	 */
	@JsonProperty("IS_CURRENT")
	private boolean isCurrent;

	//region Standard getters & setters
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getCmpAddress() { return cmpAddress; }
	public void setCmpAddress(String cmpAddress) { this.cmpAddress = cmpAddress; }

	public String getCmpPort() { return cmpPort; }
	public void setCmpPort(String cmpPort) { this.cmpPort = cmpPort; }

	public String getTspAddress() { return tspAddress; }
	public void setTspAddress(String tspAddress) { this.tspAddress = tspAddress; }

	public String getTspPort() { return tspPort; }
	public void setTspPort(String tspPort) { this.tspPort = tspPort; }

	public String getOcspAddress() { return ocspAddress; }
	public void setOcspAddress(String ocspAddress) { this.ocspAddress = ocspAddress; }

	public String getOcspPort() { return ocspPort; }
	public void setOcspPort(String ocspPort) { this.ocspPort = ocspPort; }

	public boolean isCurrent() {
		return isCurrent;
	}
	public void setIsCurrent(boolean isCurrent) { this.isCurrent = isCurrent; }
	//endregion
}