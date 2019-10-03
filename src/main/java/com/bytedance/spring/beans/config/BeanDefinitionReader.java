package com.bytedance.spring.beans.config;

import com.bytedance.spring.beans.support.BeanDefinitionRegistry;
import com.bytedance.spring.beans.support.DefaultListableBeanFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BeanDefinitionReader {

    private static final String LOCATION_PREFIX = "classpath:";

    private static final String SCANNER_PACKAGE = "scanner.package";

    //class文件地址
    private List<String> classLocations = new ArrayList<>(16);

    private Properties properties = new Properties();

    public BeanDefinitionRegistry registry;

    public BeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void doResourceLoader(String[] configLocations) {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace(LOCATION_PREFIX, ""));

            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        doScanner(properties.getProperty(SCANNER_PACKAGE));
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File file = new File(url.getFile());
        File[] files = file.listFiles();
        for (File fl : files) {
            if (fl.isDirectory()) {
                doScanner(packageName + "." + fl.getName());
            } else {
                if (!fl.getName().endsWith(".class")) { continue; }
                String classPackageName = packageName + "." + fl.getName().replace(".class", "");
                classLocations.add(classPackageName);
            }
        }
    }

    public void loadBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = ((DefaultListableBeanFactory) registry).beanDefinitions;
        for (String classLocation : classLocations) {
            try {
                Class<?> clazz = Class.forName(classLocation);
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) { continue; }
                BeanDefinition bd = createBeanDefinition(classLocation, clazz.getSimpleName());
                beanDefinitions.add(bd);
                //接口注入实现类
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    BeanDefinition ibd = createBeanDefinition(classLocation, anInterface.getSimpleName());
                    beanDefinitions.add(ibd);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private BeanDefinition createBeanDefinition(String classLocation, String simpleName) {
        BeanDefinition bd = new BeanDefinition();
        bd.setBeanClassName(classLocation);
        bd.setFactoryBeanName(toLowerCase(simpleName));
        return bd;
    }

    private String toLowerCase(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public void registBeanDefinitions() {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.registry;
        List<BeanDefinition> beanDefinitions = beanFactory.beanDefinitions;
        Map<String, BeanDefinition> beanDefinitionMap = beanFactory.beanDefinitionMap;

        for (BeanDefinition beanDefinition : beanDefinitions) {
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }
}
