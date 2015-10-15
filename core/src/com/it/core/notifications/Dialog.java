package com.it.core.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.application.ApplicationBase;

import java.util.ArrayList;

public class Dialog {

	public static final int NO_TEXT = -1;

	/**
	 * Popup сообщение
	 * @param context Контекст
	 * @param message Сообщение
	 */
	public static void showPopup (Context context, String message) {
		showPopup(context, null, message);
	}

    /**
     * Popup сообщение
     * @param context Контекст
     * @param messageId Идентификатор сообщения в ресурсах
     */
    public static void showPopup (Context context, int messageId) {
        showPopup(context, NO_TEXT, messageId);
    }

	/**
	 * Popup сообщение
	 * @param context Контекст
	 * @param titleId Идентификатор заголовка в ресурсах
	 * @param messageId Идентификатор сообщения в ресурсах
	 */
	public static void showPopup (Context context, int titleId, int messageId){
		if (context == null) { return; }
		createPopup(context,
				titleId == NO_TEXT ? null : context.getString(titleId),
				messageId == NO_TEXT ? null : context.getString(messageId)
		).show();
	}

	/**
	 * Popup сообщение
	 * @param context Контекст
	 * @param title Заголовок
	 * @param message Сообщение
	 */
	public static void showPopup (Context context, String title, String message){
		if (context != null) { createPopup(context, title, message).show(); }
	}

	/**
	 * Popup сообщение
	 * @param context Контекст
	 * @param title Заголовок
	 * @param message Сообщение
	 * @param cancelListener Обработчик закрытия
	 */
	public static void showPopup (Context context, String title, String message, DialogInterface.OnCancelListener cancelListener){
		if (context != null) { createPopup(context, title, message, cancelListener).show(); }
	}

    /**
     * Popup сообщение с возвратом на предыдущую activity
     * @param activity Активность
     * @param messageId Идентификатор сообщения в ресурсах
     */
    public static void showPopupWithBack (final Activity activity, int messageId){
        showPopupWithBack(activity, null, activity.getString(messageId));
    }

    /**
     * Popup сообщение с возвратом на предыдущую activity
     * @param activity Активность
     * @param titleId Идентификатор заголовка в ресурсах
     * @param messageId Идентификатор сообщения в ресурсах
     */
    public static void showPopupWithBack (final Activity activity, int titleId, int messageId){
        showPopupWithBack(activity, activity.getString(titleId), activity.getString(messageId));
    }

	/**
	 * Popup сообщение с возвратом на предыдущую activity
	 * @param activity Активность
	 * @param message Сообщение
	 */
	public static void showPopupWithBack (final Activity activity, String message){
        showPopupWithBack(activity, null, message);
	}

