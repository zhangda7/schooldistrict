package com.pt.schooldistrict.spider.lianjia;

import com.alibaba.fastjson.JSONObject;
import com.pt.schooldistrict.MyConsolePipeline;
import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.util.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/1/21.
 * 该类处理的事情是从sd_estate中获取待更新或添加的小区信息,之后遍历每个小区,将其中的二手房信息添加到数据库中
 */
public class EstateDetailProcessor implements PageProcessor {

    private Logger logger = Logger.getLogger(EstateDetailProcessor.class.getCanonicalName());
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private final static String LOG_TAG = "[EstateDetailProcessor] ";
    private final static String START_PAGE = "http://sh.lianjia.com/xiaoqu/pudongxinqu";
    /**
     * 缓存待处理请求的队列
     * 从DB读取的数据放入队列,之后process方法从队列中读取
     */
    private static ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    HouseDao houseDao = (HouseDao) ctx.getBean("houseDao");

    /**
     * 处理小区出售的二手房信息
     * URL Pattern : http://sh.lianjia.com/xiaoqu/5011000017872/esf/
     * @param page
     */
    private void processHouseList(Page page) {

        logger.info(String.valueOf(page.getHtml().xpath("//*[@id=\"house-lst\"]/li").nodes().size()));
        String pageData = page.getHtml().xpath("/html/body/div[5]/div[2]/div/div[2]/div/@page-data").toString();
        JSONObject json = (JSONObject) JSONObject.parse(pageData);
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
        for(Selectable url : page.getHtml().xpath("//*[@id=\"house-lst\"]/li").nodes()) {
            logger.info(url.xpath("li/div[2]/h2/a/text()").toString());
            page.addTargetRequest(url.xpath("li/div[2]/h2/a/@href").toString());
        }

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
        house.setUrl(page.getUrl().toString());
        house.setBuildYear(page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[8]/dd/text()").toString());
        house.setDescription("");
        house.setPageId(url.substring(url.lastIndexOf('/') + 1, url.indexOf("html") - 1));
        house.setType(page.getHtml().xpath("/html/body/div[5]/section[1]/div[2]/div[1]/dl[5]/dd/text()").toString());
        house.setEstateId(estateId);
        logger.info(house.toString());
        try {
            houseDao.insert(house);
        } catch (Exception ex) {
            logger.severe(ex.getMessage().toString());
        }

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
            //read one estate for test
            //Estate estates = estateDao.selectByNameEquals("崮山小区");
            //page.addTargetRequest(estates.getUrl() + "esf/pg1/");
            //read all estate
            List<Estate> estates = estateDao.listAll();
            for(Estate estate : estates) {
                page.addTargetRequest(estate.getUrl() + "esf/pg1/");
            }
        }
        else {
            logger.info("URL Pattern error!");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new EstateDetailProcessor()).
                addUrl("https://www.baidu.com").
                addPipeline(new MyConsolePipeline()).
                thread(5).run();
    }
}
