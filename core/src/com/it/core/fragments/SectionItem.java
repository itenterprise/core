package com.it.core.fragments;

/**
 * Пункт меню
 * @author bened
 *
 */
public class SectionItem {

    private long id;
    private String title;
    private String icon;

    /**
     * Создать пункт меню
     * @param id идентификатор меню для оброботки нажатия
     * @param title заголовок пункта меню
     * @param icon идентификатор иконки в ресурсах
     */
    public SectionItem(long id, String title, String icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
