package com.bytedance.spring.aop;

import java.lang.reflect.Method;

public class CglibMethodInvocation implements MethodInvocation {
    @Override
    public Object proceed() throws Throwable {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }
}
