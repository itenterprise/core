package com.it.core.activity;

import com.it.core.R;
import com.it.core.R.layout;
import com.it.core.R.menu;
import com.it.core.notifications.Dialog;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NoConnectionToApplicationServerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_connection_to_application_server);
		Dialog.showPopupWithBack(this, getString(R.string.no_connection_to_application_server));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

}
