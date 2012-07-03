package com.ek.mobileapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.ek.mobileapp.model.GeneralInfo;
import com.ek.mobileapp.model.GeneralTotal;
import com.ek.mobileapp.model.User;

public class GlobalCache {
    public static final int ALLYQ = 0;
    public static final int CZYQ = 1;
    public static final int YHYQ = 2;
    public static final String CZ = "城中";
    public static final String YH = "阳湖";

    private static GlobalCache cache = null;
    private User loginuser;
    private int selectedYQ;
    private String startDate = "";
    private String endDate = "";
    private List<GeneralTotal> totals;
    private List<GeneralInfo> infos;
    private List<GeneralInfo> czinfos;
    private List<GeneralInfo> yhinfos;

    private int screenWidth;
    private int screenHeight;

    private GlobalCache() {
    }

    public static GlobalCache getCache() {
        if (cache == null)
            cache = new GlobalCache();
        return cache;
    }

    public User getLoginuser() {
        return loginuser;
    }

    public void setLoginuser(User loginuser) {
        this.loginuser = loginuser;
    }

    public int getSelectedYQ() {
        return selectedYQ;
    }

    public void setSelectedYQ(int selectedYQ) {
        this.selectedYQ = selectedYQ;
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

    public List<GeneralInfo> getInfos() {
        if (selectedYQ == ALLYQ) {
            return infos;
        } else if (selectedYQ == CZYQ) {
            return czinfos;
        } else if (selectedYQ == YHYQ) {
            return yhinfos;
        } else
            return infos;
    }

    public void setInfos(List<GeneralInfo> infos) {
        this.infos = new ArrayList<GeneralInfo>();
        if (infos != null) {
            this.infos = infos;
        }
        czinfos = new ArrayList<GeneralInfo>();
        yhinfos = new ArrayList<GeneralInfo>();
        for (GeneralInfo info : infos) {
            if (info.getLevel1() == GeneralInfo.CZYQ) {
                czinfos.add(info);
            } else if (info.getLevel1() == GeneralInfo.YHYQ) {
                yhinfos.add(info);
            }
        }
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

    public List<GeneralTotal> getTotals() {
        return totals;
    }

    public void setTotals(List<GeneralTotal> totals) {
        this.totals = totals;
    }
}
