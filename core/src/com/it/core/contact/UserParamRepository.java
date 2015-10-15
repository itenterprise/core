package com.it.core.contact;

import android.app.Activity;

import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.model.UserInfo;
import com.it.core.service.IService;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;

/**
 * Класс управления параметрами пользователя
 */
public class UserParamRepository implements OnTaskCompleted {

	private OnUserParamLoaded listener;
	private static UserParamRepository instance;
	private static UserParam userParam;

	private UserParamRepository(){}

	public static UserParamRepository getInstance(){
		if(instance == null){
			instance = new UserParamRepository();
		}
		return instance;
	}

	/**
	 * Получение параметров пользователя
	 * @return Параметры пользователя
	 */
	public UserParam getUserParam(){
		if(userParam == null){
			userParam = new UserParam();
		}
		return userParam;
	}

	/**
	 * Загрузка параметров пользователя
	 * @param activity Активность
	 * @param listener Обработчик получения параметров пользователя
	 */
	public void loadUserParam(Activity activity, OnUserParamLoaded listener) {
		this.listener = listener;

		UserInfo.Credentials credentials = ApplicationBase.getInstance().getCredentials();
		if(credentials == null){
			credentials = UserInfo.getCredentials();
		}
		final String login = credentials.getLogin();

		ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(activity);
		p.setCache(true);
		IService service = ServiceFactory.createService(p);
		service.setOnExecuteCompletedHandler(this);
		service.ExecObject("MP.USERPARAM",new Object() {
			public String userLogin = login;
		}, UserParam.class,activity);
	}

	@Override
	public void onTaskCompleted(Object result) {
		userParam = (UserParam) result;
		if(listener != null){
			listener.onUserParamLoaded();
		}
	}
}