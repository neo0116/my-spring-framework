package com.bytedance.spring.context;

//监听所有它的实现类，回调setApplicationContext方法 注入ApplicationContext。  （观察者模式）
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext);

}
