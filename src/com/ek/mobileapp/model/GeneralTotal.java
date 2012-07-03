package com.ek.mobileapp.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.ek.mobileapp.utils.GlobalCache;

public class GeneralTotal {
    private String name;
    private float total = 0.0f;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public GeneralTotal(JSONObject json) {
        try {
            name = json.getString("name");
            total = (float) json.getDouble("total");
            type = json.getInt("type");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public GeneralTotal(int type) {
        this.setType(type);
        float t = 0.00f;
        switch (type) {
        case 1:
            name = "门诊人次";
            break;
        case 2:
            name = "门诊处方";
            break;
        case 3:
            name = "门诊处置";
            break;
        case 4:
            name = "住院人次";
            break;
        case 5:
            name = "入院人次";
            break;
        case 6:
            name = "出院人次";
            break;
        case 7:
            name = "手术人次";
            break;
        case 8:
            name = "检查人次";
            break;
        case 9:
            name = "检验人次";
            break;
        case 10:
            name = "医疗收入";
            break;
        default:
            break;
        }
        if (GlobalCache.getCache().getSelectedYQ() == GlobalCache.ALLYQ) {
            for (GeneralTotal v : GlobalCache.getCache().getTotals()) {
                if (v.getType() == type) {
                    total = total + v.getTotal();
                }
            }
        } else {
            for (GeneralTotal v : GlobalCache.getCache().getTotals()) {
                if (v.getType() == type && getYQ(v.getName()) == GlobalCache.getCache().getSelectedYQ()) {
                    total = total + v.getTotal();
                }
            }
        }
    }

    private int getYQ(String name) {
        if (name.startsWith(GlobalCache.CZ)) {
            return GlobalCache.CZYQ;
        } else if (name.startsWith(GlobalCache.YH)) {
            return GlobalCache.YHYQ;
        }
        return 0;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
