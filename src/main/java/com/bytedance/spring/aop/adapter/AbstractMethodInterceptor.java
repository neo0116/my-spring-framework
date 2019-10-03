package com.bytedance.spring.aop.adapter;

import com.bytedance.spring.aop.Joinpoint;

import java.lang.reflect.Method;

public abstract class AbstractMethodInterceptor implements MethodInterceptor{

    private Object aspectInstance;

    private Method aspectMethod;

    public Joinpoint joinpoint;

    public AbstractMethodInterceptor(Object aspectInstance, Method aspectMethod) {
        this.aspectInstance = aspectInstance;
        this.aspectMethod = aspectMethod;
    }

    protected Object invokeAdviceMethod(
            Joinpoint joinpoint,  Object returnValue, Throwable ex)
            throws Throwable {
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(this.aspectInstance);
        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == Joinpoint.class) {
                    args[i] = joinpoint;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = ex;
                }
            }
            return this.aspectMethod.invoke(this.aspectInstance, args);
        }
    }

    public Joinpoint getJoinpoint() {
        return joinpoint;
    }

    public void setJoinpoint(Joinpoint joinpoint) {
        this.joinpoint = joinpoint;
    }
}
