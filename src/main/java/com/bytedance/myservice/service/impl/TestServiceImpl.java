package com.bytedance.myservice.service.impl;

import com.bytedance.myservice.service.TestService;
import com.bytedance.spring.context.annotation.BDService;

@BDService
public class TestServiceImpl implements TestService {

    @Override
    public String getName(String name) {
        return "xm";
    }

}
