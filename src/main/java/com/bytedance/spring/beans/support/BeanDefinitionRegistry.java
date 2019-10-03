package com.bytedance.spring.beans.support;

import com.bytedance.spring.beans.config.BeanDefinition;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
