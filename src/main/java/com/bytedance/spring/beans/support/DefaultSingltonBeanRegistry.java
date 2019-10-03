package com.bytedance.spring.beans.support;

import com.bytedance.spring.beans.ObjectFactory;
import com.bytedance.spring.beans.config.SingltonBeanRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//保存单例bean
public class DefaultSingltonBeanRegistry implements SingltonBeanRegistry {

    /** Cache of singleton objects: bean name to bean instance. */
    public final Map<String, Object> singltonObjects = new ConcurrentHashMap<>(256);

    @Override
    public Object getSinglton(String beanName) {
        return singltonObjects.get(beanName);
    }

    public Object getSingleton(String beanName, ObjectFactory<?> objectFactory) {
        Object singletonObject = singltonObjects.get(beanName);
        if (null == singletonObject) {
            singletonObject = objectFactory.getObject();
            singltonObjects.put(beanName, singletonObject);
            singltonObjects.put(singletonObject.getClass().getName(), singletonObject);
        }
        return singletonObject;
    }
}
