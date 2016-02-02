package com.dt.district.dao;

import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.model.School;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by da.zhang on 16/1/21.
 */
public class TestSchool {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

    @Before
    public void setup() {
        ctx=new ClassPathXmlApplicationContext("spring-config.xml");
    }

    @Test
    public void testListAll() {
        SchoolDao schoolDao=(SchoolDao) ctx.getBean("schoolDao");
        List<School> ess = schoolDao.listAll();
        for(School es : ess) {
            System.out.println(es);
        }
    }

    @Test
    public void testSelectByName() {
        SchoolDao schoolDao=(SchoolDao) ctx.getBean("schoolDao");
        List<School> ess = schoolDao.selectById(1);
        for(School es : ess) {
            System.out.println(es);
        }
    }

    @Test
    public void insert() {
        School school = new School();
        school.setName("实验学校");
        school.setAlias("[alias1,alias2]");
        school.setDistrictId(1);
        SchoolDao schoolDao=(SchoolDao) ctx.getBean("schoolDao");
        schoolDao.insert(school);
    }
}
