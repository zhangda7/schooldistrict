package com.pt.schooldistrict.dao;

import com.pt.schooldistrict.model.Estate;
import java.util.List;

/**
 * Created by da.zhang on 16/1/16.
 */
public interface EstateDao {

    void insert(Estate estate);

    List<Estate> listAll();

    void select(String name);

    Estate selectByNameEquals(String name);

    List<Estate> selectByNameLike(String name);

    List<Estate> selectByAddressLike(String address);

    Estate selectByPageId(String pageId);

    void updateById(Estate estate);

    List<Estate> selectBySchoolId(int schoolId);

}
