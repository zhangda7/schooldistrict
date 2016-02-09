package com.dt.district.dao;

import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.dao.SchoolDistrictDao;
import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.model.SchoolDistrict;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by da.zhang on 16/1/21.
 */
public class TestSchoolDistrict {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

    @Before
    public void setup() {
        ctx=new ClassPathXmlApplicationContext("spring-config.xml");
    }


    @Test
    public void testSelectBySchoolEstateID() {
        SchoolDistrictDao schoolDistrictDao=(SchoolDistrictDao) ctx.getBean("schoolDistrictDao");
        SchoolDistrict sd = new SchoolDistrict();
        sd.setDistrictId(1);
        sd.setSchoolId(21);
        sd.setEstateId(14510);
        sd.setYear(2015);
        SchoolDistrict ret = schoolDistrictDao.selectBySchoolEstateID(sd);
        System.out.println(ret);
    }

    @Test
    public void insert() {
        SchoolDistrict schoolDistrict = new SchoolDistrict();
        schoolDistrict.setEstateId(160);
        schoolDistrict.setSchoolId(4);
        schoolDistrict.setDistrictId(1);
        schoolDistrict.setYear(2015);

        SchoolDistrictDao schoolDistrictDao=(SchoolDistrictDao) ctx.getBean("schoolDistrictDao");
        schoolDistrictDao.insert(schoolDistrict);
    }
}
