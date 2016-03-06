package com.pt.schooldistrict.web;

import com.pt.schooldistrict.dao.SchoolDao;
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
//@RequestMapping("/")
public class SchoolController {

    private Logger logger = Logger.getLogger(SchoolController.class.getName());

    @Autowired
    private SchoolDao schoolDao;

    @RequestMapping("/index.html")
    public ModelAndView listAllSchool() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/pages/school.html");
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/schoolList.json", method=RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String listAllSchools() throws UnsupportedEncodingException {
        List<School> schools = schoolDao.listAll();
        if(schools.isEmpty()){
            //return new ResponseEntity<List<School>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        String ret = "中文";
        logger.info(Util.toJson(schools));
        return Util.toJson(schools);
        //return new ResponseEntity<List<School>>(schools, HttpStatus.OK);
    }

    @RequestMapping("/www")
    public ModelAndView list(@RequestParam(value = "user", required = true) String user,
                             @CookieValue(value = "jsessionid", required = false) String jsession) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/pages/school.html");
        List<School> schools = schoolDao.listAll();
        StringBuilder sb = new StringBuilder();
        for(School s : schools) {
            sb.append(s.getName() + " | ");
        }
        mav.addObject("list", sb.toString());
        return mav;

    }
}
