package com.bytedance.spring.aop.adapter;

import com.bytedance.spring.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AspectBeforeInterceptor extends AbstractMethodInterceptor implements MethodInterceptor {

    public AspectBeforeInterceptor(Object aspectInstance, Method aspectMethod) {
        super(aspectInstance, aspectMethod);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinpoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinpoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
