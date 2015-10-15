package com.it.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Версия (мобильного клиента, сервера)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItVersion {
	private boolean mIsCurrent;
	private int mMainVersion;
	private int mMajorVersion;
	private int mMinorVersion;
	private boolean mHasModule;
	private boolean mObject;

	public ItVersion(){

	}

	/**
	 * Создать экземпляр с помощью строки (2014.10, ...)
	 * @param version
	 */
	public ItVersion(String version){
		setVersionString(version);
	}

	/**
	 * Установить версию в виде строки
	 * @param version
	 */
	@JsonProperty("VERSION")
	public void setVersionString(String version){
		String[] zones = version.split("\\.");
		if (zones.length > 0){
			mMainVersion = Integer.parseInt(zones[0]);
		}
		if (zones.length > 1){
			mMajorVersion = Integer.parseInt(zones[1]);
		}
		if (zones.length > 2){
			mMinorVersion = Integer.parseInt(zones[2]);
		}
	}

	/**
	 * Установить признак "текущая версия"
	 * @param current
	 */
	@JsonProperty("CURRENT")
	public void setCurrent(boolean current)	{
		mIsCurrent = current;
	}

	/**
	 * Признак текущей версии
	 * @return
	 */
	public boolean isCurrent(){
		return mIsCurrent;
	}

	/**
	 * Номер главной версии (2014, 2015, ...)
	 * @return
	 */
	public int getMainVersion(){
		return mMainVersion;
	}

	/**
	 * Номер СП
	 * @return
	 */
	public int getMajorVersion(){
		return mMajorVersion;
	}

	/**
	 * Номер промежуточного СП
	 * @return
	 */
	public int getMinorVersion(){
		return mMinorVersion;
	}

	@JsonProperty("HASMODULE")
	public void setHasModule(boolean value){
		mHasModule = value;
	}

	public boolean hasModule(){
		return mHasModule;
	}

	@JsonProperty("OBJECT")
	public void setObject(boolean value){

	}

	public boolean isObject(){
		return mObject;
	}

	/**
	 * Сравнить версию сервера и клиента
	 * @param mobileVersion версия клиента
	 * @param itVersion версия сервера
	 * @return признак возможности вызывать расчеты текущего сервера приложений
	 */
	public static boolean compareVersions(ItVersion mobileVersion, ItVersion itVersion){
		// Если не удалось получить версию сервера - считаем, что версия старая
		if (itVersion == null){
			return false;
		}
		// Если версия текущая - считаем, что версия подходить
		if (itVersion.isCurrent()){
			return true;
		}

		// Если основные версии не совпадают - сервер не подходит
		if (itVersion.getMainVersion() != mobileVersion.getMainVersion()){
			return false;
		}
		// Если номер СП на сервере меньше номера СП клиента - сервер не подходит
		if (itVersion.getMajorVersion() < mobileVersion.getMajorVersion()){
			return false;
		}
		return true;
	}

}
