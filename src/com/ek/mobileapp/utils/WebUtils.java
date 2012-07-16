package com.ek.mobileapp.utils;

public class WebUtils {
    public static final int WEBERROR = -1;
    public static final int APPLICATIONERROR = -2;
    public static final int SUCCESS = 0;
    public static final int NOSESSION = 1;
    public static final int AUTHERROR = 2;
    public static final int LOGINERROR = 4;

    public static final String HTTP = "http://";
    public static final String HOST = "http://192.168.1.112:8090/sshapp";
    public static final String NEWS = "/common/host_info";
    public static final String LOGINACTION = "/common/logon?type=mobile";
    public static final String USERLOG = "/system/user_log?type=mobile";
    public static final String MOBLOG = "/moblog/save?type=mobile";
    public static final String UPDATE = "/common/update?type=mobile";

    public static final String GETGENERALINFOACTION = "/control?action=generalinfo";

    public static final String ACTION_QUERYTOTAL = "/query2/query?type=mobile";
}
