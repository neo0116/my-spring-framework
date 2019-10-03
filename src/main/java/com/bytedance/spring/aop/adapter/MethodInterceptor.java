package com.bytedance.spring.aop.adapter;

import com.bytedance.spring.aop.MethodInvocation;

public interface MethodInterceptor {

    Object invoke(MethodInvocation invocation) throws Throwable;

}
