package com.bytedance.myservice.aspect;

import com.bytedance.spring.aop.Joinpoint;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LogAspect {

    public void before(Joinpoint joinpoint) {
        Object[] arguments = joinpoint.getArguments();
        System.out.println(Arrays.toString(arguments));

        Method method = joinpoint.getMethod();
        System.out.println(method.getName());

        System.out.println(joinpoint.getThis());

        System.out.println("====>before");

    }

    public void after(Joinpoint joinpoint, Object result) {
        Object[] arguments = joinpoint.getArguments();
        System.out.println(Arrays.toString(arguments));

        Method method = joinpoint.getMethod();
        System.out.println(method.getName());

        System.out.println(joinpoint.getThis());
        System.out.println(result);

        System.out.println("====>after");
    }

    public void afterThrowing(Joinpoint joinpoint, Throwable ex) {
        Object[] arguments = joinpoint.getArguments();
        System.out.println(Arrays.toString(arguments));

        Method method = joinpoint.getMethod();
        System.out.println(method.getName());

        System.out.println(joinpoint.getThis());
        System.out.println(ex.getCause().getMessage());

        System.out.println("====>afterThrowing");
    }
}
