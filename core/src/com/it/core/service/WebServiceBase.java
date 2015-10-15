package com.it.core.service;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.it.core.R;
import com.it.core.activity.ActivityBase;
import com.it.core.activity.ExceptionActivity;
import com.it.core.application.ApplicationBase;
import com.it.core.internalstorage.InternalStorageSerializer;
import com.it.core.network.NetworkParams;
import com.it.core.notifications.Dialog;
import com.it.core.serialization.SerializeHelper;
import com.it.core.service.exception.HttpException;
import com.it.core.service.exception.NoLicenseException;
import com.it.core.service.exception.WebServiceException;

public class WebServiceBase implements IWebService, OnExecuteCompleted {
	/**
	 * Обработчик ответа web-сервиса
	 */
	private OnTaskCompleted listener;
	private OnTaskCompleted listenerCaching;
	private ExecType execType;
	private Class<?> resultType;
    private Activity mActivity;
	protected boolean mSkipErrors;
	protected boolean mIsAnonymous;

	public WebServiceBase() {

	}

	public void execute(String method, Object params, Activity activity) {
		mActivity = activity;
		if (NetworkParams.isNetworkConnected()) {
			try {
				WebServiceExecuteExecutor serv = new WebServiceExecuteExecutor(method, params, mIsAnonymous);
				serv.setOnExecuteCompletedListener(this);
				serv.execute();
				return;
			} catch (Exception e) {
			}
		}
		InternalStorageSerializer iss = new InternalStorageSerializer();
		String jsonResult = iss.getJsonObject(activity, method, params);
		if (jsonResult == null) {
			onCompleted(null);
			return;
		}
		Object resultObj = null;
		switch(execType.getValue()) {
			case 1:
				resultObj = SerializeHelper.deserializeList(jsonResult, resultType);
				break;
			case 2:
				resultObj = SerializeHelper.deserialize(jsonResult, resultType);
		}
		onCompleted(resultObj);
	}

	public void onCompleted(Object obj) {
		if (listener != null) {
			listener.onTaskCompleted(obj);
		}
	}

	@Override
	public void ExecObject(String method, Object params, final Class<?> type, Activity activity) {
        mActivity = activity;
		execType = ExecType.Object;
		resultType = type;
		execute(method, params, activity);
	}

	@Override
	public void ExecObjects(String method, Object params, final Class<?> type, Activity activity) {
        mActivity = activity;
		execType = ExecType.Objects;
		resultType = type;
		execute(method, params, activity);
	}
	
	@Override
	public void Exec(String method, Object params, Activity activity) {
        mActivity = activity;
		execType = ExecType.Exec;
		execute(method, params, activity);
	}

	@Override
	public void setOnExecuteCompletedHandler(OnTaskCompleted handler) {
		listener = handler;
	}
	
	@Override
	public void setOnExecuteCompletedHandlerCaching(OnTaskCompleted handler) {
		listenerCaching = handler;
	}

	@Override
	public void OnCompleted(String result) {
		Object resultObj = null;
		if (result != null) {
			switch(execType.getValue()){
            case 0:
                resultObj = result;
                break;
			case 1:
				resultObj = SerializeHelper.deserializeList(result, resultType);
				break;
			case 2:
				resultObj = SerializeHelper.deserialize(result, resultType);
			}
			if(listenerCaching != null){
				listenerCaching.onTaskCompleted(result);
			}
		}
		onCompleted(resultObj);
	}

    @Override
    public void OnError(WebServiceException exception) {
		Activity activity = mActivity != null ? mActivity : ApplicationBase.getInstance().getActiveActivity();
	    if (exception instanceof NoLicenseException) {
		    if (!ApplicationBase.getInstance().isNoLicense()) {
			    ApplicationBase.getInstance().setNoLicense(true);
			    Dialog.showPopupDialog(activity, null, exception.getMessage(), false, activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
					    mActivity.moveTaskToBack(true);
					    mActivity.finish();
					    ApplicationBase.getInstance().setNoLicense(false);
				    }
			    });
		    }
		    return;
	    }
		onCompleted(null);
		if (mSkipErrors || activity == null) {
            return;
        }
        if (exception instanceof HttpException) {
            Dialog.showPopupHttpError(activity);
            return;
        }
		Intent i = new Intent(activity, ExceptionActivity.class);
		i.putExtra(ExceptionActivity.EXCEPTION_EXTRA, exception.getMessage());
		activity.startActivityForResult(i, ActivityBase.REQUEST_CODE_EXCEPTION_RETRY);

//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setMessage(exception.getMessage())
//                .setTitle(mActivity.getString(R.string.Exception))
//                .setCancelable(false)
//                .setPositiveButton(mActivity.getString(R.string.Exit), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        System.exit(0);
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }

	@Override
	public void setSkipErrors(boolean skipErrors) {
		mSkipErrors = skipErrors;
	}

	@Override
	public void setIsAnonymous(boolean isAnonymous) {
		mIsAnonymous = isAnonymous;
	}

	private enum ExecType{
		Exec(0), Objects(1), Object(2);
		
		private int value;
		
		private ExecType(int val){
			value = val;
		}
		
		public int getValue(){
			return value;
		}
	}
}