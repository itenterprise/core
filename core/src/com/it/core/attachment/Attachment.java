package com.it.core.attachment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.it.core.serialization.JsonDateDeserializer;

import java.util.Date;

/**
 * Описание вложения
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Attachment {

    /**
     * Уникальный номер
     */
    @JsonProperty("NDOR")
    public int Ndor;

    /**
     * Имя файла
     */
    @JsonProperty("FILENAME")
    public String FileName;

    /**
     * ФИО пользователя, который добавил
     */
    @JsonProperty("USER")
    public String User;

    /**
     * Дата добавления
     */
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonProperty("DATE")
    public Date DateAdd;

    /**
     * Признак владения
     */
    @JsonProperty("ISMY")
    public boolean IsMy;

    /**
     * Расширение файла
     */
    @JsonProperty("FILEEXT")
    public String FileExt;
}