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

    //保存生命体征
    public static final String VITALSIGN_SAVE = "/vital_sign/save_vitalsign_data?type=mobile";
    //查询一个病人
    public static final String VITALSIGN_GET_PATIENT = "/vital_sign/get_patient?type=mobile";
    //查询一个病区所有病人
    public static final String VITALSIGN_GET_PATIENT_ALL = "/vital_sign/get_patient_all?type=mobile";
    //查询一个病人一天一个指标的记录(如果是一日多次的指标,则是某个时间点的记录)
    public static final String VITALSIGN_GET_ONE = "/vital_sign/get_vitalsign_data?type=mobile";
    //查询一个病人一天的所有生命体征
    public static final String VITALSIGN_GET_ALL = "/vital_sign/get_vitalsign_data_all?type=mobile";
    //查询某一类生命体征指标
    public static final String VITALSIGN_GET_ITEM = "/vital_sign/get_vitalsign_item?type=mobile";
    //查询时间点
    public static final String VITALSIGN_GET_TIMEPOINT = "/vital_sign/get_timepoint?type=mobile";
    //查询测量类别
    public static final String VITALSIGN_GET_MEASURETYPE = "/vital_sign/get_measuretype?type=mobile";

}
