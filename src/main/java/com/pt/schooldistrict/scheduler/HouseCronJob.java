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
 *
 * 更新房子状态的逻辑:
 * 1.处理一个小区时,将其现在所有online的房子都改成gone
 * 2.获取里面每条列表,如果发现有pageId相同的,设为online,该新增的还是新增.这个简单的逻辑就可以,即使这一次全置为gone后,craw的任务全挂,
 * 使得房子状态还是gone,但下次启动时仍然有机会把房子状态重新置为online
 * 3.每次置为gone时只找online的,这样以前的gone就还是保存着当时的gmt_modified
 *
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
            //说明有分页信息,处理分页
            int totalPage = json.getInteger("totalPage");
            int curPage = json.getInteger("curPage");
            logger.info(String.format("TotalPage:%d, curPage:%d", totalPage, curPage));
            String curUrl = page.getUrl().toString();
            //这里的分页有个小的问题,格式必须完全是http://sh.lianjia.com/xiaoqu/5011000017872/esf/pg1才可以
            //之所以要做substring,是防止重复添加pg到尾端
            if(curUrl.indexOf("pg") >= 0) {
                curUrl = curUrl.substring(0, curUrl.length() - 1);
                curUrl = curUrl.substring(0, curUrl.lastIndexOf('/'));
            }

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
            if(price == null) {
                continue;
            }
            int houseProce = (int)Double.parseDouble(price);
            String pageId = getPageIdFromUrl(houseUrl);
            House house = houseDao.selectByPageId(pageId);
            logger.info(String.format("%s Hosue href = %s", LOG_TAG, houseUrl));
            if(house == null) {
                //DB中没有,表明是新房
                page.addTargetRequest(houseUrl);
            } else {
                if(house.getStatus().equals(Constants.HOUSE_STATUS_GONE)) {
                    //遇到状态是gone的,就更新状态为online
                    house.setStatus(Constants.HOUSE_STATUS_ONLINE);
                    houseDao.updateStatusById(house);
                }
                if(house.getPrice() != houseProce) {
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
        house.setPrice((int)Double.parseDouble(page.getHtml().
                xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[1]/dd/span/strong/text()").toString()));
        String area = page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[1]/dd/span/i/text()").toString();
        house.setArea(Float.parseFloat(area.substring(2, area.length() - 1)));
        house.setUrl(url);
        house.setMainPic(page.getHtml().xpath("//*[@id=\"album-box\"]/div[1]/div/ul/li/img/@src").toString());
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
            //更新该estate的online的house的状态为gone
            houseDao.updateEstateStatus(estate.getId());
            //一定要在最后加上esf/pg1,不加pg1的话分页有问题
            page.addTargetRequest(estate.getUrl() + "esf/pg1");
            //break;
        }
        //for single test
        //houseDao.updateEstateStatus(161);
        //page.addTargetRequest("http://sh.lianjia.com/xiaoqu/5011000017872/esf/pg1");
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
