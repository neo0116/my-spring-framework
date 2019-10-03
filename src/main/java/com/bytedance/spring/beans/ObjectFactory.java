package com.bytedance.spring.beans;

@FunctionalInterface
public interface ObjectFactory<T> {

    T getObject();

}
