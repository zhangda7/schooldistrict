package com.pt.schooldistrict.util;

import com.google.gson.Gson;

/**
 * Created by da.zhang on 16/1/16.
 */
public class Util {

    private static Gson gson = new Gson();

    public static int getDistrictCode(String district) {
        if(district.equals("pudongxinqu")) {
            return 1;
        }
        return 0;
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(byte[] data, Class<T> targetClass) {
        return gson.fromJson(new String(data), targetClass);
    }

    public static <T> T fromJson(String str, Class<T> targetClass) {
        return gson.fromJson(str, targetClass);
    }

}
