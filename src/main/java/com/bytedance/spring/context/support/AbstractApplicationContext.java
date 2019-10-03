package com.bytedance.spring.context.support;

import com.bytedance.spring.beans.BeanFactory;

//定义了容器刷新的模板方法，spring中例如ClassPathXmlApplicationContext等子类都会调这个方法
public abstract class AbstractApplicationContext implements BeanFactory {

    //IOC的核心模板方法
    public void refresh() {

    }

}
