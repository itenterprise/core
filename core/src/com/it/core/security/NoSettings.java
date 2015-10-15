package com.it.core.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация для определения деятельностей, которым не надо задавать настройки
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSettings {

}