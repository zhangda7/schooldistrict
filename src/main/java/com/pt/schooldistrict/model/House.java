package com.pt.schooldistrict.model;

import com.google.gson.Gson;

/**
 * Created by da.zhang on 16/1/16.
 */
public class House {

    private static Gson gson = new Gson();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 该房子的标题
     */
    private String title;

    /**
     * 中介对该房子的描述
     */
    private String description;

    /**
     * 价格
     */
    private int price;

    /**
     * 房子的面积,浮点数
     */
    private float area;

    /**
     * 该房子具体出售的URL
     */
    private String url;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * 该房子对应的URL中的ID
     */
    private String pageId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEstateId() {
        return estateId;
    }

    public void setEstateId(int estateId) {
        this.estateId = estateId;
    }

    private int estateId;


    /**
     * 该房子的户型,一室两厅等
     */
    private String type;

    /**
     * 具体该房屋的建筑年代,考虑同一小区不同期建筑年代不同
     */
    private String buildYear;

    //房屋具体是哪一栋,考虑可能同一小区的不同楼栋也是属于不同学区,比如潍坊十村,但极其罕见,暂时comment掉
    //private int houseNumber;

    public String getBuildYear() {
        return buildYear;
    }

    public void setBuildYear(String buildYear) {
        this.buildYear = buildYear;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public float getArea() {
        return area;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setArea(float area) {
        this.area = area;
    }


    public String toString() {
        return gson.toJson(this);
    }


}
