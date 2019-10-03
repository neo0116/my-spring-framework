package com.bytedance.spring.beans.config;

public interface SingltonBeanRegistry {

    Object getSinglton(String beanName);

}
