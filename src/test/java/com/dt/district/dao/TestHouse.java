package com.dt.district.dao;

import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.model.House;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by da.zhang on 16/1/21.
 */
public class TestHouse {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

    @Before
    public void setup() {
        ctx=new ClassPathXmlApplicationContext("spring-config.xml");
    }

    @Test
    public void testListAll() {
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        List<House> ess = houseDao.listAll();
        for(House es : ess) {
            System.out.println(es);
        }
    }

    @Test
    public void testSelectByName() {
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        List<House> ess = houseDao.selectById(1);
        for(House es : ess) {
            System.out.println(es);
        }
    }

    @Test
    public void insert() {
        House house = new House();
        house.setTitle("title");
        house.setArea((float)100.9);
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        houseDao.insert(house);
    }

    @Test
    public void testSelectByEstateId() {
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        List<House> ess = houseDao.selectByEstateId(210);
        for(House es : ess) {
            System.out.println(es);
        }
    }
}
