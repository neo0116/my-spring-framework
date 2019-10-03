package com.bytedance.spring.beans.support;

import com.bytedance.spring.beans.BeanFactory;
import com.bytedance.spring.beans.config.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//默认容器实现，spring中存放BeanDefinitionMap
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, BeanFactory {

    /** List of bean definition names, in registration order. */
    public volatile List<BeanDefinition> beanDefinitions = new ArrayList<>(256);

    /** Map of bean definition objects, keyed by bean name. */
    public final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {

    }
}
