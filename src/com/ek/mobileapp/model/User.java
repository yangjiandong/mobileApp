package com.ek.mobileapp.model;

//采用UserDTO替代
public class User {
    private String userId;
    private String loginName;
    private String name;
    private String dep_code;
    private String auth;
    private String telNumber;

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getLoginName() {
        return loginName;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setDep_code(String dep_code) {
        this.dep_code = dep_code;
    }
    public String getDep_code() {
        return dep_code;
    }
    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }
    public String getTelNumber() {
        return telNumber;
    }
    public void setAuth(String auth) {
        this.auth = auth;
    }
    public String getAuth() {
        return auth;
    }
}
