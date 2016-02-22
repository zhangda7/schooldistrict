package com.pt.schooldistrict.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.pt.schooldistrict.dao.*;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.model.HouseHistory;
import com.pt.schooldistrict.model.SchoolDistrict;
import com.pt.schooldistrict.util.Constants;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/2/20.
 * 更新house信息的定时任务,执行以下功能:
 * 1.遍历estate,查看有没有新增的house,如果有,添加到DB
 * 2.遍历estate,看有没有已经被删除的house,如果有,更新db中相应house的信息
 * 3.查询当前所有存在的房子,比对价格,如果价格有了变化(增加/减少),记录到logDB中
 * Note:为了减少爬链家的次数,上面的操作只是在estate列表页就可以做完,通过house的URL作为一个house的唯一主键,不用再爬house的详细信息页面
 */
public class HouseCronJob implements Job, PageProcessor {
    private Logger logger = Logger.getLogger(HouseCronJob.class.getCanonicalName());
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private final static String LOG_TAG = "[HouseCronJob] ";

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    HouseDao houseDao = (HouseDao) ctx.getBean("houseDao");
    HouseHistoryDao houseHistoryDao = (HouseHistoryDao) ctx.getBean("houseHistoryDao");
    SchoolDao schoolDao = (SchoolDao) ctx.getBean("schoolDao");
    SchoolDistrictDao schoolDistrictDao = (SchoolDistrictDao) ctx.getBean("schoolDistrictDao");

    /**
     * 处理小区出售的二手房信息
     * URL Pattern : http://sh.lianjia.com/xiaoqu/5011000017872/esf/
     * @param page
     */
    private void processHouseList(Page page) {
        logger.info(String.valueOf(page.getHtml().xpath("//*[@id=\"house-lst\"]/li").nodes().size()));
        String pageData = page.getHtml().xpath("/html/body/div[5]/div[2]/div/div[2]/div/@page-data").toString();
        JSONObject json = (JSONObject) JSONObject.parse(pageData);
        if(json != null) {
            int totalPage = json.getInteger("totalPage");
            int curPage = json.getInteger("curPage");
            logger.info(String.format("TotalPage:%d, curPage:%d", totalPage, curPage));
            String curUrl = page.getUrl().toString();
            curUrl = curUrl.substring(0, curUrl.length() - 1);
            curUrl = curUrl.substring(0, curUrl.lastIndexOf('/'));
            logger.info("Cururl:" + curUrl);
            if(++curPage <= totalPage) {
                page.addTargetRequest(curUrl + "/pg" + String.valueOf(curPage));
            }
        }

        //这里是真正处理列表了
        for(Selectable url : page.getHtml().xpath("//*[@id=\"house-lst\"]/li").nodes()) {
            logger.info(url.xpath("li/div[2]/h2/a/text()").toString());
            String houseUrl = url.xpath("li/div[2]/h2/a/@href").toString();
            String price = url.xpath("li/div[2]/div[2]/div[1]/span/text()").toString();
            String pageId = getPageIdFromUrl(houseUrl);
            House house = houseDao.selectByPageId(pageId);
            logger.info(String.format("%s Hosue href = %s", LOG_TAG, houseUrl));
            if(house == null) {
                //DB中没有,表明是新房
                page.addTargetRequest(houseUrl);
            } else {
                if(house.getPrice() != Integer.parseInt(price)) {
                    //价格不等时,将现有的价格插入househistory
                    HouseHistory houseHistory = new HouseHistory();
                    houseHistory.setHouseId(house.getId());
                    houseHistory.setPrice(house.getPrice());
                    //要是有modified的时间,因为一个房子的修改时间可能被更新多次,而create time是不变的,所以用最后一次修改的时间
                    houseHistory.setDate(house.getGmt_modified());
                    houseHistoryDao.insert(houseHistory);
                    page.addTargetRequest(houseUrl);
                }
            }

        }

    }

    /**
     * 从house的url中提取pageid
     * 传递的参数是http://sh.lianjia.com/ershoufang/SH0000519300.html
     * 返回SH0000519300
     * @param url
     * @return
     */
    private String getPageIdFromUrl(String url) {
        if(url != null) {
            return url.substring(url.lastIndexOf('/') + 1, url.indexOf("html") - 1);
        }
        return null;
    }

    private void processHouse(Page page) {
        House house = new House();
        String url = page.getUrl().toString();
        String estateUrl = page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[8]/dd/a/@href").toString();
        estateUrl = estateUrl.substring(0, estateUrl.length() - 1);
        String estatePageId = estateUrl.substring(estateUrl.lastIndexOf('/') + 1, estateUrl.length());
        int estateId = estateDao.selectByPageId(estatePageId).getId();
        house.setTitle(page.getHtml().xpath("/html/body/div[5]/div[1]/div[1]/h1/text()").toString());
        house.setPrice(Integer.parseInt(page.getHtml().
                xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[1]/dd/span/strong/text()").toString()));
        String area = page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[1]/dd/span/i/text()").toString();
        house.setArea(Float.parseFloat(area.substring(2, area.length() - 1)));
        house.setUrl(url);
        house.setBuildYear(page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[8]/dd/text()").toString());
        house.setDescription("");
        house.setPageId(getPageIdFromUrl(url));
        house.setType(page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[5]/dd/text()").toString());
        house.setEstateId(estateId);
        house.setStatus(Constants.HOUSE_STATUS_ONLINE);
        House dbHouse = houseDao.selectByPageId(getPageIdFromUrl(url));
        if(dbHouse == null) {
            houseDao.insert(house);
        } else {
            house.setId(dbHouse.getId());
            houseDao.updateById(house);
        }
        logger.info(house.toString());
        /*try {
            houseDao.insert(house);
        } catch (Exception ex) {
            logger.severe(ex.getMessage().toString());
        }*/

    }

    @Override
    public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
        System.out.println(jobCtx.getTrigger().getJobKey() + "time is " + new Date());
        Spider.create(new HouseCronJob()).
                addUrl("https://www.baidu.com").
                thread(5).run();
    }

    private void addTargetPagesFromDB(Page page) {
        List<SchoolDistrict> schoolDistricts = schoolDistrictDao.listAll();
        for(SchoolDistrict schoolDistrict : schoolDistricts) {
            Estate estate = estateDao.selectById(schoolDistrict.getEstateId());
            page.addTargetRequest(estate.getUrl() + "esf/");
            //break;
        }
        //for single test
        //page.addTargetRequest("http://sh.lianjia.com/xiaoqu/5011000017872/esf/");
    }

    @Override
    public void process(Page page) {
        logger.info(String.format("Current Page:%s", page.getUrl().toString()));
        if(page.getUrl().regex(Constants.URL_ONSALE_LIST_REGEX).match()) {
            logger.info(LOG_TAG + "processONSaleList");
            processHouseList(page);
        } else if(page.getUrl().regex(Constants.URL_HOUSE_DETAIL_REGEX).match()) {
            logger.info(LOG_TAG + "processHouse");
            processHouse(page);
        } else if(page.getUrl().toString().equals("https://www.baidu.com")) {
            //fake url, read from DB
            addTargetPagesFromDB(page);
        }
        else {
            logger.info("URL Pattern error!");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
