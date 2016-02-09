package com.pt.schooldistrict.spider;

import com.alibaba.fastjson.JSONObject;
import com.pt.schooldistrict.MyConsolePipeline;
import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.model.SchoolDistrict;
import com.pt.schooldistrict.util.Constants;
import com.pt.schooldistrict.util.ExcelReader;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
/**
 * Created by da.zhang on 16/1/21.
 * 该类处理的事情是从sd_estate中获取待更新或添加的小区信息,之后遍历每个小区,将其中的二手房信息添加到数据库中
 */
public class SchoolProcessor {

    private Logger logger = Logger.getLogger(SchoolProcessor.class.getCanonicalName());

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    SchoolDao schoolDao=(SchoolDao) ctx.getBean("schoolDao");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    /**
     * 从excel中插入学校信息
     */
    private void insertFromExcel() {
        try {
            String xls = "doc/2015年浦东新区公办小学对口地域范围表.xls";
            int dateIndex = -1;
            int nameIndex = -1;
            int numberIndex = -1;
            // 对读取Excel表格标题测试
            InputStream is = new FileInputStream(xls);
            ExcelReader excelReader = new ExcelReader();
            excelReader.setSheetIndex(0);
            String[] title = excelReader.readExcelTitle(is);
            int index = 0;
            for (String s : title) {
                System.out.print(s + " ");
                if (s.equals("学校名称")) {
                    nameIndex = index;
                }
                index++;
            }
            System.out.println(dateIndex);

            if (nameIndex == -1 ) {
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

                System.out.println("kehu"
                        + map.get(i).split("\\|")[nameIndex].trim());
                School school = new School();
                school.setName(map.get(i).split("\\|")[nameIndex].trim());
                school.setDistrictId(1);
                school.setIsPublic(1);
                school.setType(1);
                if(schoolDao.selectByNameEquals(school.getName()) == null) {
                    schoolDao.insert(school);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("未找到指定路径的文件!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SchoolProcessor processor = new SchoolProcessor();
        processor.insertFromExcel();
    }
}
