package com.pt.schooldistrict.web;

import com.google.gson.Gson;
import com.pt.schooldistrict.dao.EstateDao;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.dao.SchoolDistrictDao;
import com.pt.schooldistrict.model.Estate;
import com.pt.schooldistrict.model.School;
import com.pt.schooldistrict.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by da.zhang on 16/2/13.
 */
@RestController
@RequestMapping("/estate")
public class EstateController {

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private SchoolDistrictDao schoolDistrictDao;

    @Autowired
    private EstateDao estateDao;

    @RequestMapping("/")
    public String listBySchoolId(@RequestParam(value = "id", defaultValue = "0") int id) {
        if(id == 0) {
            return Util.toJson(estateDao.listAll());
        } else {
            List<Estate> estates = estateDao.selectBySchoolId(id);
            return Util.toJson(estates);
        }

    }



    @RequestMapping("/www")
    public ModelAndView list(@RequestParam(value = "user", required = true) String user,
                             @CookieValue(value = "jsessionid", required = false) String jsession) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("school");
        List<School> schools = schoolDao.listAll();
        StringBuilder sb = new StringBuilder();
        for(School s : schools) {
            sb.append(s.getName() + " | ");
        }
        mav.addObject("list", sb.toString());
        return mav;

    }
}
