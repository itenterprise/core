package com.it.core.menu;

import java.io.Serializable;

/**
 * Описание елемента выезжающего меню
 */
public class SideMenuItem implements Serializable{
    public int Id;
    public String Title;
	public String ActionBarTitle;
    public Integer ImageId;
    public boolean IsSection;

	/**
	 * Коструктор елемента выезжающего меню
	 * @param id Идентификатор
	 * @param title Заголовок
	 * @param actionBarTitle Заголовок в ActionBar-е (после выбора)
	 * @param imageId Идентификатор картинки
	 * @param isSection Является ли секцией
	 */
	public SideMenuItem(int id, String title, String actionBarTitle, Integer imageId, boolean isSection){
		Id = id;
		Title = title;
		ActionBarTitle = actionBarTitle;
		ImageId = imageId;
		IsSection = isSection;
	}

    /**
     * Коструктор елемента выезжающего меню
     * @param id Идентификатор
     * @param title Заголовок
     * @param imageId Идентификатор картинки
     * @param isSection Является ли секцией
     */
    public SideMenuItem(int id, String title, Integer imageId, boolean isSection){
        this(id, title, title, imageId, isSection);
    }
}