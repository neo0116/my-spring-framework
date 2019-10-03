package com.bytedance.spring.aop;

import java.lang.reflect.Method;

public interface Joinpoint {

	Object getThis();

	Method getMethod();

	Object[] getArguments();

}