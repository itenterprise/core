package com.it.core.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.it.core.R;

/**
 * Класс автодополняемого елемента
 */
public class AutoCompleteEditText extends AutoCompleteTextView {

	/* Required methods, not used in this implementation */
	public AutoCompleteEditText(Context context) {
		super(context);
		init();
	}

	/* Required methods, not used in this implementation */
	public AutoCompleteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/* Required methods, not used in this implementation */
	public AutoCompleteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		// Set the bounds of the button
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgShowAllButton, null);
		// if the clear button is pressed, fire up the handler. Otherwise do nothing
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				AutoCompleteEditText et = AutoCompleteEditText.this;
				if (et.getCompoundDrawables()[2] == null)
					return false;
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				if (event.getX() > et.getWidth() - et.getPaddingRight()	- imgShowAllButton.getIntrinsicWidth()) {
					showDropDown();
				}
				return false;
			}
		});
	}

	// The image we defined for the clear button
	public Drawable imgShowAllButton = getResources().getDrawable(
			R.drawable.ic_show_all);

	public void setImgShowAllButton(Drawable imgShowAllButton) {
		this.imgShowAllButton = imgShowAllButton;
	}

	public void hideShowAllButton() {
		this.setCompoundDrawables(null, null, null, null);
	}

	public void showShowAllButton() {
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgShowAllButton, null);
	}
}