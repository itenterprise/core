package com.it.core.service.exception;

import com.it.core.R;
import com.it.core.application.ApplicationBase;

public class HttpException extends WebServiceException {
    @Override
    public String getMessage() {
        ApplicationBase applicationBase = ApplicationBase.getInstance();
        return applicationBase.needVpnConnection()
                ? applicationBase.getString(R.string.no_connection_to_application_server)
                : applicationBase.getString(R.string.http_exception_vpn_message);
    }
}
