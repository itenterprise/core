package com.it.core.delegateduser;

import android.app.Activity;

import com.it.core.R;
import com.it.core.model.DelegatedUser;
import com.it.core.serialization.SerializeHelper;
import com.it.core.service.IService;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;
import com.it.core.tools.PreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для работы с пользователями делегирующими права
 */
public class DelegatedUserRepository {

	private static final String DELEGATED_USER_KEY = "DELEGATED_USER";
	private static DelegatedUserRepository sInstance;

	private DelegatedUserRepository(){}

	public static DelegatedUserRepository getInstance(){
		if (sInstance == null) {
			sInstance = new DelegatedUserRepository();
		}
		return sInstance;
	}

	/**
	 * Загрузить список пользователей делегирующих права
	 * @param activity Активность
	 * @param handler Обработчик действий с пользователями делегирующими права
	 */
	public void loadDelegatedUsers(Activity activity, final DelegatedUsersHandler handler) {
		ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(activity);
		p.setProgressParams(new ServiceFactory.ProgressParams(activity.getString(R.string.loading_delegated_users)));
		p.setCache(true);
		IService service = ServiceFactory.createService(p);
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				if (handler != null) {
					handler.onUsersLoaded(result != null
							? (ArrayList<DelegatedUser>) result
							: new ArrayList<DelegatedUser>());
				}
			}
		});
		service.ExecObjects("GETDELEGATEDUSERS", new Object(), DelegatedUser.class, activity);
	}

	/**
	 * Выбрать пользователя чьи права использовать
	 * @param activity Активность
	 * @param user Пользователь делегирующий права
	 * @param handler Обработчик действий с пользователями делегирующими права
	 */
	public void selectUser(Activity activity, final DelegatedUser user, final DelegatedUsersHandler handler) {
		ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(activity);
		p.setProgressParams(new ServiceFactory.ProgressParams(activity.getString(R.string.changing_delegated_user)));
		IService service = ServiceFactory.createService(p);
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				if (result == null) {
					return;
				}
				Map<String, Object> selectResult = (Map<String, Object>) result;
				Object successObj = selectResult.get("SUCCESS");
				boolean success = successObj != null ? (Boolean) successObj : false;
				if (success) {
					setCurrentDelegatedUser(user);
				}
				if (handler != null) {
					handler.onUserSelected(success);
				}
			}
		});
		Map<String, Object> args = new HashMap<String, Object>() {{
			put("USERID", user.getUserId());
		}};
		service.ExecObject("DELEGATEFROMUSER", args, Map.class, activity);
	}

	public DelegatedUser getCurrentDelegatedUser() {
		String jsonDelegatedUser = (String) PreferenceHelper.getValue(DELEGATED_USER_KEY, "");
		return SerializeHelper.deserialize(jsonDelegatedUser, DelegatedUser.class);
	}

	public void clearCurrentDelegatedUser() {
		setCurrentDelegatedUser(null);
	}

	private void setCurrentDelegatedUser(DelegatedUser user) {
		String jsonDelegatedUser = "";
		if (user != null) {
			jsonDelegatedUser = SerializeHelper.serialize(user);
		}
		PreferenceHelper.putValue(DELEGATED_USER_KEY, jsonDelegatedUser);
	}
}
