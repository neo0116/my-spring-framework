package com.bytedance.spring.beans.support;

import com.bytedance.spring.aop.*;
import com.bytedance.spring.beans.BeanWrapper;
import com.bytedance.spring.beans.config.BeanDefinition;
import com.bytedance.spring.beans.config.BeanDefinitionReader;
import com.bytedance.spring.context.annotation.BDAutowired;
import com.bytedance.spring.context.annotation.BDController;
import com.bytedance.spring.context.annotation.BDService;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory{

    /** Cache of unfinished FactoryBean instances: FactoryBean name to BeanWrapper. */
    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Class<?>> relevanceClass = new ConcurrentHashMap<>();

    private BeanDefinitionReader reader;

    @Override
    public Object createBean(String beanName, DefaultListableBeanFactory defaultListableBeanFactory) {
        return doCreateBean(beanName, defaultListableBeanFactory);
    }

    private Object doCreateBean(String beanName, DefaultListableBeanFactory defaultListableBeanFactory) {
        BeanWrapper beanWrapper = null;
        beanWrapper = createInstance(beanName, defaultListableBeanFactory.beanDefinitionMap.get(beanName));
        //DI
        populateBean(beanName, new BeanDefinition(), beanWrapper);
        Object bean = beanWrapper.getWrappedInstance();
        Object exposedObject = bean;
        //AOP
        exposedObject = initializeBean(beanName, exposedObject, defaultListableBeanFactory.beanDefinitionMap.get(beanName));
        return exposedObject;
    }

    private BeanWrapper createInstance(String beanName, BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        Object wrappedInstance = null;
        try {
            Object instance = singltonObjects.get(beanClassName);
            if (null == instance) {
                Class<?> clazz = Class.forName(beanClassName);
                wrappedInstance = clazz.newInstance();

                AdvisedSupport advised = initializeAopConfig();
                advised.setTarget(wrappedInstance);
                advised.setTargetClass(clazz);
                //是否匹配切入点表达式？
                if(advised.pointCutMatch()) {
                    wrappedInstance = createProxy(advised).getProxy();
                }

            } else {
                wrappedInstance = instance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanWrapper beanWrapper = new BeanWrapper(wrappedInstance);
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        return beanWrapper;
    }

    private AopProxy createProxy(AdvisedSupport advised) {
        Class<?>[] interfaces = advised.getTargetClass().getInterfaces();
        if (interfaces.length != 0) {
            return new JdkDynamicAopProxy(advised);
        }
        return new CglibAopProxy(advised);
    }

    private AdvisedSupport initializeAopConfig() {
        AopConfig aopConfig = new AopConfig();
        aopConfig.setPointCut(this.reader.getProperties().getProperty("pointCut"));
        aopConfig.setAspectClass(this.reader.getProperties().getProperty("aspectClass"));
        aopConfig.setAspectBefore(this.reader.getProperties().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(this.reader.getProperties().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrowing(this.reader.getProperties().getProperty("aspectAfterThrowing"));
        aopConfig.setThrowName(this.reader.getProperties().getProperty("ThrowName"));
        return new AdvisedSupport(aopConfig);
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Class<?> clazz = beanWrapper.getWrappedClass();
        Object instance = beanWrapper.getWrappedInstance();
        if (!clazz.isAnnotationPresent(BDController.class) && !clazz.isAnnotationPresent(BDService.class)){return;}

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(BDAutowired.class)) {
                BDAutowired autowired = field.getAnnotation(BDAutowired.class);
                String value = autowired.value();
                if ("".equals(value)) {
                    value = toLowerCase(field.getType().getSimpleName());
                }

                field.setAccessible(true);
                try {
                    BeanWrapper wrapper = this.factoryBeanInstanceCache.get(value);
                    Object wrappedInstance = null;
                    if (null == wrapper) {
                        wrappedInstance = getBean(value);
                    } else {
                        wrappedInstance = wrapper.getWrappedInstance();
                    }
                    field.set(instance, wrappedInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private Object initializeBean(String beanName, Object exposedObject, BeanDefinition beanDefinition) {



        return exposedObject;
    }

    private String toLowerCase(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public BeanDefinitionReader getReader() {
        return reader;
    }

    public void setReader(BeanDefinitionReader reader) {
        this.reader = reader;
    }
}
