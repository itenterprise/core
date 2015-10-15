package com.it.core.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.adapter.DelegatedUserAdapter;
import com.it.core.delegateduser.DelegatedUserRepository;
import com.it.core.delegateduser.DelegatedUsersHandler;
import com.it.core.model.DelegatedUser;
import com.it.core.notifications.Dialog;

import java.util.ArrayList;

public class DelegatedUsersActivity extends Activity implements DelegatedUsersHandler {

	private DelegatedUserAdapter mUserAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delegated_users);
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			// Включить навигацию Вверх/Назад
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		DelegatedUserRepository.getInstance().loadDelegatedUsers(this, this);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onUsersLoaded(ArrayList<DelegatedUser> users) {
		updateUsersList(users);
	}

	@Override
	public void onUserSelected(boolean success) {
		if (success) {
			if (mUserAdapter != null) {
				mUserAdapter.notifyDataSetChanged();
				setResult(RESULT_OK);
				finish();
			}
		} else {
			Dialog.showPopup(this, R.string.cant_change_delegated_user);
		}
	}

	private void updateUsersList(final ArrayList<DelegatedUser> users) {
		TextView noUsersTextView = (TextView) findViewById(R.id.delegated_users_list_no_items);
		ListView usersList = (ListView) findViewById(R.id.delegated_users_list);
		if (users == null || users.isEmpty()) {
			noUsersTextView.setVisibility(View.VISIBLE);
			Dialog.showPopup(this, R.string.no_rights_from_delegated_employees);
			return;
		}
		mUserAdapter = new DelegatedUserAdapter(this, users);
		usersList.setAdapter(mUserAdapter);
		usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DelegatedUser user = users.get(position);
				DelegatedUser currentUser = DelegatedUserRepository.getInstance().getCurrentDelegatedUser();
				if (currentUser != null && user.getUserId().equals(currentUser.getUserId())) {
					finish();
					return;
				}
				DelegatedUserRepository.getInstance().selectUser(DelegatedUsersActivity.this, user, DelegatedUsersActivity.this);
			}
		});
	}
}