	/**
	 * Popup сообщение с возвратом на предыдущую activity
	 * @param activity Активность
	 * @param title Заголовок
	 * @param message Сообщение
	 */
	public static void showPopupWithBack (final Activity activity, String title, String message){
		AlertDialog dialog = createPopup(activity, title, message);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            	activity.onBackPressed();
            }
        });
		dialog.show();
	}

    /**
     * Popup сообщение о Http ошибке
     * @param activity Активность
     */
    public static void showPopupHttpError (final Activity activity){
        String title = activity.getString(R.string.no_connection);
        boolean needVpn = ApplicationBase.getInstance().needVpnConnection();
        String message = needVpn ? activity.getString(R.string.http_exception_vpn_message)
                          : activity.getString(R.string.no_connection_to_application_server);
        AlertDialog.Builder builder = createDialogBuilder(activity, title, message, null);

        builder.setCancelable(false)
                .setNegativeButton(activity.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        if(needVpn){
            // Кнопка для перехода к настройкам VPN
            builder.setPositiveButton(activity.getString(R.string.open_vpn), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.startActivity(new Intent("android.net.vpn.SETTINGS"));
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

	/**
	 * Popup диалог с кнопкой
	 */
	public static void showPopupDialog (Context context,
	                                    String title,
	                                    String message,
	                                    boolean cancelable,
	                                    String positiveButtonTitle,
	                                    DialogInterface.OnClickListener positiveButtonOnClickListener) {
		showPopupDialog(context, title, message, cancelable, positiveButtonTitle, null, positiveButtonOnClickListener, null);
	}

	/**
	 * Popup диалог типа YESNO
	 * @param context Контекст
	 * @param title Заголовок
	 * @param message Сообщение
	 * @param cancelable Возможность закрыть по клику вне диалога
	 * @param positiveButtonTitle Заголовок кнопки "YES"
	 * @param negativeButtonTitle Заголовок кнопки "NO"
	 * @param positiveButtonOnClickListener Обработчик клика по кнопке "YES"
	 * @param negativeButtonOnClickListener Обработчик клика по кнопке "NO"
	 */
	public static void showPopupDialog (Context context,
	                                    String title,
	                                    String message,
	                                    boolean cancelable,
	                                    String positiveButtonTitle,
	                                    String negativeButtonTitle,
	                                    DialogInterface.OnClickListener positiveButtonOnClickListener,
	                                    DialogInterface.OnClickListener negativeButtonOnClickListener) {
		AlertDialog.Builder builder = createDialogBuilder(context, title, message, null);

		builder.setCancelable(cancelable)
				.setNegativeButton(negativeButtonTitle, negativeButtonOnClickListener)
				.setPositiveButton(positiveButtonTitle, positiveButtonOnClickListener);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(cancelable);
		dialog.show();
	}

	/**
	 * Popup диалог с кнопками "Да"/"Нет"
	 * @param context Контекст
	 */
	public static void showPopupDialog (Context context, @StringRes int titleId, @StringRes int messageId,
	                                    boolean cancelable, @StringRes int positiveButtonTitleId, @StringRes int negativeButtonTitleId,
	                                    DialogInterface.OnClickListener positiveButtonOnClickListener,
	                                    DialogInterface.OnClickListener negativeButtonOnClickListener){
		showPopupDialog(context,
				titleId == NO_TEXT ? null : context.getString(titleId),
				messageId == NO_TEXT ? null : context.getString(messageId),
				cancelable,
				context.getString(positiveButtonTitleId),
				context.getString(negativeButtonTitleId),
				positiveButtonOnClickListener,
				negativeButtonOnClickListener);
	}

	public static void showPicker(Context context, String title, String[] items, DialogInterface.OnClickListener clickListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setItems(items, clickListener);
		builder.setNegativeButton(context.getString(R.string.cancel), null);
		builder.setTitle(title);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static void showPicker(Context context, int titleId, int[] itemsIds, DialogInterface.OnClickListener clickListener) {
		String[] items = new String[itemsIds.length];
		for(int i = 0; i < itemsIds.length; i++){
			items[i] = context.getString(itemsIds[i]);
		}
		showPicker(context, context.getString(titleId), items, clickListener);
	}

	public static void showPicker(Context context, int titleId, ArrayList<Integer> itemsIds, DialogInterface.OnClickListener clickListener) {
		String[] items = new String[itemsIds.size()];
		for(int i = 0; i < itemsIds.size(); i++){
			items[i] = context.getString(itemsIds.get(i));
		}
		showPicker(context, context.getString(titleId), items, clickListener);
	}

	public static void showSetDialog(Context context, String title, String defaultValue1, String defaultValue2,
	                                 String hint1, String hint2, final OnValueSetListener onAddedListener) {
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
		final EditText editText1 = (EditText)textEntryView.findViewById(R.id.alert_dialog_text_entry_edit_text1);
		editText1.setText(defaultValue1);
		editText1.setHint(hint1);
		final EditText editText2 = (EditText)textEntryView.findViewById(R.id.alert_dialog_text_entry_edit_text2);
		editText2.setText(defaultValue2);
		editText2.setHint(hint2);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(textEntryView);
		builder.setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onAddedListener.onSet(dialog, editText1.getText().toString(), editText2.getText().toString());
			}
		});
		builder.setNegativeButton(context.getString(R.string.cancel), null);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static void showSetValueDialog(Context context, String title, String defaultValue,
	                                 String hint, String positiveButtonTitle, String negativeButtonTitle,
	                                 final OnValueSetListener onAddedListener) {
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
		final EditText editText1 = (EditText)textEntryView.findViewById(R.id.alert_dialog_text_entry_edit_text1);
		editText1.setText(defaultValue);
		editText1.setHint(hint);
		textEntryView.findViewById(R.id.alert_dialog_text_entry_edit_text2).setVisibility(View.GONE);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(textEntryView);
		builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onAddedListener.onSet(dialog, editText1.getText().toString(), "");
			}
		});
		builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onAddedListener.onCancel(dialog);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static void showSetValueDialog(Context context, int titleId, int defaultValueId,
	                                      int hintId, int positiveButtonTitleId, int negativeButtonTitleId,
	                                      final OnValueSetListener onAddedListener) {
		showSetValueDialog(context, context.getString(titleId), context.getString(defaultValueId),
				context.getString(hintId), context.getString(positiveButtonTitleId), context.getString(negativeButtonTitleId),
				onAddedListener);
	}

	public static void showSetPasswordDialog(Context context, String title, final OnPasswordSetListener onSetListener) {
		LayoutInflater factory = LayoutInflater.from(context);
		View textEntryView = factory.inflate(R.layout.alert_dialog_set_password, null);
		final EditText passwordEditText = (EditText)textEntryView.findViewById(R.id.alert_dialog_password_edit_text);
		final CheckBox rememberCheckBox = (CheckBox)textEntryView.findViewById(R.id.alert_dialog_check_box);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(textEntryView);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onSetListener.onSet(dialog, passwordEditText.getText().toString(), rememberCheckBox.isChecked());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onSetListener.onCancel(dialog);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static ProgressDialog showProgressDialog(Context context, int progressMessageId) {
		return showProgressDialog(context, context.getString(progressMessageId));
	}

	public static ProgressDialog showProgressDialog(Context context, String progressMessage) {
		return showProgressDialog(context, progressMessage, ProgressDialog.STYLE_SPINNER);
	}

	public static ProgressDialog showProgressDialogLinear(Context context, String progressMessage) {
		return showProgressDialog(context, progressMessage, ProgressDialog.STYLE_HORIZONTAL);
	}

	public static ProgressDialog showProgressDialog(Context context, String progressMessage, int progressStyle) {
		ProgressDialog progress = null;
		if (context != null) {
			progress = new ProgressDialog(context);
			progress.setMessage(progressMessage);
			progress.setProgressStyle(progressStyle);
			progress.setCancelable(false);
			progress.show();
		}
		return progress;
	}
	
	public static void hideProgress(ProgressDialog progress) {
		if (progress != null) {
			progress.dismiss();
			progress = null;
		}
	}

	/**
	 * Создать всплывающее сообщение
	 * @param context Контекст
	 * @param title Заголовок
	 * @param message Сообщение
	 * @return Всплывающее окно
	 */
	private static AlertDialog createPopup (Context context, String title, String message) {
		AlertDialog.Builder builder = createDialogBuilder(context, title, message, null);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	/**
	 * Создать всплывающее сообщение
	 * @param context Контекст
	 * @param title Заголовок
	 * @param message Сообщение
	 * @param cancelListener Обработчик закрытия
	 * @return Всплывающее окно
	 */
	private static AlertDialog createPopup (Context context, String title, String message, DialogInterface.OnCancelListener cancelListener) {
		AlertDialog.Builder builder = createDialogBuilder(context, title, message, cancelListener);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	private static AlertDialog.Builder createDialogBuilder (Context context, String title, String message, DialogInterface.OnCancelListener cancelListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(title != null) {
			TextView titleText = new TextView(context);
			titleText.setText(title);
			titleText.setPadding(10, 10, 10, 10);
			titleText.setGravity(Gravity.CENTER);
			titleText.setTextColor(context.getResources().getColor(R.color.holo_blue_light));
			titleText.setTextSize(23);
			builder.setCustomTitle(titleText);
		}
		if (message != null) {
			TextView messageText = new TextView(context);
			messageText.setText(message);
			messageText.setPadding(10, 10, 10, 10);
			messageText.setGravity(Gravity.CENTER);
			messageText.setTextSize(18);
			builder.setView(messageText);
		}
		if (cancelListener != null) {
			builder.setOnCancelListener(cancelListener);
		}
		return builder;
	}

	public interface OnValueSetListener {
		public void onSet(DialogInterface dialog, String value1, String value2);
		public void onCancel(DialogInterface dialog);
	}

	public interface OnPasswordSetListener {
		public void onSet(DialogInterface dialog, String pass, boolean remember);
		public void onCancel(DialogInterface dialog);
	}
}