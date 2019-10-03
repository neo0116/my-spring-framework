package com.bytedance.spring.beans.support;

import com.bytedance.spring.beans.BeanFactory;

public abstract class AbstractBeanFactory extends DefaultSingltonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String beanName) throws Exception {
        return doGetBean(beanName);
    }

    private Object doGetBean(String beanName) {
        //创建
        return getSingleton(beanName, () -> {
            return createBean(beanName, (DefaultListableBeanFactory) this);
        });
    }

    protected abstract Object createBean(String beanName, DefaultListableBeanFactory defaultListableBeanFactory);
}
