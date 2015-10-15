package com.it.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.model.WebServiceAddress;
import com.it.core.tools.TextViewTools;

import java.util.List;

/**
 * Адаптер заполнения списка адресов веб-сервисов
 */
public class WebServiceAddressAdapter extends ArrayAdapter<WebServiceAddress> {

	private List<WebServiceAddress> mAddresses;

	public WebServiceAddressAdapter(Context context, List<WebServiceAddress> addresses) {
		super(context, R.layout.service_address_item, addresses);
		mAddresses = addresses;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.service_address_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.service_address_item_title);
			viewHolder.url = (TextView) convertView.findViewById(R.id.service_address_item_url);
			viewHolder.checked = (ImageView)convertView.findViewById(R.id.service_address_item_checked_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		WebServiceAddress item = mAddresses.get(position);
		TextViewTools.setText(viewHolder.title, item.getTitle());
		TextViewTools.setText(viewHolder.url, item.getUrl());
		viewHolder.checked.setVisibility(item.isCurrent() ? View.VISIBLE : View.GONE);
		return convertView;
	}

	private static class ViewHolder {
		TextView title;
		TextView url;
		ImageView checked;
	}
}