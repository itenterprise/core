package com.it.core.service.exception;

import com.it.core.application.ApplicationBase;
import com.it.core.R;

/**
 * Created by bened on 15.05.2014.
 */
public class NoLicenseException extends WebServiceException {
    private String _message;

    public NoLicenseException(String message){
        _message = message;
    }

    public NoLicenseException(){
        _message = ApplicationBase.getInstance().getString(R.string.no_license_message);
    }

    public String getMessage(){
        return _message;
    }
}
