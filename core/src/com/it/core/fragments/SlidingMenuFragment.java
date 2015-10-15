package com.it.core.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.it.core.R;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SlidingMenuFragment extends Fragment implements
		ExpandableListView.OnChildClickListener, ExpandableListView.OnItemLongClickListener {

	private ExpandableListView sectionListView;

	private SlidingMenuHelper listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SlidingMenuHelper){
			listener = (SlidingMenuHelper)activity;
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.slidingmenu_fragment, container,
				false);
		this.sectionListView = (ExpandableListView) view.findViewById(R.id.slidingmenu_view);
		this.sectionListView.setGroupIndicator(null);

		updateMenu();

		return view;
	}

	public void updateMenu(){
		if (this.getActivity() == null){
			return;
		}
		ArrayList<Section> sections = new ArrayList<Section>();
		createMenu(sections);
		SectionListAdapter sectionListAdapter = new SectionListAdapter(
				this.getActivity(), sections);
		this.sectionListView.setAdapter(sectionListAdapter);

		int count = sectionListAdapter.getGroupCount();
		for (int position = 0; position < count; position++) {
			this.sectionListView.expandGroup(position);
		}

		this.sectionListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent,
										View v, int groupPosition, long id) {
				return true;
			}
		});

		this.sectionListView.setOnChildClickListener(this);
		this.sectionListView.setOnItemLongClickListener(this);
	}

	protected void createMenu(ArrayList<Section> sections){
		if (listener != null){
			listener.createMenu(sections);
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if (listener != null) {
			listener.onMenuItemClick(id);
		}
		return false;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (listener != null) {
			listener.onMenuItemLongClick((Long) view.getTag());
		}
		return false;
	}
}
