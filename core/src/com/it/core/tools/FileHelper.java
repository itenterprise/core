package com.it.core.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.it.core.R;
import com.it.core.application.ApplicationBase;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Класс для работы с файлами
 */
public class FileHelper {

	/**
	 * Получить временный каталог
	 * @return Каталог
	 */
	public static File getTempDir() {
		Context context = ApplicationBase.getInstance().getApplicationContext();
		File dir = context.getExternalCacheDir();
		if (dir == null){
			dir = context.getCacheDir();
		}
		return dir;
	}

	/**
	 * Удаление файлов из временного каталога
	 */
	public static void deleteFilesFromTemp() {
		clearDirectory(getTempDir());
	}

	/**
	 * Удаление файлов из каталога
	 */
	public static void clearDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			String[] children = directory.list();
			for (String aChildren : children) {
				new File(directory, aChildren).delete();
			}
		}
	}

	/**
	 * Получить каталог с файлами приложения
	 * @return Каталог
	 */
	public static File getFilesDir() {
		Context context = ApplicationBase.getInstance().getApplicationContext();
		File dir = context.getExternalFilesDir(null);
		if (dir == null){
			dir = context.getFilesDir();
		}
		return dir;
	}

	/**
	 * Скопировать файл
	 * @param srcPath Путь к файлу-оригиналу
	 * @param dstPath Путь к файлу-копии
	 * @throws IOException
	 */
	public static void copy(String srcPath, String dstPath) throws IOException {
		copy(new File(srcPath), new File(dstPath));
	}

	/**
	 * Скопировать файл
	 * @param src Файл-оригинал
	 * @param dst Файл-копия
	 * @throws IOException
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Получить имя файла без расширения
	 *
	 * @param fileName Имя файла (с расширением)
	 * @return Имя файла без расширения
	 */
	public static String getNameWithoutType(String fileName){
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	/**
	 * Получить расширение файла
	 * @param fileName Имя файла
	 * @return Расширение файла (без точки)
	 */
	public static String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/**
	 * Проверить наличие файла по пути к нему
	 * @param filePath Путь к файлу
	 * @return Признак наличия
	 */
	public static boolean isFileExists(String filePath) {
		boolean exists = true;
		if (filePath == null || filePath.isEmpty() || !new File(filePath).exists()) {
			exists = false;
		}
		return exists;
	}

	/**
	 * Открыть файл
	 * @param path Путь к файлу
	 */
	public static void openFile(Activity activity, String path){
		if (path == null || activity == null) { return; }
		File f = new File(path);
		Intent newIntent = new Intent(Intent.ACTION_VIEW);
		String mimeType = new FileHelper().getMimeType(f);
		newIntent.setDataAndType(Uri.fromFile(f), mimeType);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			if (f.exists()) {
				activity.startActivity(newIntent);
			}
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(activity, R.string.cant_find_app_to_open_file, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Read bytes from a File into a byte[].
	 *
	 * @param file The File to read.
	 * @return A byte[] containing the contents of the File.
	 * @throws IOException Thrown if the File is too long to read or couldn't be
	 * read fully.
	 */
	public static byte[] readBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();
		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
		}
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * Writes the specified byte[] to the specified File path.
	 *
	 * @param theFile File Object representing the path to write to.
	 * @param bytes The byte[] of data to write to the File.
	 * @throws IOException Thrown if there is problem creating or writing the
	 * File.
	 */
	public static void writeBytesToFile(File theFile, byte[] bytes) throws IOException {
		BufferedOutputStream bos = null;
		try {
			FileOutputStream fos = new FileOutputStream(theFile);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} finally {
			if (bos != null) {
				try {
					//flush and close the BufferedOutputStream
					bos.flush();
					bos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Получить файл в виде строки
	 * @param filePath Путь к файлу
	 * @return Строка
	 * @throws Exception
	 */
	public String getStringFromFile (String filePath) throws Exception {
		File file = new File(filePath);
		if (!file.exists()){
			return null;
		}
		FileInputStream fin = new FileInputStream(file);
		String ret = convertStreamToString(fin);
		fin.close();
		return ret;
	}

    /**
     * Получить иконку, соответствующую к формату документа
     *
     * @param fileName - название документа (с расширением)
     * @return идентификатор иконки
     */
    public int getIconByFileName(String fileName){
        String type = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        return getIconByExtension(type);
    }

    public int getIconByExtension(String fileExtension){
        int iconId = 0;
        Formats currentFormat;
        try{
            currentFormat = Formats.valueOf(fileExtension.toUpperCase());
        }
        catch(Exception e){
            currentFormat = Formats.DOCUMENT;
        }
        switch (currentFormat) {
            case PDF:
                iconId = R.drawable.ic_pdf;
                break;
            case DOC:
            case DOCX:
                iconId = R.drawable.ic_word;
                break;
            case PTT:
            case PTTX:
                iconId = R.drawable.ic_power_point;
                break;
            case XLS:
            case XLSX:
                iconId = R.drawable.ic_excel;
                break;
            case ZIP:
                iconId = R.drawable.ic_zip;
                break;
            case JPG:
            case JPEG:
                iconId = R.drawable.ic_jpg;
                break;
            case PNG:
                iconId = R.drawable.ic_png;
                break;
            default:
                iconId = R.drawable.ic_document;
                break;
        }
        return iconId;
    }

    /**
     * Конвертировать поток в строку
     * @param is Входящий поток
     * @return Строка
     * @throws Exception
     */
    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String getMimeType(File file){
        return MimeTypeMap.getSingleton().
                getMimeTypeFromExtension(getType(file.toString()).substring(1));
    }

    private String getType(String url){
        if (url.indexOf("?")>-1) {
            url = url.substring(0,url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") );
            if (ext.indexOf("%")>-1) {
                ext = ext.substring(0,ext.indexOf("%"));
            }
            if (ext.indexOf("/")>-1) {
                ext = ext.substring(0,ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    /**
     * Форматы файлов
     */
    private enum Formats {
        PDF,
        DOC,
        DOCX,
        PTT,
        PTTX,
        XLS,
        XLSX,
        ZIP,
        JPG,
        JPEG,
        PNG,
        DOCUMENT
    }
}