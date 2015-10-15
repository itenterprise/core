package com.it.core.service;

import com.it.core.login.ChangePasswordService;
import com.it.core.login.IChangePasswordService;
import com.it.core.login.ILoginService;
import com.it.core.login.LoginService;

import android.app.Activity;

import com.it.core.session.ISessionUpdateService;
import com.it.core.session.UpdateSessionService;

/**
 * Фабрика объектов для вызова веб-расчетов
 * @author bened
 *
 */
public class ServiceFactory {
	/**
	 * Создать объект для доступа к веб-сервисам
	 * @param serviceParams Параметры объекта
	 * @return Объект для доступа к веб-сервисам
	 */
	public static IService createService(ServiceParams serviceParams) {
		IWebService service = new WebServiceWithReconnect();
		Activity activity = serviceParams.getActivity();
        ProgressParams progressParams = serviceParams.getProgressParams();
		if (progressParams != null) {
			service = new ServiceWithLoading(service, activity, progressParams);
		}
		if (serviceParams.getCache()) {
			service = new ServiceWithCaching(service, activity);
		}
		service.setSkipErrors(serviceParams.isSkipErrors());
		service.setIsAnonymous(serviceParams.isAnonymous());
		return service;
	}

	/**
	 * Создать объект для доступа к веб-сервисам
	 * @return Объект для доступа к веб-сервисам
	 */
	public static IService createService() {
		return new WebServiceWithReconnect();
	}

    /**
     * Создать объект для выполнения входа
     * @param activity Активность с которой пытаемся выполнить вход
     * @return Объект для выполнения входа
     */
	public static ILoginService createLoginService(Activity activity) {
		return new LoginService(activity);
	}

	/**
	 * Создать объект для изменения пароля текущего пользователя
	 * @param activity Активность с которой пытаемся изменить пароль
	 * @return Объект для изменения пароля
	 */
	public static IChangePasswordService createChangePasswordService(Activity activity) {
		return new ChangePasswordService(activity);
	}

    /**
     * Создать объект для обновления сессии
     * @param activity Активность с которой пытаемся обновить сессию
     * @return Объект для обновления сессии
     */
    public static ISessionUpdateService createUpdateSessionService(Activity activity) {
        return new UpdateSessionService(activity);
    }

	/**
	 * Параметры вызова веб-сервиса
	 * @author bened
	 *
	 */
	public static class ServiceParams{
		private Activity activity;
        private ProgressParams progressParams;
		private boolean cache;
		private boolean mSkipErrors;
		private boolean mIsAnonymous;

        public ServiceParams(Activity act) {
            activity = act;
        }

		/**
		 * Отобразить диалог загрузки при вызове веб-метода
		 * @param progressParams параметры для запуска диалога
		 */
		public void setProgressParams(ProgressParams progressParams) {
            this.progressParams = progressParams;
		}

        public void setProgressMessage(String message) {
            progressParams = new ProgressParams(message);
        }

        public void setProgressMessage(int messageId) {
            progressParams = new ProgressParams(activity.getString(messageId));
        }


        ProgressParams getProgressParams() {
            return progressParams;
        }

		Activity getActivity() {
			return activity;
		}
		
		public void setCache(boolean val) {
			cache = val;
		}
		
		boolean getCache() {
			return cache;
		}

		public void setSkipErrors(boolean skipErrors) {
			mSkipErrors = skipErrors;
		}

		public boolean isSkipErrors() {
			return mSkipErrors;
		}

		public void setIsAnonymous(boolean isAnonymous) {
			mIsAnonymous = isAnonymous;
		}

		public boolean isAnonymous() {
			return mIsAnonymous;
		}
	}

    /**
     * Параметры отображения прогресса
     */
    public static class ProgressParams{
        private String loadingMessage;

        public ProgressParams(){
        }

        public ProgressParams(String message){
            loadingMessage = message;
        }

        String getLoadingMessage(){
            return loadingMessage;
        }
    }
}