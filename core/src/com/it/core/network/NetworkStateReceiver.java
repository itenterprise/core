package com.it.core.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.it.core.R;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
//		if (intent.getExtras() != null) {
//			if(NetworkParams.isNetworkConnected(context)){
//				Toast.makeText(context, context.getString(R.string.offline_mode), Toast.LENGTH_LONG).show();
//			}
//		}
	}
}
