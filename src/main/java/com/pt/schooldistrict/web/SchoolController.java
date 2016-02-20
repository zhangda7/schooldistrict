package com.pt.schooldistrict.web;

import com.google.gson.Gson;
import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.model.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by da.zhang on 16/2/13.
 */
@RestController
@RequestMapping("/school")
public class SchoolController {

    private Gson gson = new Gson();

    @Autowired
    private SchoolDao schoolDao;

    @RequestMapping("/hi")
    public String listAll(Model model) {
        model.addAttribute("message", schoolDao.listAll().toString());
        return "xuequfang";
    }

    @ResponseBody
    @RequestMapping(value = "/hello")
    public String listAllUsers() {
        List<School> schools = schoolDao.listAll();
        if(schools.isEmpty()){
            //return new ResponseEntity<List<School>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        String ret = "123";
        return gson.toJson(schools);
        //return new ResponseEntity<List<School>>(schools, HttpStatus.OK);
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
