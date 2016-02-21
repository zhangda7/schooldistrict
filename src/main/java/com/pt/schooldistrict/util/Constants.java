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
    public final static String URL_ESTATE_DETAIL_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+/?$";
    /**
     * 链家的每个小区二手房的列表
     */
    public final static String URL_ONSALE_LIST_REGEX = "http://sh.lianjia.com/xiaoqu/\\d+/esf/?";
    /**
     * 每个出售的二手房的详细信息
     */
    public final static String URL_HOUSE_DETAIL_REGEX = "http://sh.lianjia.com/ershoufang/\\w*";

    public final static String HOUSE_STATUS_ONLINE = "online";

    public final static String HOUSE_STATUS_SOLD = "sold";

}
