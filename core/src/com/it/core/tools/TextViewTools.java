package com.it.core.tools;

import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Класс заполнения елементов TextView
 */
public class TextViewTools {

	/**
	 * Задать текст для TextView
	 * @param view TextView
	 * @param text Текст
	 */
	public static void setText(TextView view, String text) {
		setText(view, text, "", "");
	}

	/**
	 * Задать текст для TextView
	 * @param activity Активность
	 * @param textViewId Идентификатор TextView
	 * @param text Текст
	 */
	public static void setText(Activity activity, int textViewId, String text) {
		setText(activity, textViewId, text, "", "");
	}

	/**
	 * Задать текст для TextView
	 * @param rootView Корневой елемент
	 * @param textViewId Идентификатор TextView
	 * @param text Текст
	 */
	public static void setText(View rootView, int textViewId, String text) {
		setText(rootView, textViewId, text, "", "");
	}

	/**
	 * Задать текст для TextView
	 * @param activity Активность
	 * @param textViewId Идентификатор TextView
	 * @param text Текст
	 * @param prefix Префикс
	 * @param suffix Суффикс
	 */
	public static void setText(Activity activity, int textViewId, String text, String prefix, String suffix) {
		setText((TextView) activity.findViewById(textViewId), text, prefix, suffix);
	}

	/**
	 * Задать текст для TextView
	 * @param rootView Корневой елемент
	 * @param textViewId Идентификатор TextView
	 * @param text Текст
	 * @param prefix Префикс
	 * @param suffix Суффикс
	 */
	public static void setText(View rootView, int textViewId, String text, String prefix, String suffix) {
		setText((TextView) rootView.findViewById(textViewId), text, prefix, suffix);
	}

	/**
	 * Задать текст для TextView
	 * @param view TextView
	 * @param text Текст
	 * @param prefix Префикс
	 * @param suffix Суффикс
	 */
	public static void setText(TextView view, String text, String prefix, String suffix) {
		if (view == null) {
			return;
		}
		if (text == null || text.isEmpty()) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText((prefix != null ? prefix : "") + text + (suffix != null ? suffix : ""));
		}
	}

	/**
	 * Задать текст в HTML для TextView
	 * @param view TextView
	 * @param html HTML текст
	 */
	public static void setHtmlText(TextView view, String html) {
		if(html == null || html.isEmpty()) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText(Html.fromHtml(html));
		}
	}

	/**
	 * Очистить значения текстовых полей
	 * @param activity Активность
	 * @param resourceIds Идентификаторы ресурсов
	 */
	public static void clearTextFields(Activity activity, int[] resourceIds) {
		for(int id: resourceIds) {
			((TextView)activity.findViewById(id)).setText("");
		}
	}
}