package com.pt.schooldistrict;

import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.dao.SchoolDistrictDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.House;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/1/31.
 */
public class Test {
    private Logger logger = Logger.getLogger(Test.class.getCanonicalName());

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    SchoolDao schoolDao=(SchoolDao) ctx.getBean("schoolDao");
    HouseDao houseDao=(HouseDao) ctx.getBean("houseDao");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    SchoolDistrictDao schoolDistrictDao=(SchoolDistrictDao) ctx.getBean("schoolDistrictDao");

    public void testSelectEstateHouseBySchoolId() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> ess = estateDao.selectBySchoolId(21);
        for(Estate e : ess) {
            logger.info(e.toString());
            /*List<House> houses = houseDao.selectByEstateId(e.getId());
            if(houses != null && houses.size() > 0) {
                logger.info(houses.get(0).toString());
            }*/
        }
        //System.out.println(ess);
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.testSelectEstateHouseBySchoolId();
    }

}
