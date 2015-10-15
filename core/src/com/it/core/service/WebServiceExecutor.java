package com.it.core.service;

import android.os.AsyncTask;
import android.util.Log;

import com.it.core.service.exception.WebServiceException;
import com.it.core.tools.PreferenceHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public abstract class WebServiceExecutor extends AsyncTask <Void, Void, WebServiceExecutor.ExecuteResult> {

	/**
	 * Пространство имен, в котором описан web-сервис
	 */
	private static final int TIMEOUT = 300000;
	protected static final String CHARSET = "UTF-8";
	
	protected String method;
	private OnExecuteCompleted listener;

	public WebServiceExecutor(String method){
		this.method = method;
	}
	
	public void setOnExecuteCompletedListener(OnExecuteCompleted listener) {
		this.listener = listener;
	}
	
	@Override
	protected WebServiceExecutor.ExecuteResult doInBackground(Void... params) {
		try {
			URL url = new URL(getPostUrl());
			//trustEveryone();
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(TIMEOUT);
			con.setConnectTimeout(TIMEOUT);
			con.setRequestMethod("POST");
			fillRequestHeaders(con);
			// Send post request
			con.setDoInput(true);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(getJSONRequestParams());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_FORBIDDEN || responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
				return new FailedExecuteResult(new Exception("HTTP_FORBIDDEN_OR_NOT_FOUND"));
			}
			if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
				return new FailedExecuteResult(new Exception(readResult(con.getErrorStream())));
			}
			return new SuccessExecuteResult(readResult(con.getInputStream()));
		} catch (Exception e) {
			Log.w("IT-CORE", e.toString());
			return new FailedExecuteResult(e);
		}
	}

	@Override
	protected void onPostExecute(ExecuteResult result) {
		if (listener != null && result instanceof SuccessExecuteResult) {
			listener.OnCompleted(((SuccessExecuteResult) result).getResult());
		}
		else if (listener != null && result instanceof FailedExecuteResult) {
			WebServiceException exception = WebServiceException.getException(((FailedExecuteResult) result).getException());
			listener.OnError(exception);
		}
	}

	private String getPostUrl() {
		String serviceUrl = PreferenceHelper.getWebServiceUrl();
		String[] urlParts = serviceUrl.split("\\?");
		String params = urlParts.length == 2 ? urlParts[1] + "&" : "";
		return String.format("%s/%s?%s", urlParts[0], method, params + "pureJSON=");
	}

	private String readResult(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(inputStream));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
				                               String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
				                               String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}

	protected abstract String getJSONRequestParams();

	protected void fillRequestHeaders(HttpURLConnection connection) {
	}

	protected class ExecuteResult {
	}

	protected class FailedExecuteResult extends ExecuteResult {
		private Exception mException;

		public FailedExecuteResult(Exception ex){
	        mException = ex;
	    }

		public Exception getException(){
	        return mException;
	    }
	}

	protected class SuccessExecuteResult extends ExecuteResult {
		private String mResult;

		public SuccessExecuteResult(String result){
			mResult = result;
		}

		public String getResult(){
			return mResult;
		}
	}
}