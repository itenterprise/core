package com.it.core.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import android.widget.SearchView;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.contact.Contact;
import com.it.core.contact.ContactRepository;
import com.it.core.notifications.Dialog;

import java.util.ArrayList;

/**
 * Класс заполнения елементов View
 */

public class ViewTools {

	/**
	 * Получить текст из поля поиска
	 * @return Текст
	 */
	public static String getSearchText(SearchView searchView) {
		return searchView == null ? "" : searchView.getQuery().toString();
	}

	/**
	 * Задать фотографию контакта в ImageView
	 * @param context Контекст
	 * @param view ImageView
	 * @param contactId Идентификатор контакта (логин)
	 */
	public static void setPhoto(Context context, ImageView view, String contactId) {
		if (contactId == null) {
			return;
		}
		final ContactRepository contactManager = ContactRepository.getInstance();
		Bitmap photo = contactManager.getContactPhoto(context, contactId);
		if (photo != null) {
			view.setImageBitmap(photo);
		} else {
			view.setImageResource(R.drawable.ic_user_photo);
		}
	}

	public static void setCircleImageView(ImageView imageView, int color) {
		ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
		drawable.getPaint().setColor(color);
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			imageView.setBackgroundDrawable(drawable);
		} else {
			imageView.setBackground(drawable);
		}
	}

	/**
	 * Задать диалог "Написать письмо/Позвонить"
	 * @param context Контекст
	 * @param textView TextView
	 * @param text Отображаемый текст
	 * @param contactId Идентификатор контакта
	 */
	public static void setPhoneAndEmail(Context context, TextView textView, String text, String contactId){
		TextViewTools.setText(textView, text);
		setPhoneAndEmail(context, textView, contactId);
	}

	/**
	 * Задать диалог "Написать письмо/Позвонить"
	 * @param context Контекст
	 * @param view View
	 * @param contactId Идентификатор контакта
	 */
	public static void setPhoneAndEmail(Context context, View view, String contactId){
		if(contactId == null) {
			return;
		}
		Contact contact = ContactRepository.getInstance().getContact(context, contactId);
		if(contact != null){
			setPhoneAndEmail(context, view, contact.getPhone(), contact.getEmail());
		}
	}

	/**
	 * Задать диалог "Написать письмо/Позвонить"
	 * @param context Контекст
	 * @param textView TextView
	 * @param text Отображаемый текст
	 * @param phone Телефон
	 * @param email Email
	 */
	public static void setPhoneAndEmail(Context context, TextView textView, String text, String phone, final String email){
		TextViewTools.setText(textView, text);
		setPhoneAndEmail(context, (View)textView, phone, email);
	}

	/**
	 * Задать диалог "Написать письмо/Позвонить"
	 * @param context Контекст
	 * @param view View
	 * @param phone Телефон
	 * @param email Email
	 */
	public static void setPhoneAndEmail(final Context context, View view, final String phone, final String email){
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ArrayList<Integer> itemsIds = new ArrayList<Integer>();
				if(email != null && !email.isEmpty()){
					itemsIds.add(R.string.write_email);
				}
				if(phone != null && !phone.isEmpty()){
					itemsIds.add(R.string.make_call);
				}
				if(itemsIds.size() == 0){
					return;
				}
				Dialog.showPicker(context, R.string.choose_action, itemsIds,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent;
								intent = itemsIds.get(which) == R.string.write_email ? new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null)) : new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
								context.startActivity(intent);
							}
						});
			}
		});
	}

	/**
	 * Инициализация контрола "Потяни, чтоб обновить"
	 * @param rootView Корневой елемент
	 * @param swipeLayoutId Идентификатор SwipeRefreshLayout
	 * @param refreshListener Обработчик обновления
	 */
	public static SwipeRefreshLayout initSwipeToRefresh(View rootView, int swipeLayoutId, SwipeRefreshLayout.OnRefreshListener refreshListener) {
		SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(swipeLayoutId);
		swipeLayout.setOnRefreshListener(refreshListener);
		swipeLayout.setColorSchemeResources(
				android.R.color.holo_green_dark,
				android.R.color.holo_red_dark,
				android.R.color.holo_blue_dark,
				android.R.color.holo_orange_dark);
		return swipeLayout;
	}
}