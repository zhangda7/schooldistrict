package com.dt.district.dao;

import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.model.Estate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by da.zhang on 16/1/18.
 */
public class TestEstate {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");

    @Before
    public void setup() {
        ctx=new ClassPathXmlApplicationContext("spring-config.xml");
    }

    @Test
    public void testListAll() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> ess = estateDao.listAll();
        for(Estate es : ess) {
            System.out.println(es);
        }
    }

    @Test
    public void testSelectByNameEquals() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        Estate ess = estateDao.selectByNameEquals("中邦城市");
        System.out.println(ess);
    }

    @Test
    public void testSelectByNameLike() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> ess = estateDao.selectByNameLike("福山路100弄");
        System.out.println(ess);
    }

    @Test
    public void testSelectByAddressLike() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> ess = estateDao.selectByAddressLike("福山路100弄");
        System.out.println(ess);
    }

    @Test
    public void testSelectByPageId() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        Estate ess = estateDao.selectByPageId("5011000017872");
        System.out.println(ess);
    }

    @Test
    public void testUpdateById() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        Estate ess = estateDao.selectByPageId("5011000011929");
        ess.setBuildYear("2002");
        estateDao.updateById(ess);
        System.out.println(ess);
    }

    @Test
    public void insert() {
        Estate estate = new Estate();
        estate.setName("中邦城市");
        estate.setAddress("秀沿路");
        estate.setDistrict(1);
        estate.setAveragePrice(22000);
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        estateDao.insert(estate);
    }

    @Test
    public void testSelectBySchoolId() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> ess = estateDao.selectBySchoolId(21);
        System.out.println(ess);
    }

    /*public static void main(String[] args) {
        TestEstate test = new TestEstate();
        test.testListAll();
        //test.insert();
    }*/

}
