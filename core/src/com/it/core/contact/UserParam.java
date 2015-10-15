package com.it.core.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.it.core.model.UserInfo;

/**
 * Параметры пользователя
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserParam {

    public UserParam(){
        userLogin = UserInfo.getCredentials().getLogin();
        nkdk = "";
        fio = UserInfo.getUserName();
    }

    /**
     * Логин
     */
    @JsonProperty("USER_LOGIN")
    private String userLogin;

    /**
     * N_KDK - идентификатор пользователя
     */
    @JsonProperty("N_KDK")
    private String nkdk;

    /**
     * ФИО
     */
    @JsonProperty("FIO")
    private String fio;

    public String getUserLogin() {
        return userLogin;
    }
    public String getNkdk() {
        return nkdk;
    }
    public String getFio() {
        return fio;
    }
}