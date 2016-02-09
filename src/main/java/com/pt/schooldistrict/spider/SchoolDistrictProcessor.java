package com.pt.schooldistrict.spider;

import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.dao.SchoolDistrictDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.model.SchoolDistrict;
import com.pt.schooldistrict.util.ExcelReader;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by da.zhang on 16/1/21.
 * 该类处理的事情是遍历每个学校的招生信息,再插入到schoolDistrict库中
 */
public class SchoolDistrictProcessor {

    private Logger logger = Logger.getLogger(SchoolDistrictProcessor.class.getCanonicalName());

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    SchoolDao schoolDao=(SchoolDao) ctx.getBean("schoolDao");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    SchoolDistrictDao schoolDistrictDao=(SchoolDistrictDao) ctx.getBean("schoolDistrictDao");

    private void insertFromExcel() {
        try {
            String xls = "doc/2015年浦东新区公办小学对口地域范围表.xls";
            int districtIndex = -1;
            int nameIndex = -1;
            int addressIndex = -1;
            int commentIndex = -1;
            // 对读取Excel表格标题测试
            InputStream is = new FileInputStream(xls);
            ExcelReader excelReader = new ExcelReader();
            excelReader.setSheetIndex(0);
            String[] title = excelReader.readExcelTitle(is);
            System.out.println("获得Excel表格的标题:");
            int index = 0;
            for (String s : title) {
                if (s.equals("区县")) {
                    districtIndex = index;
                } else if (s.equals("学校名称")) {
                    nameIndex = index;
                } else if (s.equals("对口路牌（显示）")) {
                    addressIndex = index;
                } else if(s.equals("备注")) {
                    commentIndex = index;
                }

                index++;
            }
            if (districtIndex == -1 || nameIndex == -1 || addressIndex == -1) {
                System.out.println("Excel 表格标题错误，按任意键退出");
                System.exit(1);
            }

            // 对读取Excel表格内容测试
            InputStream is2 = new FileInputStream(xls);
            Map<Integer, String> map = excelReader.readExcelContent(is2);
            int found = 0;
            int notFound = 0;
            System.out.println("获得Excel表格的内容:");
            for (int i = 1; i <= map.size(); i++) {
                System.out.println(map.get(i));
                String district = map.get(i).split("\\|")[districtIndex].trim();
                String schoolName = map.get(i).split("\\|")[nameIndex].trim();
                String address = map.get(i).split("\\|")[addressIndex].trim();
                System.out.println(String.format("%s-%s-%s", district, schoolName, address));
                String comment = "NOTFOUND";
                if(map.get(i).split("\\|").length >= commentIndex + 1) {
                    comment = map.get(i).split("\\|")[commentIndex].trim();
                }
                School school = schoolDao.selectByNameEquals(schoolName);
                if(school == null) {
                    logger.warn("School " + schoolName + "not found");
                    continue;
                }
                boolean findOne = false;
                List<Estate> estates = estateDao.selectByAddressLike(address);
                if(estates.size() == 0) {
                    estates = estateDao.selectByNameLike(comment);
                    if(estates.size() > 0) {
                        findOne = true;
                    }
                } else {
                    findOne = true;
                }
                if(findOne) {
                    //如果查到的小区数目>1, 就每个都把学校插进去好了,因为本来就是一个地址
                    /*if(estates.size() > 1) {
                        logger.error(String.format("School:%s, address:%s, find %d matching", schoolName, address, estates.size()));
                    }*/
                    for(Estate estate : estates) {
                        SchoolDistrict sd = new SchoolDistrict();
                        sd.setDistrictId(1);
                        sd.setSchoolId(school.getId());
                        sd.setEstateId(estates.get(0).getId());
                        sd.setYear(2015);
                        if(schoolDistrictDao.selectBySchoolEstateID(sd) == null) {
                            //在DB中查找不到时,才进行insert,把验证的逻辑放到code里做,不要依赖DB的简直验证
                            schoolDistrictDao.insert(sd);
                        }
                    }


                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("未找到指定路径的文件!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SchoolDistrictProcessor processor = new SchoolDistrictProcessor();
        processor.insertFromExcel();
    }
}
