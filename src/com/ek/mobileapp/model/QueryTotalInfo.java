package com.ek.mobileapp.model;

import org.json.JSONException;
import org.json.JSONObject;

public class QueryTotalInfo {
    private String itemName;
    private String value;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QueryTotalInfo(JSONObject json) {
        try {
            itemName = json.getString("itemName");
            value = json.getString("value");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
