package com.pt.schooldistrict.model;

import com.google.gson.Gson;

/**
 * Created by da.zhang on 16/1/16.
 * POJO for estate
 */
public class Estate {

    private static Gson gson = new Gson();

    public Estate() {
        district = 0;
        averagePrice = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    /**
     * 小区名称
     */
    private String name;

    /**
     * 小区地址,xx路xx号/弄
     */
    private String address;

    private int averagePrice;

    private String buildYear;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 该小区对应的网上的URL
     */
    private String url;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * 小区URL中的唯一ID,在链家里应该是唯一标示一个小区的信息
     */
    private String pageId;



    /**
     * 属于哪个区,浦东,静安等,用int标示
     */
    private int district;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getAveragePrice() {
        return averagePrice;
    }

    public String getBuildYear() {
        return buildYear;
    }

    public int getDistrict() {
        return district;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAveragePrice(int averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void setBuildYear(String buildYear) {
        this.buildYear = buildYear;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
