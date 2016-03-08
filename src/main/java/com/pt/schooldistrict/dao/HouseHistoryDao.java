package com.pt.schooldistrict.dao;

import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.model.HouseHistory;

import java.util.List;

/**
 * Created by da.zhang on 16/2/21.
 */
public interface HouseHistoryDao {

    void insert(HouseHistory houseHistory);

    List<HouseHistory> selectByEstateId(int estateId);

    List<HouseHistory> selectByHouseId(int houseId);
}
