package com.it.core.internalstorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.it.core.tools.FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Класс для работы с внутренним хранилищем
 */
public class InternalStorageSerializer {

	private static final String IMAGES_DIRECTORY = "images";

	/**
	 * Сохранить объект
	 */
	public boolean saveJsonObject(Context context, String method, Object params, Object obj) {
		if (obj == null) {
			return false;
		}
		try {
			String fileName = createFileNameFromMethodAndParams(method, params);
			File file = new File(context.getFilesDir(), fileName);
			if (file.exists()){
				file.delete();
			}
			// Создать потоки для записи
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(((String) obj).getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Получить один объект из внутренней памяти
	 * @param context Контекст выполнения
	 * @param method Веб-расчет
	 * @param params Параметры для отбора
	 * @return
	 */
	public String getJsonObject(Context context, String method, Object params) {
		String fileName = createFileNameFromMethodAndParams(method, params);
		File file = new File(context.getFilesDir(), fileName);
		if (file.exists()){
			try {
				FileInputStream fis = context.openFileInput(fileName);
				StringBuilder sb = new StringBuilder();
				try{
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					fis.close();
				} catch(OutOfMemoryError om){
					om.printStackTrace();
				} catch(Exception ex){
					ex.printStackTrace();
				}
				String result = sb.toString();
				return result;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Сохранить картинку
	 */
	public boolean saveBitmap(Context context, String fileName, Bitmap bitmap) {
		if (bitmap == null) {
			return false;
		}
		// Create imageDir
		File file = new File(context.getDir(IMAGES_DIRECTORY, Context.MODE_PRIVATE), fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			// Use the compress method on the BitMap object to write image to the OutputStream
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Получить картинку из внутренней памяти
	 * @param context Контекст
	 * @param fileName Название файла
	 * @return Картинка
	 */
	public Bitmap getBitmap(Context context, String fileName) {
		File file = new File(context.getDir(IMAGES_DIRECTORY, Context.MODE_PRIVATE), fileName);
		if (!file.exists()){
			return null;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * Получить файл картинки из внутренней памяти
	 * @param context Контекст
	 * @param fileName Название файла
	 * @return Файл картинки
	 */
	public File getImageFile(Context context, String fileName) {
		File file = new File(context.getDir(IMAGES_DIRECTORY, Context.MODE_PRIVATE), fileName);
		if (!file.exists()) {
			return null;
		}
		return file;
	}

	/**
	 * Сохранить сериализуемый объект
	 * @param context Контекст
	 * @param fileName Имя файла
	 * @param object Сериализуемый объект
	 */
	public void putSerializable(Context context, String fileName, Serializable object) {
		if(object == null){
			return;
		}
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null){
					oos.close();
				}
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Получить сериализуемый объект
	 * @param context Контекст
	 * @param fileName Имя файла
	 * @return Сериализуемый объект
	 */
	public Serializable getSerializable(Context context, String fileName) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Serializable object = null;
		try {
			fis = context.openFileInput(fileName);
			ois = new ObjectInputStream(fis);
			object = (Serializable) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null){
					ois.close();
				}
				if (fis != null){
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	/**
	 * Положить файл во внутреннее хранилище
	 * @param context Контекст
	 * @param file Файл
	 * @param dirName Имя папки в которую положить копию файла
	 * @throws IOException Ошибка при копировании файла
	 */
	public void putFile(Context context, File file, String dirName) throws IOException {
		String fileName = file.getName();
		putFile(context, file.getName(), file, dirName);
	}

	/**
	 * Положить файл во внутреннее хранилище
	 * @param context Контекст
	 * @param fileName Имя файла во внутреннем хранилище
	 * @param file Файл
	 * @param dirName Имя папки в которую положить копию файла
	 * @throws IOException Ошибка при копировании файла
	 */
	public void putFile(Context context, String fileName, File file, String dirName) throws IOException {
		File internalFile = new File(context.getDir(dirName, Context.MODE_PRIVATE), fileName);
		FileHelper.copy(file, internalFile);
	}

	/**
	 * Удалить файл во внутреннем хранилище
	 * @param context Контекст
	 * @param fileName Имя файла
	 * @param dirName Имя папки в которой находиться файл
	 */
	public void removeFile(Context context, String fileName, String dirName) {
		File internalFile = new File(context.getDir(dirName, Context.MODE_PRIVATE), fileName);
		if (internalFile.exists()) {
			internalFile.delete();
		}
	}

	/**
	 * Достать файл из внутреннего хранилища
	 * @param context Контекст
	 * @param fileName Имя файла
	 * @param dirName Имя папки в которой находится файл
	 * @return Файл
	 */
	public File getFile(Context context, String fileName, String dirName) {
		return new File(context.getDir(dirName, Context.MODE_PRIVATE), fileName);
	}

	/**
	 * Создать имя файла по названию метода и параметрам запроса
	 * @param methodName Код расчета
	 * @param params Параметры расчета
	 * @return Имя файла
	 */
	private String createFileNameFromMethodAndParams(String methodName, Object params) {
		String fileName = methodName;
		try {
			for (Field field : params.getClass().getFields()) {
				Object fieldValue = field.get(params);
				if (fieldValue != null){
					fileName += fieldValue.toString();
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}
}