package com.dt.district.dao;

import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.dao.HouseHistoryDao;
import com.pt.schooldistrict.model.HouseHistory;
import com.pt.schooldistrict.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by da.zhang on 16/2/21.
 */
public class TestHouseHistory {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

    @Before
    public void setup() {
        ctx=new ClassPathXmlApplicationContext("spring-config.xml");
    }


    @Test
    public void testInsert() {
        HouseHistoryDao houseHistoryDao=(HouseHistoryDao) ctx.getBean("houseHistoryDao");
        HouseHistory houseHistory = new HouseHistory();
        houseHistory.setPrice(20);
        houseHistory.setHouseId(3);
        houseHistoryDao.insert(houseHistory);

    }

    @Test
    public void testSelectByEstateId() {
        HouseHistoryDao houseHistoryDao=(HouseHistoryDao) ctx.getBean("houseHistoryDao");
        List<HouseHistory> houseHistoryList = houseHistoryDao.selectByEstateId(161);
        System.out.println(Util.toJson(houseHistoryList));
    }

    @Test
    public void testSelectByHouseId() {
        HouseHistoryDao houseHistoryDao=(HouseHistoryDao) ctx.getBean("houseHistoryDao");
        List<HouseHistory> houseHistoryList = houseHistoryDao.selectByHouseId(11846);
        System.out.println(Util.toJson(houseHistoryList));
    }

}
