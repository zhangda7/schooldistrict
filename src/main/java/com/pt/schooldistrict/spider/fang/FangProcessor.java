package com.pt.schooldistrict.spider.fang;

import com.alibaba.fastjson.JSONObject;
import com.pt.schooldistrict.MyConsolePipeline;
import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.util.Util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/1/16.
 */
public class FangProcessor implements PageProcessor{

    private Logger logger = Logger.getLogger(FangProcessor.class.getCanonicalName());

    private final static String LOG_TAG = "[FangProcessor] ";
    private Site site = Site.me().setRetryTimes(5).setSleepTime(500).setTimeOut(20000);
    //pattern : chool-a025代表浦东，爬整个上海市的用http://esf.sh.fang.com/school/
    private final static String START_PAGE = "http://esf.sh.fang.com/school-a025/";
    //pattern: http://esf.sh.fang.com/school-a025/i32/
    private final static String URL_LIST_REGEX = START_PAGE + "i//d*/?";
    //pattern : http://esf.sh.fang.com/school/6442.htm
    private final static String URL_DETAIL_REGEX = "http://esf.sh.fang.com/school/\\d+/?$";
    private final static String URL_SEARCH_REGEX = "http://sh.lianjia.com/xiaoqu/rs";

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");
    SchoolDao schoolDao = (SchoolDao) ctx.getBean("schoolDao");

    private void processList(Page page) {
        /**
         * Fang的分页是这样的
         * //*[@id="PageControl1_hlk_next"]/@href是下一页的xpath，每次只获取下一页好了
         * XPath中@表示属性
         */
        String nextPage = page.getHtml().xpath("//*[@id=\"PageControl1_hlk_next\"]/@href").toString();
        if(nextPage != null && ! nextPage.isEmpty()) {
            logger.info("Next page : " + nextPage);
            page.addTargetRequest(nextPage);
        }
        //
        //logger.info(page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul").toString());
        logger.info(String.valueOf(page.getHtml().xpath("/html/body/div[4]/div[4]/div[2]/dl").nodes().size()));
        for(Selectable url : page.getHtml().xpath("/html/body/div[4]/div[4]/div[2]/dl").nodes()) {
            String schoolUrl = url.xpath("dl/dd/p[1]/a/@href").toString();
            String schoolName = url.xpath("dl/dd/p[1]/a/text()").toString();
            School exsit = schoolDao.selectByNameEquals(schoolName);
            if(exsit == null) {
                //在数据库中查找不到时,do nothing
                continue;
            } else {
                    page.addTargetRequest(schoolUrl);
                }
            }
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
            estate.setUrl(url);
            String urlWithoutLastSlash = url.substring(0, url.length() - 1);
            estate.setPageId(urlWithoutLastSlash.substring(urlWithoutLastSlash.lastIndexOf('/') + 1));
            estate.setAddress(page.getHtml().xpath("/html/body/div[5]/div[1]/section/div[1]/div[1]/span[2]/text()").toString());
            estate.setBuildYear(page.getHtml().xpath("//*[@id=\"zoneView\"]/div[2]/div[3]/ol/li[1]/span/span/text()").toString());
            if(estate.getBuildYear() == null || estate.getBuildYear().isEmpty()) {
                //可能有的网页获取的xpath不一样
                estate.setBuildYear(page.getHtml().xpath("//*[@id=\"zoneView\"]/div[2]/div[2]/div/div/ol/li[1]/span/span/text()").toString());
            }
            String average = page.getHtml().xpath("//*[@id=\"zoneView\"]/div[2]/div[2]/div/span/text()").toString().trim();
            estate.setDistrict(Util.getDistrictCode(START_PAGE.substring(START_PAGE.lastIndexOf('/') + 1)));
            if(! average.equals("暂无信息")) {
                estate.setAveragePrice(Integer.parseInt(average));
            }

        } catch(Exception ex) {

        }

        logger.info(estate.toString());
        Estate exsit = estateDao.selectByNameEquals(estate.getName());
        if(exsit == null) {
            //在数据库中查找不到时,更新
            estateDao.insert(estate);
        } else {
            //目前认为只要在数据库中能找到,就不进行后续更新了
            if(exsit.getAddress() == null ||
                    exsit.getUrl() == null ||
                    exsit.getPageId() == null) {
                //处理有时插入失败的情况,指只有小区的名字被插入,其他信息则位空,要相应更新
                estate.setId(exsit.getId());
                estateDao.updateById(estate);
                //System.exit(1);
            }
        }

        //page.putField("estate", estate);
        //把二手房的页面添加进来
        //page.addTargetRequest(url + "esf/");
    }

    private void processSearchList(Page page) {

        logger.info(String.valueOf(page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul/li").nodes().size()));
        for(Selectable url : page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul/li").nodes()) {
            String estateUrl = url.xpath("li/div[2]/h2/a/@href").toString();
            String name = url.xpath("li/div[2]/h2/a/@title").toString();
            logger.info(String.format("Name : %s, Url : %s", name, estateUrl));
            page.addTargetRequest(estateUrl);
        }

        //JSONObject json = JSONObject.parseObject();
    }



    @Override
    public void process(Page page) {
        logger.info(String.format("Current Page:%s", page.getUrl().toString()));
        if(page.getUrl().toString().startsWith(URL_SEARCH_REGEX)) {
            processSearchList(page);
        } else if(page.getUrl().regex(URL_LIST_REGEX).match()) {
            processList(page);
        } else if(page.getUrl().regex(URL_DETAIL_REGEX).match()) {
            processEstate(page);
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
        Spider.create(new FangProcessor()).
                //addUrl(START_PAGE).
                addUrl("https://www.baidu.com"). // for update from DB
                addPipeline(new MyConsolePipeline()).
                thread(5).run();
    }

}
