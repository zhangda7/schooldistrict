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

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by da.zhang on 16/2/13.
 */
@RestController
@RequestMapping("/estate")
public class EstateController {

    private Logger logger = Logger.getLogger(EstateController.class.getName());

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private SchoolDistrictDao schoolDistrictDao;

    @Autowired
    private EstateDao estateDao;

    @ResponseBody
    @RequestMapping(value = "/estatelist.json", method=RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String listBySchoolId(@RequestParam(value = "id", defaultValue = "0") int id) {
        if(id == 0) {
            return "empty";
            //return Util.toJson(estateDao.listAll());
        } else {
            List<Estate> estates = estateDao.selectBySchoolId(id);
            return Util.toJson(estates);
        }

    }

    @RequestMapping("/list.html")
    public ModelAndView listAllSchool(@RequestParam(value = "id", defaultValue = "0") int id) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("estate");
        mav.addObject("id", id);
        return mav;
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
