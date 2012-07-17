package com.ek.mobileapp.utils;

import com.ek.mobileapp.model.Patient;

public class BarCodeUtils {
    public static boolean isPatientTM(String tm) {
        if(tm.trim().length() == 6) return true;
        else return false;
    }

    public static boolean isMedicineTM(String tm) {
        if(tm.trim().length() == 14) return true;
        else return false;
    }

    public static Patient findPatientFromBarCode(String tm) {
        for(Patient p : GlobalCache.getCache().getPatients()) {
            if(p.getPatientId().equals(tm.trim())) {
                return p;
            }
        }
        return null;
    }
}
