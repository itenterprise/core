package com.it.core.contact;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.it.core.application.ApplicationBase;
import com.it.core.internalstorage.InternalStorageSerializer;
import com.it.core.service.IService;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;
import com.it.core.tools.PreferenceHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс управления контактами
 */
public class ContactRepository {

	private static ContactRepository mInstance;
	private InternalStorageSerializer mInternalStorage = new InternalStorageSerializer();

	private ContactRepository(){
	}

	public static ContactRepository getInstance(){
		if (mInstance != null) {
			return mInstance;
		}
		mInstance = new ContactRepository();
		return mInstance;
	}

	/**
	 * Обновить контакт авторизированного пользователя (в Account Switcher)
	 * @param activity Активность
	 * @param contactsLoadedListener Обработчик загрузки контактов
	 */
	public void refreshAuthenticatedUser(Activity activity, OnGetContact contactsLoadedListener){
		refresh(activity, ApplicationBase.getInstance().getCredentials().getLogin(), contactsLoadedListener);
	}

	/**
	 * Обновить контакт
	 * @param activity Активность
	 * @param contactId Идентификаторы контактов
	 * @param contactsLoadedListener Обработчик загрузки контактов
	 */
	public void refresh(Activity activity, String contactId, OnGetContact contactsLoadedListener){
		loadContact(activity, getContact(activity, contactId), contactsLoadedListener);
	}

	/**
	 * Получить контакт из InternalStorage
	 * @param contactId Идентификаторы контактов
	 * @return Список контактов
	 */
	public Contact getContact(Context context, String contactId) {
		Contact contact = (Contact)mInternalStorage.getSerializable(context, contactId.toUpperCase());
		if (contact == null) {
			contact = new Contact(contactId);
		}
		return contact;
	}

	/**
	 * Обновить контакт в InternalStorage
	 * @param updatedContact Обновленный контакт
	 */
	public void update(final Context context, final Contact updatedContact, final OnContactUpdated updatedListener){
		mInternalStorage.putSerializable(context, updatedContact.getId().toUpperCase(), updatedContact);
		String serverFilePath = PreferenceHelper.getFileUrl(updatedContact.getPhotoName());
		Picasso picasso = Picasso.with(context);
		picasso.load(serverFilePath).into(new Target() {
			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
				mInternalStorage.saveBitmap(context, updatedContact.getId().toUpperCase(), bitmap);
				updatedListener.onContactUpdated();
			}

			@Override
			public void onBitmapFailed(Drawable drawable) {
				updatedListener.onContactUpdated();
			}

			@Override
			public void onPrepareLoad(Drawable drawable) {

			}
		});
	}

	public Bitmap getContactPhoto(Context context, String contactId){
		return mInternalStorage.getBitmap(context, contactId.toUpperCase());
	}

	/**
	 * Загрузить контакты
	 * @param activity Активность
	 * @param contact Контакт (который нужно загрузить/обновить)
	 * @param listener Обработчик загрузки контакта
	 */
	private void loadContact(final Activity activity, final Contact contact, final OnGetContact listener){
		String method = "DESKTOP.GETUSERS";
		Object params = new Object(){ public List USERS = toDictionary(contact); };
		IService service = ServiceFactory.createService();
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				ArrayList<Contact> updatedContacts = (ArrayList<Contact>) result;
				if (updatedContacts == null || updatedContacts.isEmpty()) {
					return;
				}
				update(activity, updatedContacts.get(0), new OnContactUpdated() {
					@Override
					public void onContactUpdated() {
						if (listener != null) {
							listener.onGetContact();
						}
					}
				});
			}
		});
		service.ExecObjects(method, params, Contact.class, activity);
	}

	private ArrayList toDictionary(final Contact contact){
		ArrayList userIds = new ArrayList();
		userIds.add(new Object(){
			public String USERID = contact.getId();
			public String HASH = contact.getHash();
		});
		return userIds;
	}
}