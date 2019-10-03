package com.bytedance.spring.aop.adapter;

import com.bytedance.spring.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AspectAfterThrowingInterceptor extends AbstractMethodInterceptor implements MethodInterceptor {

    private String throwName;

    public AspectAfterThrowingInterceptor(Object aspectInstance, Method aspectMethod) {
        super(aspectInstance, aspectMethod);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            this.joinpoint = mi;
            return mi.proceed();
        } catch (Throwable throwable) {
            invokeAdviceMethod(this.joinpoint, null, throwable.getCause());
            throw throwable;
        }
    }

    public AspectAfterThrowingInterceptor setThrowName(String throwName) {
        this.throwName = throwName;
        return this;
    }
}
