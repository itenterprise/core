package com.it.core.tools;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.it.core.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Диалог выбора даты/времени
 */
public class DateTimePicker {

    private Context mContext;

	/**
	 * Признак второго (ошибочного) срабатывания OnDateSet (известный баг)
	 */
	private boolean mSecondOnDateSet;

	/**
	 * Признак второго (ошибочного) срабатывания OnTimeSet (известный баг)
	 */
	private boolean mSecondOnTimeSet;

	public DateTimePicker(Context context) {
		mContext = context;
	}

	/**
	 * Задать диалог выбора даты/времени для поля ввода
	 * @param dateTimeEditText Поле ввода даты/времени
	 * @param init Признак необходимости инициализации текущей датой/временем
	 */
	public void setDateTimePicker(EditText dateTimeEditText, boolean init) {
		setDateTimePicker(dateTimeEditText, true, true, init);
	}

	/**
	 * Задать диалог выбора даты/времени для поля ввода
	 * @param dateTimeEditText Поле ввода даты/времени
	 * @param initDate Значение для инициализации
	 */
	public void setDateTimePicker(EditText dateTimeEditText, Date initDate) {
		setDateTimePicker(dateTimeEditText, true, true, initDate, DateTimeFormatter.Format.DATE_TIME_EXT);
	}

	/**
	 * Задать диалог выбора даты для поля ввода
	 * @param dateTimeEditText Поле ввода даты
	 * @param init Признак необходимости инициализации текущей датой
	 */
	public void setDatePicker(EditText dateTimeEditText, boolean init) {
		setDateTimePicker(dateTimeEditText, true, false, init);
	}

	/**
	 * Задать диалог выбора даты для поля ввода
	 * @param dateTimeEditText Поле ввода даты
	 * @param initDate Значение для инициализации
	 */
	public void setDatePicker(EditText dateTimeEditText, Date initDate) {
		setDateTimePicker(dateTimeEditText, true, false, initDate, DateTimeFormatter.Format.DATE_EXT);
	}

	/**
	 * Задать диалог выбора времени для поля ввода
	 * @param dateTimeEditText Поле ввода времени
	 * @param init Признак необходимости инициализации текущим времени
	 */
	public void setTimePicker(EditText dateTimeEditText, boolean init) {
		setDateTimePicker(dateTimeEditText, false, true, init);
	}

	/**
	 * Задать диалог выбора времени для поля ввода
	 * @param dateTimeEditText Поле ввода времени
	 * @param initDate Значение для инициализации
	 */
	public void setTimePicker(EditText dateTimeEditText, Date initDate) {
		setDateTimePicker(dateTimeEditText, false, true, initDate, DateTimeFormatter.Format.TIME);
	}

	/**
	 * Задать диалог выбора даты для поля ввода
	 * @param dateTimeEditText Поле ввода даты
	 * @param pickDate Признак необходимости выбора даты
	 * @param pickTime Признак необходимости выбора времени
	 * @param init Признак необходимости инициализации текущей датой/временем
	 */
	private void setDateTimePicker(final EditText dateTimeEditText, final boolean pickDate, final boolean pickTime, boolean init){
		if(init) {
			dateTimeEditText.setText(getInitValue(pickDate, pickTime));
		}
		final DateTimeFormatter.Format dateFormat = getDateFormat(pickDate, pickTime);
		setDateTimePicker(dateTimeEditText, pickDate, pickTime,
				DateTimeFormatter.getDateFromString(getInitValue(pickDate, pickTime), dateFormat), dateFormat);

//		final Calendar calendar = Calendar.getInstance();
//		dateTimeEditText.setOnTouchListener(new View.OnTouchListener() {
//			// Событие нажатия на поле EditText
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() != MotionEvent.ACTION_DOWN) {
//					return false;
//				}
//				// Указание параметров инициализации
//				String dateTime = dateTimeEditText.getText().toString();
//				Date initDate = (dateTime == null || dateTime.isEmpty()) ? new Date() : DateTimeFormatter.getDateFromString(dateTime, dateFormat);
//				calendar.setTime(initDate);
//				if(pickDate) {
//					showDatePicker(dateTimeEditText, calendar, pickTime);
//				} else if(pickTime){
//					showTimePicker(dateTimeEditText, calendar, false);
//				}
//				return false;
//			}
//		});
	}

