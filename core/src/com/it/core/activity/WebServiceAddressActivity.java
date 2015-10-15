package com.it.core.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.it.core.R;
import com.it.core.adapter.WebServiceAddressAdapter;
import com.it.core.application.ApplicationBase;
import com.it.core.model.WebServiceAddress;
import com.it.core.notifications.Dialog;
import com.it.core.serialization.SerializeHelper;
import com.it.core.tools.OnWebServicesChanged;
import com.it.core.tools.PreferenceHelper;

import java.util.ArrayList;

public class WebServiceAddressActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ActionMode.Callback, OnWebServicesChanged {

	private SharedPreferences mPreferences;
	private ArrayList<WebServiceAddress> mAddresses;
	private ListView mListView;
	private WebServiceAddressAdapter mAdapter;
	private Object mActionMode;
	private int mSelectedPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_service_address);
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			// Включить навигацию Вверх/Назад
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		mPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		String jsonList = mPreferences.getString(SettingsActivityBase.WEB_SERVICES_LIST_KEY, "");
		mAddresses = SerializeHelper.deserializeList(jsonList, WebServiceAddress.class);

		if (mAddresses == null || mAddresses.isEmpty()) {
			mAddresses = new ArrayList<WebServiceAddress>();
			mAddresses.add(new WebServiceAddress(getString(R.string.default_), PreferenceHelper.getWebServiceUrl(), true));
			PreferenceHelper.putWebserviceAddresses(mAddresses);
		}

		mListView = (ListView)findViewById(R.id.addresses_list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		refreshAddressList();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PreferenceHelper.selectWebServiceAddress(position, null);
//
//		for (WebServiceAddress address: mAddresses) {
//			address.setIsCurrent(false);
//		}
//		mAddresses.get(position).setIsCurrent(true);
//		refreshAddressPreferences();
//		setResultUrl(mAddresses.get(position).getUrl());
		finish();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mActionMode != null) {
			return false;
		}
		mSelectedPosition = position;
		mListView.setItemChecked(position, true);
		// Start the CAB using the ActionMode.Callback defined above
		mActionMode = startActionMode(this);
		view.setSelected(true);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_web_service_address, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		if (id == R.id.action_add_address) {
			Dialog.showSetDialog(this, this.getString(R.string.new_address), "", "", this.getString(R.string.title), this.getString(R.string.address), new Dialog.OnValueSetListener() {
				@Override
				public void onSet(DialogInterface dialog, final String title, final String url) {
					PreferenceHelper.addWebServiceAddress(WebServiceAddressActivity.this, new WebServiceAddress(title, url), WebServiceAddressActivity.this, true);
				}

				@Override
				public void onCancel(DialogInterface dialog) { }
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		// Inflate a menu resource providing context menu items
		MenuInflater inflater = actionMode.getMenuInflater();
		// Assumes that you have "contexual.xml" menu resources
		inflater.inflate(R.menu.menu_web_service_address_long_click, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		final WebServiceAddress address = mAddresses.get(mSelectedPosition);
		// Если выбрали "Редактировать адрес"
		if (menuItem.getItemId() == R.id.action_edit_address) {
			Dialog.showSetDialog(this, getString(R.string.address_editing), address.getTitle(), address.getUrl(),
					this.getString(R.string.title), getString(R.string.address), new Dialog.OnValueSetListener() {
				@Override
				public void onSet(DialogInterface dialog, final String title, final String url) {
					if (!address.getUrl().equals(url) || !address.getTitle().equals(title)) {
//						NetworkParams.pingWebServices(WebServiceAddressActivity.this, url, new OnGetPingResponse() {
//							@Override
//							public void onGetPingResponse(boolean response) {
//								onUrlChecked(response, title, url, false);
//								mSelectedPosition = -1;
//							}
//						});
						PreferenceHelper.editWebServiceAddress(WebServiceAddressActivity.this, new WebServiceAddress(title, url,
								address.isCurrent()), mSelectedPosition, WebServiceAddressActivity.this, true);
					}
				}

				@Override
				public void onCancel(DialogInterface dialog) { }
			});
			actionMode.finish();
			return true;
		}
		// Если выбрали "Удалить адрес"
		if (menuItem.getItemId() == R.id.action_remove_address) {
			Dialog.showPopupDialog(this, getString(R.string.address_removing), String.format(getString(R.string.remove_string), address.getUrl()),
					false, getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PreferenceHelper.removeWebServiceAddress(mSelectedPosition, WebServiceAddressActivity.this);
//							removeAddress();
//							mSelectedPosition = -1;
						}
					}, null);
			actionMode.finish();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		mActionMode = null;
		mListView.setItemChecked(mSelectedPosition, false);
	}

//	/**
//	 * Проверка корректности ссылки
//	 * @param isCorrect Признак корректности ссылки
//	 * @param title Название (псевдоним) сервера приложений
//	 * @param url Адрес сервера приложений
//	 * @param isNew Признак добавления нового значения
//	 */
//	private void onUrlChecked(boolean isCorrect, String title, String url, boolean isNew) {
//		if (!isCorrect) {
//			Dialog.showPopup(this, R.string.no_connection_with_server, R.string.incorrect_link);
//			return;
//		}
//		setAddress(title, url, isNew);
//	}

//	/**
//	 * Задать адрес
//	 * @param title Название
//	 * @param url Адрес
//	 * @param isNew Новый или отредактированный
//	 */
//	private void setAddress(String title, String url, boolean isNew) {
//		// Проверить задан ли адрес
//		if (url.isEmpty()) {
//			Dialog.showPopup(this, R.string.cant_add_empty_address);
//			return;
//		}
//		// Проверить на наличие такого же адреса
//		for (int i = 0; i < mAddresses.size(); i++) {
//			if (mAddresses.get(i).getUrl().equals(url) &&
//					(isNew || i != mSelectedPosition)) {
//				Dialog.showPopup(this, R.string.address_already_at_list);
//				return;
//			}
//		}
//		if (isNew) {
//			// Добавить адрес
//			mAddresses.add(new WebServiceAddress(title, url));
//		} else {
//			// Отредактировать адрес
//			WebServiceAddress address = mAddresses.get(mSelectedPosition);
//			address.setTitle(title);
//			address.setUrl(url);
//			// Если адрес является текущим, то необходимо установить в результат Activity (чтоб обновить в SettingsActivity)
//			if (address.isCurrent()) {
//				setResultUrl(url);
//			}
//		}
//		// Обновить адреса в списке и настройках
//		refreshAddresses();
//	}

//	/**
//	 * Удалить адрес
// 	 */
//	private void removeAddress() {
//		mAddresses.remove(mSelectedPosition);
//		refreshAddresses();
//	}

//	/**
//	 * Обновить адреса в списке и настройках
//	 */
//	private void refreshAddresses() {
//		refreshAddressPreferences();
//		refreshAddressList();
//	}

//	/**
//	 * Обновить адреса в настройках
//	 */
//	private void refreshAddressPreferences() {
//		mPreferences.edit().putString(SettingsActivityBase.WEB_SERVICES_LIST_KEY, SerializeHelper.serialize(mAddresses)).apply();
//	}

	/**
	 * Обновить список адресов
	 */
	private void refreshAddressList() {
//		if (mAdapter != null) {
//			mAdapter.notifyDataSetChanged();
//		} else {
//			mAdapter = new WebServiceAddressAdapter(this, mAddresses);
//			mListView.setAdapter(mAdapter);
//		}
		mAdapter = new WebServiceAddressAdapter(this, mAddresses);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * Задать адрес в результат Активности
	 * @param url Адрес веб-сервиса
	 */
	private void setResultUrl(String url) {
		Intent data = new Intent();
		data.putExtra(SettingsActivityBase.WEB_SERVICE_URL_PREFERENCE, url);
		setResult(RESULT_OK, data);
	}

	@Override
	public void onWebServicesChanged(ArrayList<WebServiceAddress> addresses) {
		mAddresses = addresses;
		refreshAddressList();
		mSelectedPosition = -1;
	}
}