package com.pt.schooldistrict.dao;

import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.House;

import java.util.List;

/**
 * Created by da.zhang on 16/1/21.
 */
public interface HouseDao {
    void insert(House house);

    List<House> listAll();

    void select(String name);

    House selectById(int id);

    void deleteById(int id);

    List<House> selectByEstateId(int id);

    void updateById(House house);

    House selectByPageId(String pageId);

}
