package com.it.core.session;

import android.app.Activity;
import android.app.ProgressDialog;

import com.it.core.R;
import com.it.core.model.UserInfo;
import com.it.core.notifications.Dialog;
import com.it.core.service.OnExecuteCompleted;
import com.it.core.service.WebServiceSessionUpdater;
import com.it.core.service.exception.WebServiceException;

public class UpdateSessionService implements ISessionUpdateService{

    public UpdateSessionService (Activity activity) {
        this.activity = activity;
    }

    /**
     * Деятельность с которой пытаемся обновить сессию
     */
    private Activity activity;

    /**
     * Обработчик результатов входа
     */
    private OnSessionUpdated handler;

    /**
     * Установить обработчик
     */
    public void setOnSessionUpdatedHandler(OnSessionUpdated handler) {
        this.handler = handler;
    }

    /**
     * Обновить идентификатор сессии
     */
    public void update() {
        final ProgressDialog progress = Dialog.showProgressDialog(activity, activity.getString(R.string.progress_dialog_loading_message));
        WebServiceSessionUpdater updater = new WebServiceSessionUpdater(UserInfo.getTicket(), Session.getId());
        updater.setOnExecuteCompletedListener(new OnExecuteCompleted() {
            @Override
            public void OnCompleted(String tempName) {
                onUpdated(progress);
            }

            @Override
            public void OnError(WebServiceException exception) {
                onUpdated(progress);
            }
        });
        updater.execute();
    }

    private void onUpdated(ProgressDialog progress){
        Dialog.hideProgress(progress);
        Session.setId(UserInfo.getTicket());
        handler.onUpdated();
    }
}