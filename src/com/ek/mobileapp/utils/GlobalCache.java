package com.ek.mobileapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.UserDTO;

public class GlobalCache {
    private static GlobalCache cache = null;
    private UserDTO loginuser;
    private String lastIp;//客户端ip
    private String hostIp;//服务端ip
    private String startDate = "";
    private String endDate = "";
    private int screenWidth;
    private int screenHeight;

    private List<Patient> patients = new ArrayList<Patient>();
    private Patient currentPatient;

    private GlobalCache() {
    }

    public static GlobalCache getCache() {
        if (cache == null)
            cache = new GlobalCache();
        return cache;
    }

    public UserDTO getLoginuser() {
        return loginuser;
    }

    public void setLoginuser(UserDTO loginuser) {
        this.loginuser = loginuser;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

}
