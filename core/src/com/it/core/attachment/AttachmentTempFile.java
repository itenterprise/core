package com.it.core.attachment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Описание файла вложения во временном каталоге
 */
public class AttachmentTempFile {
	/**
	 * Имя файла
	 */
	@JsonProperty("ID")
	public String Id;

	/**
	 * Имя файла
	 */
	@JsonProperty("FILENAME")
	public String FileName;

	/**
	 * Расширение файла
	 */
	@JsonProperty("FILETYPE")
	public String FileType;
}
