package com.bytedance.spring.beans;
//spring顶层规范接口
public interface BeanFactory {

    //核心方法
    Object getBean(String beanName) throws Exception;

}
