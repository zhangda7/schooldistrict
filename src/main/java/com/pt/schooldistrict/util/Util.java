package com.pt.schooldistrict.util;

/**
 * Created by da.zhang on 16/1/16.
 */
public class Util {

    public static int getDistrictCode(String district) {
        if(district.equals("pudongxinqu")) {
            return 1;
        }
        return 0;
    }

}
