package com.pt.schooldistrict.spider.lianjia;

import com.alibaba.fastjson.JSONObject;
import com.pt.schooldistrict.MyConsolePipeline;
import com.pt.schooldistrict.dao.EstateDao;
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
public class LianjiaProcessor implements PageProcessor{

    private Logger logger = Logger.getLogger(LianjiaProcessor.class.getCanonicalName());

    private final static String LOG_TAG = "[LianjiaProcessor] ";
    private Site site = Site.me().setRetryTimes(5).setSleepTime(500).setTimeOut(20000);
    private final static String START_PAGE = "http://sh.lianjia.com/xiaoqu/pudongxinqu";
    private final static String URL_LIST_REGEX = "http://sh.lianjia.com/xiaoqu/[a-z]+/?";
    private final static String URL_DETAIL_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+/?$";
    private final static String URL_ONSALE_LIST_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+/esf/?";
    private final static String URL_SEARCH_REGEX = "http://sh.lianjia.com/xiaoqu/rs";
    private final static String URL_SCHOOL_START_PAGE = "http://sh.lianjia.com/xuequfang/pudongxinqu/";
    private final static String URL_SCHOOL_LIST_REGEX = URL_SCHOOL_START_PAGE;
    private final static String URL_SCHOOL_DETAIL_REGEX = "http://sh.lianjia.com/xuequfang/\\d+.html";

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
    EstateDao estateDao=(EstateDao) ctx.getBean("estateDao");

    private void processSchoolList(Page page) {
        //处理URL_SCHOOL_START_PAGE = "http://sh.lianjia.com/xuequfang/pudongxinqu/";开头的学校列表

    }

    private void processSchoolDetail(Page page) {
        //处理每个学校内的划片小区
    }

    private void processList(Page page) {
        /**
         * 链家的分页是这样的
         * 在分页部分的html中,只有内容很少的一个div,其中的page-data属性的内容是{"totalPage:"100, "curPage":1}
         * 之后再通过css来实现分页链接,所以直接获取下一页的html时获取不到的,直接获取这个内容,再自己拼好了
         * XPath中@表示属性
         */
        String pageData = page.getHtml().xpath("/html/body/div[5]/div[2]/div[3]/@page-data").toString();
        JSONObject json = (JSONObject) JSONObject.parse(pageData);
        int totalPage = json.getInteger("totalPage");
        int curPage = json.getInteger("curPage");
        logger.info(String.format("TotalPage:%d, curPage:%d", totalPage, curPage));
        if(++curPage <= totalPage) {
            page.addTargetRequest(START_PAGE + "/pg" + String.valueOf(curPage));
        }
        logger.info(json.get("totalPage").toString());
        //
        //logger.info(page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul").toString());
        logger.info(String.valueOf(page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul/li").nodes().size()));
        for(Selectable url : page.getHtml().xpath("/html/body/div[5]/div[2]/div[2]/ul/li").nodes()) {
            String estateUrl = url.xpath("li/div[2]/h2/a/@href").toString();
            String name = url.xpath("li/div[2]/h2/a/@title").toString();
            logger.info(String.format("Name : %s, Url : %s", name, estateUrl));
            Estate exsit = estateDao.selectByNameEquals(name);
            if(exsit == null) {
                //在数据库中查找不到时,更新
                page.addTargetRequest(estateUrl);
            } else {
                //目前认为只要在数据库中能找到,就不进行后续更新了
                if(exsit.getAddress() == null ||
                        exsit.getUrl() == null ||
                        exsit.getPageId() == null) {
                    //处理有时插入失败的情况,指只有小区的名字被插入,其他信息则位空,要相应更新
                    page.addTargetRequest(estateUrl);
                }
            }
            //page.addTargetRequest(url.xpath("li/div[2]/h2/a/@href").toString());
        }

        //JSONObject json = JSONObject.parseObject();
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
        } else if(page.getUrl().regex(URL_SCHOOL_LIST_REGEX).match()) {
            processSchoolList(page);
        } else if(page.getUrl().regex(URL_SCHOOL_DETAIL_REGEX).match()) {
            processSchoolDetail();
        } else if(page.getUrl().toString().equals("https://www.baidu.com"))  {
            //page.addTargetRequest("http://sh.lianjia.com/xiaoqu/rs浦电路50号");
            //fake url, read from DB
            List<Estate> estates = estateDao.listAll();
            for(Estate estate : estates) {
                if(estate.getAddress() == null ||
                        estate.getUrl() == null ||
                        estate.getPageId() == null) {
                    page.addTargetRequest("http://sh.lianjia.com/xiaoqu/rs" + estate.getName());
                }


            }
        }

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
        Spider.create(new LianjiaProcessor()).
                //addUrl(START_PAGE).
                addUrl("https://www.baidu.com"). // for update from DB
                addPipeline(new MyConsolePipeline()).
                thread(5).run();
    }

}
