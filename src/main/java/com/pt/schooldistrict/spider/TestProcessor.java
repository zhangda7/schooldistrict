package com.pt.schooldistrict.spider;

import com.alibaba.fastjson.JSONObject;
import com.pt.schooldistrict.MyConsolePipeline;
import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.HouseDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.util.Util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/1/16.
 */
public class TestProcessor implements PageProcessor{

    private Logger logger = Logger.getLogger(TestProcessor.class.getCanonicalName());

    private final static String LOG_TAG = "[LianjiaProcessor] ";
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private final static String START_PAGE = "http://sh.lianjia.com/xiaoqu/5011000012777/esf/";
    private final static String URL_LIST_REGEX = "http://sh.lianjia.com/xiaoqu/[a-z]+/?";
    private final static String URL_DETAIL_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+/?$";
    private final static String URL_ONSALE_LIST_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+/esf/?";
    private final static String URL_HOUSE_DETAIL_REGEX = "http://sh.lianjia.com/ershoufang/\\w*";

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    HouseDao houseDao = (HouseDao) ctx.getBean("houseDao");

    private void processList(Page page) {
        String pageData = page.getHtml().xpath("/html/body/div[5]/div[2]/div[3]/@page-data").toString();
        JSONObject json = (JSONObject) JSONObject.parse(pageData);
        int totalPage = json.getInteger("totalPage");
        int curPage = json.getInteger("curPage");
        if(++curPage <= totalPage) {
            page.addTargetRequest(START_PAGE + "/pg" + String.valueOf(curPage));
        }
        logger.info(String.valueOf(page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul/li").nodes().size()));
        for(Selectable url : page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul/li").nodes()) {
            logger.info(url.xpath("li/div[2]/h2/a/@href").toString());
            page.addTargetRequest(url.xpath("li/div[2]/h2/a/@href").toString());
        }

    }





    /**
     * 处理小区的信息
     * URL Pattern:http://sh.lianjia.com/xiaoqu/5011000004411/
     * @param page
     */
    private void processEstate(Page page) {
        Estate estate = new Estate();
        String url = page.getUrl().toString();
        try {
            estate.setName(page.getHtml().xpath("/html/body/div[5]/div[1]/section/div[1]/div[1]/a[1]/h1/text()").toString());
            estate.setAveragePrice(Integer.parseInt(page.getHtml().xpath("//*[@id=\"zoneView\"]/div[2]/div[2]/div/span/text()").toString().trim()));
            estate.setDistrict(Util.getDistrictCode(START_PAGE.substring(START_PAGE.lastIndexOf('/') + 1)));
            estate.setBuildYear(page.getHtml().xpath("//*[@id=\"zoneView\"]/div[2]/div[3]/ol/li[1]/span/span/text()").toString());
            estate.setUrl(url);
            String urlWithoutLastSlash = url.substring(0, url.length() - 1);
            estate.setPageId(urlWithoutLastSlash.substring(urlWithoutLastSlash.lastIndexOf('/') + 1));
        } catch(Exception ex) {

        }

        logger.info(estate.toString());
        estateDao.insert(estate);
        //page.putField("estate", estate);
        //把二手房的页面添加进来
        //page.addTargetRequest(url + "esf/");
    }



    @Override
    public void process(Page page) {
        logger.info(String.format("Current Page:%s", page.getUrl().toString()));
        if(page.getUrl().regex(URL_LIST_REGEX).match()) {
            logger.info(LOG_TAG + "processList");
            processList(page);
        } else if(page.getUrl().regex(URL_DETAIL_REGEX).match()) {
            logger.info(LOG_TAG + "processEstate");
            processEstate(page);
        } /*else if(page.getUrl().regex(URL_ONSALE_LIST_REGEX).match()) {
            logger.info(LOG_TAG + "processONSaleList");
            processHouseList(page);
        } else if(page.getUrl().regex(URL_HOUSE_DETAIL_REGEX).match()) {
            logger.info(LOG_TAG + "processHouse");
            processHouse(page);
        }*/
        else {
            logger.info("URL Pattern error!");
        }
        //page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        /*page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//*[@id='js-pjax-container']/div/div/div[1]/h1/span[2]/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));*/
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new TestProcessor()).
                addUrl("http://sh.lianjia.com/ershoufang/SH0000519300.html").
                addPipeline(new MyConsolePipeline()).
                thread(5).run();
    }

}
