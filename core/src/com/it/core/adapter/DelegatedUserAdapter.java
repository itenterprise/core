package com.it.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.delegateduser.DelegatedUserRepository;
import com.it.core.model.DelegatedUser;
import com.it.core.tools.TextViewTools;

import java.util.List;

/**
 * Адаптер заполнения списка пользователей делегирующих права
 */
public class DelegatedUserAdapter extends ArrayAdapter<DelegatedUser> {

	private List<DelegatedUser> mUsers;

	public DelegatedUserAdapter(Context context, List<DelegatedUser> users) {
		super(context, R.layout.delegated_user_item, users);
		mUsers = users;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.delegated_user_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.delegated_user_item_name);
			viewHolder.checked = (ImageView)convertView.findViewById(R.id.delegated_user_item_checked_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DelegatedUser user = mUsers.get(position);
		TextViewTools.setText(viewHolder.name, user.getName());
		DelegatedUser currentUser = DelegatedUserRepository.getInstance().getCurrentDelegatedUser();
		int checkVisibility = View.GONE;
		if (currentUser != null && user.getUserId().equals(currentUser.getUserId())) {
			checkVisibility = View.VISIBLE;
		}
		viewHolder.checked.setVisibility(checkVisibility);
		return convertView;
	}

	private static class ViewHolder {
		TextView name;
		ImageView checked;
	}
}