package com.bytedance.spring.aop;

public interface MethodInvocation extends Joinpoint{

    Object proceed() throws Throwable;

}
