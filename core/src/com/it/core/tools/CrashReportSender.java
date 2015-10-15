package com.it.core.tools;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.it.core.application.ApplicationBase;
import com.it.core.serialization.SerializeHelper;
import com.it.core.service.IService;
import com.it.core.service.ServiceFactory;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Класс для отправки отчета о непредвиденной ошибке
 */
public class CrashReportSender implements ReportSender {

	private static final int TIMEOUT = 60000;
	private static final String CHARSET = "UTF-8";
//	private Activity mActivity;

	@Override
	public void send(CrashReportData errorContent) throws ReportSenderException {
		try {
			URL url = new URL("https://m.it.ua/ws/webservice.asmx/ExecuteEx?pureJSON=");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(TIMEOUT);
			con.setConnectTimeout(TIMEOUT);
			con.setRequestMethod("POST");
			// Send post request
			con.setDoInput(true);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(getJSONRequestParams("_CRASHREPORT.ADD", createReport(errorContent)));
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
		} catch (Exception e) {
			Log.w("IT-CORE CrashReport", e.toString());
			throw new ReportSenderException("Error while sending " + ACRA.getConfig().reportType()
					+ " report via Http ", e);
		}
	}

	private Object createReport(final CrashReportData errorContent) {
		Time time = new Time();
		time.parse3339(errorContent.getProperty(ReportField.USER_APP_START_DATE));
		final String startDate = DateTimeFormatter.getUTCStringDate(time);
		time.parse3339(errorContent.getProperty(ReportField.USER_CRASH_DATE));
		final String crashDate = DateTimeFormatter.getUTCStringDate(time);
		final Object report = new Object() {
			public String REPORT_ID = errorContent.getProperty(ReportField.REPORT_ID);
			public String USER_LOGIN = ApplicationBase.getInstance().getCredentials().getLogin();
			public int APP_VERSION_CODE = Integer.parseInt(errorContent.getProperty(ReportField.APP_VERSION_CODE));
			public String APP_VERSION_NAME = errorContent.getProperty(ReportField.APP_VERSION_NAME);
			public String PACKAGE_NAME = errorContent.getProperty(ReportField.PACKAGE_NAME);
			public String OPERATION_SYSTEM = "Android";
			public String OPERATION_SYSTEM_VERSION = errorContent.getProperty(ReportField.ANDROID_VERSION);
			public String DEVICE_BRAND = errorContent.getProperty(ReportField.BRAND);
			public String DEVICE_MODEL = errorContent.getProperty(ReportField.PHONE_MODEL);
			public String FILE_PATH = errorContent.getProperty(ReportField.FILE_PATH);
			public String SYSTEM_BUILD = errorContent.getProperty(ReportField.BUILD);
			public String TOTAL_MEMORY = errorContent.getProperty(ReportField.TOTAL_MEM_SIZE);
			public String AVAILABLE_MEMORY = errorContent.getProperty(ReportField.AVAILABLE_MEM_SIZE);
			public String STACK_TRACE = errorContent.getProperty(ReportField.STACK_TRACE);
			public String INITIAL_CONFIGURATION = errorContent.getProperty(ReportField.INITIAL_CONFIGURATION);
			public String CRASH_CONFIGURATION = errorContent.getProperty(ReportField.CRASH_CONFIGURATION);
			public String DISPLAY_CONFIGURATION = errorContent.getProperty(ReportField.DISPLAY);
			public String APPLICATION_START_DATE = startDate;
			public String APPLICATION_CRASH_DATE = crashDate;
			public String DEVICE_FEATURES = errorContent.getProperty(ReportField.DEVICE_FEATURES);
			public String ENVIRONMENT = errorContent.getProperty(ReportField.ENVIRONMENT);
			public String SECURE_SETTINGS = errorContent.getProperty(ReportField.SETTINGS_SECURE);
			public String GLOBAL_SETTINGS = errorContent.getProperty(ReportField.SETTINGS_GLOBAL);
			public String APPLICATION_PREFERENCES = errorContent.getProperty(ReportField.SHARED_PREFERENCES);
		};
		return new Object() { public Object REPORT = report; };
	}

	private String getJSONRequestParams(String calc, Object params) {
		String parameters = "";
		try {
			parameters = String.format("calcId=%s&args=%s&ticket=",
					calc, params != null ? URLEncoder.encode(SerializeHelper.serialize(params), CHARSET): "");
			int index = 0;
			while (parameters.substring(index).contains("Date%28")) {
				int begin = parameters.indexOf("Date%28", index);
				int end = parameters.indexOf("%22", begin);
				parameters = new StringBuilder(parameters).insert(end, "%5c%2F").toString();
				parameters = new StringBuilder(parameters).insert(begin, "%5c%2F").toString();
				index = end;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parameters;
	}
}