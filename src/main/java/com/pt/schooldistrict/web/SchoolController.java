package com.pt.schooldistrict.web;

import com.pt.schooldistrict.dao.SchoolDao;
import com.pt.schooldistrict.model.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by da.zhang on 16/2/13.
 */
@Controller
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private SchoolDao schoolDao;

    @RequestMapping("/hi")
    public String listAll(Model model) {
        model.addAttribute("message", schoolDao.listAll().toString());
        return "xuequfang";
    }

    @RequestMapping("/")
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
