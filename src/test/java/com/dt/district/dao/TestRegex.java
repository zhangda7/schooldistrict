package com.dt.district.dao;

import com.pt.schooldistrict.util.Constants;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by da.zhang on 16/3/19.
 */
public class TestRegex {

    @Test
    public void testRegex() {
        String estate = "http://sh.lianjia.com/xiaoqu/5011000017365.html";
        String regex =  "http://sh.lianjia.com/xiaoqu/\\w+\\.html$";
        Pattern pattern = Pattern.compile(regex);
        System.out.println(pattern.matcher(estate).matches());
    }

}
