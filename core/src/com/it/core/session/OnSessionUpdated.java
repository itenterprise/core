package com.it.core.session;

/**
 * Должен реализовывать класс, который будет обрабатывать результат попытки обновления сессии
 */
public interface OnSessionUpdated {

    /**
     * Будет вызван при получении ответа
     */
    void onUpdated();
}
