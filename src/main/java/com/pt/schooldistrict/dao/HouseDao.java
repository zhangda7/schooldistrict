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

    List<House> selectOnlineByEstateId(int id);

    void updateById(House house);

    House selectByPageId(String pageId);

    House selectOnlineByPageId(String pageId);

    /**
     * 更新一个estateId内的所有状态是online的house至gone状态
     * @param estateId
     */
    void updateEstateStatus(int estateId);

    void updateStatusById(House house);

}
