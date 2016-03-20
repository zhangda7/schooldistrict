package com.dt.district.dao;

import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.model.Estate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    public void testSelectByRegex() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> ess = estateDao.selectByAddressLike("-");
        //首先对每个estate的address进行遍历
        for(int i = 0; i < ess.size() ; i++) {
            Estate estate = ess.get(i);
            String[] adds = estate.getAddress().split("/");
            StringBuilder sb = new StringBuilder();
            //之后读每个address用/进行拆分
            for(String address : adds) {
                Pattern p=Pattern.compile("\\d+\\-\\d+");
                Matcher m=p.matcher(address);
                System.out.println(address);
                //如果满足1-20的这种形式,就进入拼接环节
                if(m.find()) {
                    String find = address.substring(m.start(), m.end());

                    int slashIndex = find.indexOf('-');
                    int start = Integer.parseInt(find.substring(0, slashIndex));
                    int end = Integer.parseInt(find.substring(slashIndex + 1));
                    System.out.println(String.format("Str : %s, start : %d, end : %d", find, start, end));
                    //从start到end依次拼接起来
                    for(int j = start; j <= end; j++) {
                        sb.append(address.substring(0, m.start()));
                        sb.append(String.valueOf(j));
                        sb.append(address.substring(m.end()) + " / ");
                    }
                } else {
                    sb.append(address + " / ");
                }

            }
            //最后更新DB
            estate.setAddress(sb.toString());
            estateDao.updateById(estate);
            System.out.println("Done : " + sb.toString());
        }

    }

    @Test
    public void updateEstateUrl() {
        EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
        List<Estate> estates = estateDao.listAll();
        for(Estate estate : estates) {
            String url = estate.getUrl();
            if(url != null) {
                url = url.substring(0, url.length() - 1) + ".html";
                estate.setUrl(url);
            }
            estateDao.updateById(estate);
        }
    }


    /*public static void main(String[] args) {
        TestEstate test = new TestEstate();
        test.testListAll();
        //test.insert();
    }*/

}
