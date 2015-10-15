package com.it.core.network;

/**
 * Обработчик завершения загрузки файла
 */
public interface OnFileDownloadCompleted {
	void onFileDownloadCompleted(String destination);
}