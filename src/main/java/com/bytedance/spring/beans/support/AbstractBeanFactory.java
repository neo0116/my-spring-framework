package com.bytedance.spring.beans.support;

import com.bytedance.spring.beans.BeanFactory;

public abstract class AbstractBeanFactory extends DefaultSingltonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String beanName) throws Exception {
        return doGetBean(beanName);
    }

    private Object doGetBean(String beanName) {
        //创建，singltonObjects中有就直接取，否则会调用createBean方法，供给型函数式接口
        return getSingleton(beanName, () -> {
            return createBean(beanName, (DefaultListableBeanFactory) this);
        });
    }

    protected abstract Object createBean(String beanName, DefaultListableBeanFactory defaultListableBeanFactory);
}
