package com.dt.district.dao;

import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.util.ExcelReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by da.zhang on 16/1/23.
 */
public class TestExcel {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");

    Map<String, String> result = new HashMap<String, String>();

    public static void main(String[] args) throws IOException {
        try {
            String xls = "doc/2015年浦东新区公办小学对口地域范围表.xls";
            TestExcel test = new TestExcel();
            int dateIndex = -1;
            int kehuIndex = -1;
            int numberIndex = -1;
            // 对读取Excel表格标题测试
            InputStream is = new FileInputStream(xls);
            ExcelReader excelReader = new ExcelReader();
            excelReader.setSheetIndex(1);
            String[] title = excelReader.readExcelTitle(is);
            System.out.println("获得Excel表格的标题:");
            int index = 0;
            for (String s : title) {
                System.out.print(s + " ");
                if (s.equals("区县")) {
                    dateIndex = index;
                } else if (s.equals("学校名称")) {
                    kehuIndex = index;
                } else if (s.equals("对口路牌（显示）")) {
                    numberIndex = index;
                }

                index++;
            }
            System.out.println(dateIndex);

            if (dateIndex == -1 || kehuIndex == -1 || numberIndex == -1) {
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
                System.out.println("date"
                        + map.get(i).split("\\|")[dateIndex].trim());
                System.out.println("kehu"
                        + map.get(i).split("\\|")[kehuIndex].trim());
                System.out.println("number"
                        + map.get(i).split("\\|")[numberIndex].trim());
                String address = map.get(i).split("\\|")[numberIndex].trim();
                List<Estate> estates = test.estateDao.selectByAddressLike(address);
                if(estates.size() > 0) {
                    test.result.put(address, estates.get(0).getName());
                    found ++;
                } else {
                    test.result.put(address, "=========================");
                    notFound ++;
                }
            }
            for(Map.Entry<String, String> entry : test.result.entrySet()) {
                System.out.println(String.format("Address : %s, estate : %s", entry.getKey(), entry.getValue()));
            }
            System.out.println("Found:" + found + ", not found : " + notFound);

        } catch (FileNotFoundException e) {
            System.out.println("未找到指定路径的文件!");
            e.printStackTrace();
        }
    }
}
