package com.it.core.service;

public interface IWebService extends IService{
	void setSkipErrors(boolean skipErrors);
	void setIsAnonymous(boolean isAnonymous);
}
