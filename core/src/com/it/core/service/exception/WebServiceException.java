package com.it.core.service.exception;

/**
 * Created by bened on 15.05.2014.
 */
public abstract class WebServiceException {
    public abstract String getMessage();

    public static WebServiceException getException(Exception exception) {
	    // Добавил проверку на null, не обрабатывалась ошибка java.net.SocketTimeoutException
	    if (exception == null || exception.getMessage() == null) {
		    return new ExecuteException();
	    }
	    String message = exception.getMessage();
	    if (message.contains("No license")) {
		    return new NoLicenseException();
	    }
	    if (message.contains("doesn't have access to calculation") ||
			    message.contains("doesn&#39;t have access to calculation")) {
		    return new AccessException();
	    }
	    if (message.contains("Calculation not exists")) {
		    return new NoCalculationException();
	    }
        if (message.equals("HTTP_FORBIDDEN_OR_NOT_FOUND")) {
            return new HttpException();
        }
        return new ExecuteException();
    }
}