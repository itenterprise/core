package com.it.core.fragments;

import java.util.ArrayList;

public interface SlidingMenuHelper {
	void createMenu(ArrayList<Section> sections);
	void onMenuItemClick(long id);
	void onMenuItemLongClick(long id);
}
