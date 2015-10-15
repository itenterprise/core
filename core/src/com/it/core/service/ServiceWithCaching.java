package com.it.core.service;

import android.app.Activity;
import android.content.Context;

import com.it.core.internalstorage.InternalStorageSerializer;

public class ServiceWithCaching implements IWebService{

	IWebService service;
	Context activity;
	private String method;
	private Object params;

	ServiceWithCaching(IWebService service, final Activity activity){
		this.service = service;
		this.activity = activity;
		service.setOnExecuteCompletedHandlerCaching(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				InternalStorageSerializer iss = new InternalStorageSerializer();
				iss.saveJsonObject(activity, method, params, result);
			}
		});
	}
	
	@Override
	public void ExecObject(String method, Object params, Class<?> type, Activity activity) {
		this.method = method;
		this.params = params;
		service.ExecObject(method, params, type, activity);
	}

	@Override
	public void ExecObjects(String method, Object params, Class<?> type, Activity activity) {
		this.method = method;
		this.params = params;
		service.ExecObjects(method, params, type, activity);
	}

	@Override
	public void Exec(String method, Object params, Activity activity) {
		this.method = method;
		this.params = params;
		service.Exec(method, params, activity);
	}

	@Override
	public void setOnExecuteCompletedHandler(final OnTaskCompleted handler) {
		service.setOnExecuteCompletedHandler(handler);
	}
	
	@Override
	public void setOnExecuteCompletedHandlerCaching(final OnTaskCompleted handler) {
		service.setOnExecuteCompletedHandlerCaching(handler);
	}

	@Override
	public void setSkipErrors(boolean skipErrors) {
		service.setSkipErrors(skipErrors);
	}

	@Override
	public void setIsAnonymous(boolean isAnonymous) {
		service.setIsAnonymous(isAnonymous);
	}
}
