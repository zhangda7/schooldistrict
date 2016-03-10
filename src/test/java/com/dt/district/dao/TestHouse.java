package com.dt.district.dao;

import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.util.Constants;
import com.pt.schooldistrict.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
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
        House ess = houseDao.selectById(3);
        System.out.println(ess);
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
        List<House> ess = houseDao.selectByEstateId(161);
        for(House es : ess) {
            System.out.println(es.getHouseHistoryList().size());
            System.out.println(Util.toJson(es));
        }
    }

    @Test
    public void testSelectById() {
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        House es = houseDao.selectById(11846);
        System.out.println(es);
    }

    @Test
    public void testSelectOnlineByEstateId() {
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        List<House> ess = houseDao.selectOnlineByEstateId(253);
        for(House es : ess) {
            System.out.println(es);
        }
    }

    @Test
    public void testUpdateById() {
        HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
        House ess = houseDao.selectById(3);
        ess.setGmt_created(new Date());
        ess.setGmt_modified(new Date());
        ess.setPrice(10);
        ess.setStatus(Constants.HOUSE_STATUS_ONLINE);
        houseDao.updateById(ess);

    }

    @Test
    public void testUpdateStatus() {
        HouseDao houseDao = (HouseDao) ctx.getBean("houseDao");
        //houseDao.updateEstateStatus(165);
        House house = houseDao.selectOnlineByPageId("SH0001096683");
        System.out.println(house);
    }
}
