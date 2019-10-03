package com.bytedance.spring.aop.adapter;

import com.bytedance.spring.aop.MethodInvocation;

import java.lang.reflect.Method;

public class AspectAfterReturningInterceptor extends AbstractMethodInterceptor implements MethodInterceptor {

    public AspectAfterReturningInterceptor(Object aspectInstance, Method aspectMethod) {
        super(aspectInstance, aspectMethod);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinpoint = mi;
        Object result = mi.proceed();
        afterReturning(result, mi.getMethod(), mi.getArguments(), mi.getThis());
        return result;
    }

    private void afterReturning(Object result, Method method, Object[] arguments, Object aThis) throws Throwable {
        invokeAdviceMethod(this.joinpoint, result, null);
    }
}
