package com.it.core.fragments;

import java.util.ArrayList;
import java.util.List;

/**
 * Секция выезжающего меню
 * @author bened
 *
 */
public class Section {
	
    private String title;
    private List<SectionItem> sectionItems = new ArrayList<SectionItem>();

    /**
     * Создать секцию
     * @param title заголовок секции
     */
    public Section(String title) {
        this.title = title;
    }

    /**
     * Добавить пункт меню в секцию
     * @param id идентификатор меню для оброботки нажатия
     * @param title заголовок пункта меню
     * @param icon идентификатор иконки в ресурсах
     */
    public void addSectionItem(long id, String title, String icon) {
        this.sectionItems.add( new SectionItem(id, title, icon));
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<SectionItem> getSectionItems() {
        return sectionItems;
    }
    
    public void setSectionItems(List<SectionItem> sectionItems) {
        this.sectionItems = sectionItems;
    }
}
