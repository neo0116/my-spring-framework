package com.bytedance.myservice.controller;

import com.bytedance.spring.context.annotation.BDAutowired;
import com.bytedance.spring.context.annotation.BDController;
import com.bytedance.spring.context.annotation.BDRequestMapping;
import com.bytedance.spring.context.annotation.BDRequestParam;
import com.bytedance.myservice.service.TestService;
import com.bytedance.spring.webmvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@BDController
@BDRequestMapping(value = "/test")
public class TestController {

    @BDAutowired
    TestService testService;

    @BDRequestMapping(value = "/v1")
    public ModelAndView v1(
            @BDRequestParam(value = "name")
                    String name, HttpServletResponse response) {
        String result = testService.getName(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @BDRequestMapping(value = "/v2")
    public ModelAndView v2(
            @BDRequestParam(value = "name")
                    String name) {
        try {
            String name1 = testService.getName(name);
            Map<String, String> model = new HashMap<>();
            model.put("userNick", "路人是妖怪");
            model.put("name", name1);
            ModelAndView mv = new ModelAndView("login", model);
//            int i = 1 / 0;
            return mv;
        } catch (Exception e) {
            Map<String, String> model = new HashMap<>();
            model.put("data", "呵呵");
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            ModelAndView mv = new ModelAndView("500", model);
            return mv;
        }
    }

}
