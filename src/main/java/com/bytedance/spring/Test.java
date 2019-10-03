package com.bytedance.spring;

import com.bytedance.spring.context.ApplicationContext;

public class Test {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ApplicationContext("classpath:application.properties");
        Object beanWrapper = applicationContext.getBean("testController");
        System.out.println(beanWrapper);
    }
}
