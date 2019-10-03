package com.bytedance.myservice.service;

import com.bytedance.spring.context.annotation.BDService;

@BDService
public class TestServiceImpl implements TestService {

    @Override
    public String getName(String name) {
        return "xm";
    }

}
