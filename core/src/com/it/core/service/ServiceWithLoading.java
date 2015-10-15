package com.it.core.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.it.core.R;

public class ServiceWithLoading implements IWebService {

	IWebService service;
	ProgressDialog progress;
	Context context;
    String progressText;
	//private static Handler handler = new Handler(Looper.getMainLooper());
	
	ServiceWithLoading(IWebService service, Context ctx, ServiceFactory.ProgressParams progressParams){
		this.service = service;
		this.context = ctx;
        this.progressText = progressParams.getLoadingMessage();

	}
	
	@Override
	public void ExecObject(String method, Object params, Class<?> type, Activity activity) {
		showProgress();
		service.ExecObject(method, params, type, activity);
	}

	@Override
	public void ExecObjects(String method, Object params, Class<?> type, Activity activity) {
		showProgress();
		service.ExecObjects(method, params, type, activity);
	}

	@Override
	public void Exec(String method, Object params, Activity activity) {
		showProgress();
		service.Exec(method, params, activity);
	}

	@Override
	public void setOnExecuteCompletedHandler(final OnTaskCompleted handler) {
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				handler.onTaskCompleted(result);
                ServiceWithLoading.this.hideProgress();
			}
		});
	}
	
	@Override
	public void setOnExecuteCompletedHandlerCaching(final OnTaskCompleted handler) {
		service.setOnExecuteCompletedHandlerCaching(handler);
	}

	private void showProgress(){
		if (progress == null && context != null) {
			progress = ProgressDialog.show(context, "", progressText != null ? progressText : context.getString(R.string.progress_dialog_loading_message));
		}
	}

	private void hideProgress(){
		if (progress != null){
			progress.dismiss();
			progress = null;
		}
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
