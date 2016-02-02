package com.pt.schooldistrict.dao;

import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.model.SchoolDistrict;

import java.util.List;

/**
 * Created by da.zhang on 16/1/21.
 */
public interface SchoolDistrictDao {
    void insert(SchoolDistrict school);

    List<SchoolDistrict> listAll();

    void select(String name);

    List<SchoolDistrict> selectById(int id);

    void deleteById(int id);

}
