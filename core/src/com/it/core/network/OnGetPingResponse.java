package com.it.core.network;

/**
 * Обработчик получения ответа от веб метода "PING"
 */
public interface OnGetPingResponse {
	void onGetPingResponse(boolean response);
}