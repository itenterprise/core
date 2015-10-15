package com.it.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.application.ApplicationBase;

/**
 * Created by bened on 24.07.2014.
 */
public class ExceptionActivity extends ActivityBase {

	public static final String EXCEPTION_EXTRA = "EXCEPTION_MESSAGE";
	public static final String CLEAN_VERSION_FLAG_EXTRA = "CLEAN_VERSION";

	private boolean mCleanVersionOnRetry;

	@Override
	protected void onAfterCreate() {
		super.onAfterCreate();
		setContentView(R.layout.activity_exception);
		createSlidingMenu();
		TextView exceptionView = (TextView)findViewById(R.id.exception_text_view);
		Intent i = getIntent();
		exceptionView.setText(i.getStringExtra(EXCEPTION_EXTRA));
		mCleanVersionOnRetry = i.getBooleanExtra(CLEAN_VERSION_FLAG_EXTRA, false);
		Button retryButton = (Button)findViewById(R.id.retry_button);
		retryButton.setOnClickListener(onRetryClick);
	}

	private View.OnClickListener onRetryClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finishWithOkResult();
		}
	};

	private void finishWithOkResult() {
		if (mCleanVersionOnRetry){
			ApplicationBase.getInstance().setInited(false);
		}
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		finishWithOkResult();
	}

	@Override
	protected boolean needsCheckVersion() {
		return false;
	}
}
