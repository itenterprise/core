package com.it.core.tools;

import com.it.core.model.WebServiceAddress;

import java.util.ArrayList;

public interface OnWebServicesChanged {
	public void onWebServicesChanged(ArrayList<WebServiceAddress> addresses);
}
