package com.pt.schooldistrict.dao;

import com.pt.schooldistrict.model.House;
import com.pt.schooldistrict.model.School;

import java.util.List;

/**
 * Created by da.zhang on 16/1/21.
 */
public interface SchoolDao {
    void insert(School school);

    List<School> listAll();

    void select(String name);

    List<School> selectById(int id);

    School selectByNameEquals(String name);

    void deleteById(int id);

}
