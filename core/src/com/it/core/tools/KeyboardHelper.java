package com.it.core.tools;


import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Класс для работы с виртуальной клавитурой
 */
public class KeyboardHelper {

    /**
     * Спрятать клавиатуру
     * @param activity Активность
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Прятать клавиатуру, если фокус не на EditText
     * @param activity Активность
     * @param event MotionEvent
     * @param superTouchEvent Результат выполнения метода родительского класса
     * @return true/false
     */
    public static boolean toggleKeyboard(Activity activity, MotionEvent event, boolean superTouchEvent) {
        View v = activity.getCurrentFocus();
        if (v instanceof EditText) {
            View w = activity.getCurrentFocus();
            int scrCoordinates[] = new int[2];
            w.getLocationOnScreen(scrCoordinates);
            float x = event.getRawX() + w.getLeft() - scrCoordinates[0];
            float y = event.getRawY() + w.getTop() - scrCoordinates[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                // Спрятать клавиатуру, когда кликнули вне EditText
                hideSoftKeyboard(activity);
	            v.clearFocus();
            }
        }
        return superTouchEvent;
    }
}
