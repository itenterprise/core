package com.it.core.delegateduser;

import com.it.core.model.DelegatedUser;

import java.util.ArrayList;

/**
 * Обработчик действий с пользователями делегирующими права
 */
public interface DelegatedUsersHandler {
	public void onUsersLoaded(ArrayList<DelegatedUser> users);
	public void onUserSelected(boolean success);
}
