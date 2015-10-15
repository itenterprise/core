package com.it.core.tools;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Класс для работы с елементом WebView
 */

public class WebViewTools {

	/**
	 * Загрузка HTML данных в WebView
	 * @param webView Елемент WebView
	 * @param data HTML данные
	 */
	public static void loadData(WebView webView, String data) {
		if (data != null && !data.isEmpty()) {
			WebSettings webSettings = webView.getSettings();
			// Make the zoom controls visible
			webSettings.setBuiltInZoomControls(true);
			webSettings.setSupportZoom(true);
			// Enable Javascript for interaction
			webSettings.setJavaScriptEnabled(true);


			// Allow for touching selecting/deselecting data series
			webView.requestFocusFromTouch();
			// Set the client
			webView.setWebViewClient(new WebViewClient());
			webView.setWebChromeClient(new WebChromeClient());
//			webView.setBackgroundColor(0);
			webView.getSettings().setLoadWithOverviewMode(false);
			webView.getSettings().setUseWideViewPort(true);


			webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
			String script = "<script>" +
//					"document.body.style.margin='0';" +
//					" document.body.style.padding='0';" +
					" document.body.style.fontSize=15+'px';" +
					"</script>";
			webView.loadData(data + script, "text/html; charset=UTF-8", null);
		} else {
			webView.setVisibility(View.GONE);
		}
	}

	/**
	 * Загрузка HTML данных в WebView со специальним форматом
	 * @param webView Елемент WebView
	 * @param data HTML данные
	 */
	public static void loadDataFormatted(WebView webView, String data) {
		if (data != null && !data.isEmpty()) {
			String formattedData = String.format("<body style=\"margin: 0; " +
					"padding: 0; " +
					"color:#B0B0B0; " +
					"font-size: 14px\"> %s</body>", data);
			webView.loadData(formattedData, "text/html; charset=UTF-8", null);
			webView.refreshDrawableState();
			webView.reload();

		} else {
			webView.setVisibility(View.GONE);
		}
	}

	/**
	 * Отключить обработку "длинного клика"
	 * @param webView Елемент WebView
	 */
	public static void disableLongClick(WebView webView) {
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		webView.setClickable(false);
		webView.setLongClickable(false);
		webView.setFocusable(false);
		webView.setFocusableInTouchMode(false);
	}
}