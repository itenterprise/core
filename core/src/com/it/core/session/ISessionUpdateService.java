package com.it.core.session;

/**
 * Выполнение методов для обновления сессии
 */
public interface ISessionUpdateService {

    /**
     * Установить обработчик результата входа
     * @param handler обработчик
     */
    void setOnSessionUpdatedHandler(OnSessionUpdated handler);

    /**
     * Выполнить обновление
     */
    void update();
}
