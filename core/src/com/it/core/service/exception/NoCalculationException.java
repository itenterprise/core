package com.it.core.service.exception;

import com.it.core.R;
import com.it.core.application.ApplicationBase;

/**
 * Ошибка отсутствия расчета на сервере
 */
public class NoCalculationException extends WebServiceException {
	@Override
	public String getMessage() {
		return ApplicationBase.getInstance().getString(R.string.calculation_not_exists);
	}
}
