package com.it.core.attachment;

import android.app.Activity;
import android.app.ProgressDialog;

import com.it.core.R;
import com.it.core.network.FileDownloaderAsync;
import com.it.core.network.FileUploaderAsync;
import com.it.core.network.OnFileDownloadCompleted;
import com.it.core.notifications.Dialog;
import com.it.core.service.IService;
import com.it.core.service.OnExecuteCompleted;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;
import com.it.core.service.exception.WebServiceException;
import com.it.core.tools.FileHelper;
import com.it.core.tools.PreferenceHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * Класс для работы с вложениями
 */
public class AttachmentRepository {

	/**
	 * Активность
	 */
	private Activity mActivity;

	public AttachmentRepository(Activity activity){
		mActivity = activity;
	}

	/**
	 * Получить список вложений
	 * @param tableParam Код таблицы
	 * @param keyValueParam Значение ключа строки
	 * @param progressMessage Сообщение прогресса загрузки
	 * @param gotListListener Обработчик получения списка вложений
	 */
	public void getList(final String tableParam, final String keyValueParam, String progressMessage, final OnAttachmentsListLoaded gotListListener){
		ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(mActivity);
		if(progressMessage!=null && !progressMessage.isEmpty()){
			p.setProgressParams(new ServiceFactory.ProgressParams(progressMessage));
		}
		IService service = ServiceFactory.createService(p);
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				if(gotListListener != null){
					gotListListener.onAttachmentsListLoaded((ArrayList<Attachment>)result);
				}
			}
		});
		service.ExecObjects("GETALLATTACHMENTS", new Object() {
			public String table = tableParam;
			public String keyValue = keyValueParam;
		}, Attachment.class, mActivity);
	}

	/**
	 * Открыть вложение
	 * @param tableParam Код таблицы
	 * @param keyValueParam Значение ключа строки
	 * @param ndorParam Номер вложения
	 * @param progressMessage Сообщение прогресса загрузки
	 */
	public void open(final String tableParam, final String keyValueParam, final int ndorParam, String progressMessage) {
		ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(mActivity);
		if (progressMessage!=null && !progressMessage.isEmpty()) {
			p.setProgressParams(new ServiceFactory.ProgressParams(progressMessage));
		}
		IService service = ServiceFactory.createService(p);
		service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object result) {
				AttachmentTempFile tempFile = (AttachmentTempFile) result;
				if (tempFile != null && tempFile.FileName != null) {
					downloadAttachment(tempFile.FileName);
				} else {
					Dialog.showPopup(mActivity, R.string.no_permissions_for_attachment);
				}
			}
		});
		service.ExecObject("GETATTACHMENT", new Object() {
			public String table = tableParam;
			public String keyValue = keyValueParam;
			public int ndor = ndorParam;
		}, AttachmentTempFile.class, mActivity);
	}

	/**
	 * Добавление вложения
	 * @param tableParam Код таблицы
	 * @param keyValueParam Значение ключа строки
	 * @param file Файл, который добавляется
	 * @param name Имя заданое пользователем
	 * @param progressMessage Сообщение прогресса загрузки
	 * @param addedListener Обработчик добавления вложения
	 */
	public void add(final String tableParam, final String keyValueParam, final File file, final String name, final String progressMessage, final OnAttachmentAdded addedListener) {
		final ProgressDialog uploadProgress = Dialog.showProgressDialog(mActivity, mActivity.getString(R.string.file_upload));
		uploadAttachment(file, new OnExecuteCompleted() {
			@Override
			public void OnCompleted(String tempName) {
				Dialog.hideProgress(uploadProgress);
				if (tempName.equals("error")) {
					Dialog.showPopup(mActivity, mActivity.getString(R.string.file_upload_error));
					return;
				}
				String attachmentName = name.isEmpty() ? file.getName() : name + "." + FileHelper.getExtension(tempName);
				addAttachment(tableParam, keyValueParam, attachmentName, tempName, progressMessage, addedListener);
			}

			@Override
			public void OnError(WebServiceException exception) {
				Dialog.hideProgress(uploadProgress);
				Dialog.showPopup(mActivity, mActivity.getString(R.string.file_upload_error));
			}
		});
	}

	/**
	 * Удаление вложения
	 * @param tableParam Код таблицы
	 * @param keyValueParam Значение ключа строки
	 * @param ndorParam Номер вложения
	 * @param progressMessage Сообщение прогресса загрузки
	 * @param deletedListener Обработчик удаления вложения
	 */
	public void delete(final String tableParam, final String keyValueParam, final int ndorParam, String progressMessage, final OnAttachmentDeleted deletedListener){
		try
		{
			ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(mActivity);
			if(progressMessage!=null && !progressMessage.isEmpty()){
				p.setProgressParams(new ServiceFactory.ProgressParams(progressMessage));
			}
			p.setCache(false);
			IService service = ServiceFactory.createService(p);
			service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
				@Override
				public void onTaskCompleted(Object result) {
					if(result!= null && deletedListener != null){
						deletedListener.onAttachmentDeleted(Boolean.valueOf((String)result));
					}
				}
			});
			service.Exec("DELETEATTACHMENT", new Object() {
				public String table = tableParam;
				public String keyValue = keyValueParam;
				public int ndor = ndorParam;
			}, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.showPopup(mActivity, mActivity.getString(R.string.cant_delete_file));
		}
	}

	/**
	 * Загрузка файла с сервера
	 * @param fileName Имя файла во временном web-каталоге
	 */
	private void downloadAttachment(String fileName){
		String serverFilePath = PreferenceHelper.getFileUrl(fileName);
		String localFilePath = FileHelper.getTempDir() + "/" + fileName;
		FileDownloaderAsync downloader = new FileDownloaderAsync(mActivity, new OnFileDownloadCompleted() {
			@Override
			public void onFileDownloadCompleted(String destinationPath) {
				FileHelper.openFile(mActivity, destinationPath);
			}
		});
		downloader.execute(serverFilePath, localFilePath);
	}

	/**
	 * Выгрузка файла на сервер
	 * @param file Файл
	 * @param uploadedListener Обработчик выгрузки файла вложения
	 */
	private void uploadAttachment(File file, final OnExecuteCompleted uploadedListener){
		FileUploaderAsync uploadService = new FileUploaderAsync(uploadedListener);
		String uploadUrl = PreferenceHelper.getUploadUrl();
		try {
			uploadService.execute(file.getPath(), uploadUrl);
		} catch (Exception e) {
			e.printStackTrace();
			uploadedListener.OnCompleted("error");
		}
	}

	/**
	 * Добавление вложения (уже выгруженного)
	 * @param tableParam Код таблицы
	 * @param keyValueParam Значение ключа строки
	 * @param fileNameParam Оригинальное имя файла
	 * @param filePathParam Имя файла во временном web-каталоге
	 * @param progressMessage Сообщение прогресса загрузки
	 * @param addedListener Обработчик добавления вложения
	 */
	private void addAttachment(final String tableParam, final String keyValueParam, final String fileNameParam, final String filePathParam,
								String progressMessage, final OnAttachmentAdded addedListener){
		try
		{
			ServiceFactory.ServiceParams p = new ServiceFactory.ServiceParams(mActivity);
			if(progressMessage!=null && !progressMessage.isEmpty()){
				p.setProgressParams(new ServiceFactory.ProgressParams(progressMessage));
			}
			p.setCache(false);
			IService service = ServiceFactory.createService(p);
			service.setOnExecuteCompletedHandler(new OnTaskCompleted() {
				@Override
				public void onTaskCompleted(Object result) {
					if(result!= null && addedListener != null){
						addedListener.onAttachmentAdded(Boolean.valueOf((String) result));
					}
				}
			});
			service.Exec("ADDATTACHMENT", new Object() {
				public String table = tableParam;
				public String keyValue = keyValueParam;
				public String fileName = fileNameParam;
				public String filePath = filePathParam;
			}, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.showPopup(mActivity, mActivity.getString(R.string.cant_add_file));
		}
	}
}