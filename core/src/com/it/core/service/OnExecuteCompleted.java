package com.it.core.service;

import com.it.core.service.exception.WebServiceException;

public interface OnExecuteCompleted {
	public void OnCompleted(String result);
    public void OnError(WebServiceException exception);
}
