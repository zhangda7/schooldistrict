package com.pt.schooldistrict.spider;

import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.dao.SchoolDistrictDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.model.SchoolDistrict;
import com.pt.schooldistrict.util.ExcelReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/1/21.
 * 该类处理的事情是从sd_estate中获取待更新或添加的小区信息,之后遍历每个小区,将其中的二手房信息添加到数据库中
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
            // 对读取Excel表格标题测试
            InputStream is = new FileInputStream(xls);
            ExcelReader excelReader = new ExcelReader();
            excelReader.setSheetIndex(1);
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
                String name = map.get(i).split("\\|")[nameIndex].trim();
                String address = map.get(i).split("\\|")[addressIndex].trim();
                System.out.println(String.format("%s-%s-%s", district, name, address));
                School school = schoolDao.selectByNameEquals(name);
                if(school == null) {
                    logger.warning("School " + name + "not found");
                    continue;
                }

                List<Estate> estates = estateDao.selectByAddressLike(address);
                if(estates.size() <= 0) {
                    logger.warning("address " + address + "not found");
                    continue;
                }
                SchoolDistrict sd = new SchoolDistrict();
                sd.setDistrictId(1);
                sd.setSchoolId(school.getId());
                sd.setEstateId(estates.get(0).getId());
                sd.setYear(2015);
                schoolDistrictDao.insert(sd);

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
