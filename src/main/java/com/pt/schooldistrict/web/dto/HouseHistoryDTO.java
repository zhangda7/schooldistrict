package com.pt.schooldistrict.web.dto;

import com.pt.schooldistrict.model.House;

import java.util.Date;
import java.util.List;

/**
 * Created by da.zhang on 16/2/21.
 * DTO for 房子价格变动的对象
 */
public class HouseHistoryDTO {

    public class HistoryPrice {

        public int price;

        public Date date;
    }

    private List<Integer> historyPrices;

    private int houseId;

    private int estateName;

    private String schoolName;

    private String area;

    private String type;

    private String buildYear;

    private String url;

}
