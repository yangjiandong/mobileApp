package com.ek.mobileapp.utils;

public class WebUtils {
    public static final int WEBERROR = -1;
    public static final int APPLICATIONERROR = -2;
    public static final int SUCCESS = 0;
    public static final int NOSESSION = 1;
    public static final int AUTHERROR = 2;
    public static final int LOGINERROR = 4;

    public static final String HOST = "http://192.168.1.112:8080/EKingSoftMobileServer";
    public static final String NEWS = "/news.html";
    public static final String LOGINACTION = "/control?action=login&type=manager";
    public static final String GETGENERALINFOACTION = "/control?action=generalinfo";
}
