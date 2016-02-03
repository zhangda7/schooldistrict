package com.pt.schooldistrict.spider.fang;

import com.pt.schooldistrict.MyConsolePipeline;
import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.util.Util;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * Created by da.zhang on 16/1/16.
 */
public class FangProcessor implements PageProcessor{

    private Logger logger = Logger.getLogger(FangProcessor.class.getCanonicalName());

    private final static String LOG_TAG = "[FangProcessor] ";
    private Site site = Site.me().setRetryTimes(5).setSleepTime(500).setTimeOut(20000);
    //pattern : chool-a025代表浦东，爬整个上海市的用http://esf.sh.fang.com/school/
    private final static String SCHOOL_START_PAGE = "http://esf.sh.fang.com/school-a025/";
    //pattern: http://esf.sh.fang.com/school-a025/i32/
    private final static String SCHOOL_URL_LIST_REGEX = SCHOOL_START_PAGE + "i\\d*/?";
    //pattern : http://esf.sh.fang.com/school/6442.htm
    private final static String SCHOOL_URL_DETAIL_REGEX = "http://esf.sh.fang.com/school/\\d+.htm$";

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
            logger.info(String.format("School name : %s, url : %s", schoolName, schoolUrl));
            School exsit = schoolDao.selectByNameEquals(schoolName);
            if(exsit == null) {
                //在数据库中查找不到时,do nothing
                exsit = schoolDao.selectByAliasLike(schoolName);
            }
            if(exsit == null) {
                //name 和 alias都找不到,就不处理这个学校了
                continue;
            }
            else {
                page.addTargetRequest(schoolUrl);
            }
        }
    }



    /**
     * 处理小区的信息
     * URL Pattern:http://sh.lianjia.com/xiaoqu/5011000004411/
     * @param page
     */
    private void processSchool(Page page) {
        School school = new School();
        String url = page.getUrl().toString();
        String schoolName = page.getHtml().xpath("/html/body/div[2]/p/span[1]/text()").toString();
        school.setAddress(page.getHtml().xpath("/html/body/div[2]/div[2]/div[1]/div[2]/ul/li[3]/text()").toString());
        school.setComment(page.getHtml().xpath("/html/body/div[2]/p/span[2]/text()").toString());
        School exsit = schoolDao.selectByNameEquals(schoolName);
        if(exsit == null) {
            //在数据库中查找不到时,do nothing
            exsit = schoolDao.selectByAliasLike(schoolName);
        }
        if(exsit == null) {
            return ;
        }
        school.setId(exsit.getId());
        school.setComment(exsit.getComment() + " " + school.getComment());
        schoolDao.updateById(school);


    }

    



    @Override
    public void process(Page page) {
        logger.info(String.format("Current Page:%s", page.getUrl().toString()));
        //if(page.getUrl().toString().startsWith(URL_SEARCH_REGEX)) {
        //    processSearchList(page);
        //} else
        if(page.getUrl().regex(SCHOOL_URL_LIST_REGEX).match()) {
            processList(page);
        } else if(page.getUrl().regex(SCHOOL_URL_DETAIL_REGEX).match()) {
            processSchool(page);
        } else if(page.getUrl().toString().equals(SCHOOL_START_PAGE)) {
            processList(page);
        } else {
            logger.info("URL Pattern error!");
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new FangProcessor()).
                addUrl(SCHOOL_START_PAGE).
                //addUrl("https://www.baidu.com"). // for update from DB
                addPipeline(new MyConsolePipeline()).
                thread(5).run();
    }

}
