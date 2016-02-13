package com.pt.schooldistrict.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by da.zhang on 16/2/13.
 */
@Controller
//@RequestMapping("/school")
public class SchoolController {

    @RequestMapping("/hi")
    public String listAll() {
        return "index";
    }
}
