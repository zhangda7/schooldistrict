package com.pt.schooldistrict.web.dto;

import com.pt.schooldistrict.model.House;

import java.util.List;

/**
 * Created by da.zhang on 16/2/21.
 * DTO for 房子价格变动的对象
 */
public class HouseHistoryDTO {

    private int houseId;

    private List<Integer> historyPrices;

    House house;

}
