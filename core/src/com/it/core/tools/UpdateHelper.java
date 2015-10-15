package com.it.core.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.it.core.R;
import com.it.core.network.FileDownloaderAsync;
import com.it.core.network.OnFileDownloadCompleted;

import java.io.File;

/**
 * Класс для работы с обновлениями приложения
 */
public class UpdateHelper {

	/**
	 * Обновление приложения
	 * @param context Контекст
	 * @param fileName Имя файла обновления
	 */
	public static void updateApplication(final Context context, String fileName) {
		String serverFilePath = PreferenceHelper.getFileUrl(fileName);
		String localFilePath = FileHelper.getTempDir() + "/" + fileName;
		FileDownloaderAsync downloader = new FileDownloaderAsync(context, R.string.new_version_downloading, new OnFileDownloadCompleted() {
			@Override
			public void onFileDownloadCompleted(String destination) {
				if (destination == null || destination.isEmpty()) { return; }
				File updateFile = new File(destination);
				if (!updateFile.exists()) { return; }
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(updateFile), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					context.startActivity(intent);
				} catch (android.content.ActivityNotFoundException e) {	}
			}
		});
		downloader.execute(serverFilePath, localFilePath);
	}
}