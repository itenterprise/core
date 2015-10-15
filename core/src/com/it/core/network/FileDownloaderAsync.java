package com.it.core.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.it.core.R;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Асинхронная загрузка файла
 */
public class FileDownloaderAsync extends AsyncTask<String, String, String>{

	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog mDialog;
	/** application context. */
	private Context mContext;
	private boolean mShowProgress;
	private OnFileDownloadCompleted mListener;
	private int mDownloadMessageId;

	public FileDownloaderAsync(Context context, OnFileDownloadCompleted listener) {
		this(context, true, R.string.progress_dialog_loading_message, listener);
	}

	public FileDownloaderAsync(Context context, boolean showProgress, OnFileDownloadCompleted listener) {
		this(context, showProgress, R.string.progress_dialog_loading_message, listener);
	}

	public FileDownloaderAsync(Context context, int downloadMessageId, OnFileDownloadCompleted listener) {
		this(context, true, downloadMessageId, listener);
	}

	private FileDownloaderAsync(Context context, boolean showProgress, int downloadMessageId, OnFileDownloadCompleted listener) {
		mContext = context;
		mListener = listener;
		mShowProgress = showProgress;
		mDownloadMessageId = downloadMessageId;
	}

	private ProgressDialog createDialog(Context context) {
		mDialog = new ProgressDialog(context);
		mDialog.setMessage(context.getString(mDownloadMessageId));
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setCancelable(false);
		mDialog.show();
		return mDialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mShowProgress) {
			createDialog(mContext);
		}
	}

	@Override
	protected String doInBackground(String... urls) {
		int count;
		String destinationPath;
		try {
	
		URL sourceUrl = new URL(urls[0]);
		destinationPath = urls[1];
		URLConnection connection = sourceUrl.openConnection();
		connection.connect();
	
		int lengthOfFile = connection.getContentLength();
		InputStream input = new BufferedInputStream(sourceUrl.openStream());
		OutputStream output = new FileOutputStream(destinationPath);
		byte data[] = new byte[1024];
		long total = 0;
			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress(""+(int)((total*100)/lengthOfFile));
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			destinationPath = null;
		}
		return destinationPath;
	}
	
	protected void onProgressUpdate(String... progress) {
		if (mShowProgress) {
			mDialog.setProgress(Integer.parseInt(progress[0]));
		}
	}

	@Override
	protected void onPostExecute(String destinationPath) {
		if (mShowProgress && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		mListener.onFileDownloadCompleted(destinationPath);
	}
}