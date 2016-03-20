package com.pt.schooldistrict.util;

/**
 * Created by da.zhang on 16/1/22.
 */
public class Constants {

    public final static String START_PAGE = "http://sh.lianjia.com/xiaoqu/5011000012777/esf/";

    /**
     * 链家的小区信息列表
     */
    public final static String URL_LIST_REGEX = "http://sh.lianjia.com/xiaoqu/[a-z]+/?";
    /**
     * 链家的每个小区详细信息的列表
     */
    public final static String URL_ESTATE_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+\\.html$";
    /**
     * 链家的每个小区二手房的列表
     */
    public final static String URL_ONSALE_LIST_REGEX = "http://sh.lianjia.com/ershoufang/\\w+$";
    /**
     * 每个出售的二手房的详细信息
     */
    public final static String URL_HOUSE_DETAIL_REGEX = "http://sh.lianjia.com/ershoufang/\\w*\\.html$";

    public final static String HOUSE_STATUS_ONLINE = "online";

    /**
     * 这个需要访问具体的页面才能确保一个房子已卖出
     */
    public final static String HOUSE_STATUS_SOLD = "sold";

    /**
     * 房子已不在线,代表已卖出或下架
     */
    public final static String HOUSE_STATUS_GONE = "gone";

}