	/**
	 * Задать диалог выбора даты для поля ввода
	 * @param dateTimeEditText Поле ввода даты
	 * @param pickDate Признак необходимости выбора даты
	 * @param pickTime Признак необходимости выбора времени
	 * @param initValue Значение для инициализации
	 */
	private void setDateTimePicker(final EditText dateTimeEditText, final boolean pickDate, final boolean pickTime, Date initValue, final DateTimeFormatter.Format dateFormat) {
		if (initValue != null) {
			dateTimeEditText.setText(DateTimeFormatter.getStringDate(initValue, dateFormat));
		}
		final Calendar calendar = Calendar.getInstance();
		dateTimeEditText.setOnTouchListener(new View.OnTouchListener() {
			// Событие нажатия на поле EditText
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() != MotionEvent.ACTION_DOWN) {
					return false;
				}
				// Указание параметров инициализации
				String dateTime = dateTimeEditText.getText().toString();
				Date initDate = (dateTime == null || dateTime.isEmpty()) ? new Date() : DateTimeFormatter.getDateFromString(dateTime, dateFormat);
				calendar.setTime(initDate);
				if (pickDate) {
					showDatePicker(dateTimeEditText, calendar, pickTime);
				} else if (pickTime) {
					showTimePicker(dateTimeEditText, calendar, false);
				}
				return false;
			}
		});
	}

	/**
	 * Показать диалог выбора даты
	 * @param dateTimeEditText Поле ввода дати/времени
	 * @param calendar Календарь с инициализирующей датой/временем
	 * @param pickTime Признак необходимости выбора времени
	 */
	private void showDatePicker(final EditText dateTimeEditText, final Calendar calendar, final boolean pickTime) {
		mSecondOnDateSet = false;
	    // Задание параметров диалога выбора даты
	    DatePickerDialog datePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
	        @Override
	        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	            if(mSecondOnDateSet){
		            return;
	            }
	            dateTimeEditText.setText(DateTimeFormatter.getStringDate(dayOfMonth, monthOfYear + 1, year));
	            if (pickTime) {
	                showTimePicker(dateTimeEditText, calendar, true);
	            }
	            mSecondOnDateSet = true;
	        }
	    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	    datePicker.setTitle(mContext.getString(R.string.date_picker));
	    datePicker.show();
	}

    /**
     * Показать диалог выбора времени
     * @param dateTimeEditText Поле ввода дати/времени
     * @param calendar Календарь с инициализирующей датой/временем
     * @param pickDate Признак необходимости выбора даты
     */
    private void showTimePicker(final EditText dateTimeEditText, Calendar calendar, final boolean pickDate){
	    mSecondOnTimeSet = false;
        // Задание параметров диалога выбора времени
        TimePickerDialog timePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
	            if (mSecondOnTimeSet) {
		            return;
	            }
                String date = "";
                if (pickDate) {
                    date = dateTimeEditText.getText().toString() + " ";
                }
                dateTimeEditText.setText(date + DateTimeFormatter.getStringTime(selectedMinute, selectedHour));
	            mSecondOnTimeSet = true;
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);//Yes 24 hour time
        timePicker.setTitle(mContext.getString(R.string.time_picker));
        timePicker.show();
    }

	/**
	 * Получить значение для инициализации
	 * @param pickDate Признак необходимости выбора даты
	 * @param pickTime Признак необходимости выбора времени
	 * @return Инициализирующее значение
	 */
	private String getInitValue(boolean pickDate, boolean pickTime){
		Calendar currentTime = Calendar.getInstance();
		int year = currentTime.get(Calendar.YEAR);
		int monthOfYear = currentTime.get(Calendar.MONTH) + 1;
		int dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH);
		int hour = currentTime.get(Calendar.HOUR_OF_DAY);
		int minute = currentTime.get(Calendar.MINUTE);
		String initValue = "";
		if (pickDate) {
			initValue = DateTimeFormatter.getStringDate(dayOfMonth, monthOfYear, year);
		}
		if (pickTime) {
			if (!initValue.isEmpty()) {
				initValue += " ";
			}
			initValue += DateTimeFormatter.getStringTime(minute, hour);
		}
		return initValue;
	}

	/**
	 * Получить формат даты
	 * @param pickDate Признак необходимости выбора даты
	 * @param pickTime Признак необходимости выбора времени
	 * @return Формат времени
	 */
	private DateTimeFormatter.Format getDateFormat(boolean pickDate, boolean pickTime){
		DateTimeFormatter.Format format = DateTimeFormatter.Format.DATE_TIME_EXT;
		if (!pickDate) {
			format = DateTimeFormatter.Format.TIME;
		} else if (!pickTime) {
			format = DateTimeFormatter.Format.DATE_EXT;
		}
		return format;
	}
}