package com.it.core.service.exception;

import com.it.core.R;
import com.it.core.application.ApplicationBase;

/**
 * Created by bened on 15.05.2014.
 */
public class AccessException extends WebServiceException {
    @Override
    public String getMessage() {
        return ApplicationBase.getInstance().getString(R.string.access_exception);
    }
}
