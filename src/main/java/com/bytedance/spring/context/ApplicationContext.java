package com.bytedance.spring.context;

import com.bytedance.spring.beans.BeanFactory;
import com.bytedance.spring.beans.config.BeanDefinition;
import com.bytedance.spring.beans.config.BeanDefinitionReader;
import com.bytedance.spring.beans.support.DefaultListableBeanFactory;
import com.bytedance.spring.context.support.AbstractApplicationContext;

import java.util.Map;

public class ApplicationContext extends AbstractApplicationContext implements BeanFactory {

    private String[] configLocations;

    public DefaultListableBeanFactory defaultListableBeanFactory;

    public BeanDefinitionReader reader;

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        refresh();
    }

    @Override
    public void refresh() {
        //1.定位配置文件
        BeanDefinitionReader reader = resourceLoader(this.configLocations);
        //2.读取配置文件
        reader.loadBeanDefinitions(reader);
        //3.加载配置文件到BeanDefinition集合中
        reader.registBeanDefinitions();
        //4.非懒加载bean注入IOC中
        doNotLazyBeanAutowire(reader);
    }

    private void doNotLazyBeanAutowire(BeanDefinitionReader reader) {
        Map<String, BeanDefinition> beanDefinitionMap = ((DefaultListableBeanFactory) reader.registry).beanDefinitionMap;
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            boolean lazyInit = beanDefinition.isLazyInit();
            if (lazyInit) {
                try {
                    this.getBean(entry.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private BeanDefinitionReader resourceLoader(String[] configLocations) {
        this.defaultListableBeanFactory = new DefaultListableBeanFactory();
        this.reader = new BeanDefinitionReader(defaultListableBeanFactory);
        this.reader.doResourceLoader(configLocations);
        return reader;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return defaultListableBeanFactory.getBean(beanName);
    }

    public int getBeanDefinitionCount() {
        return defaultListableBeanFactory.beanDefinitionMap.size();
    }


    public String[] getBeanDefinitionNames() {
        return defaultListableBeanFactory.beanDefinitionMap.keySet().toArray(new String[defaultListableBeanFactory.beanDefinitionMap.size()]);
    }

    public BeanDefinitionReader getReader() {
        return reader;
    }
}


