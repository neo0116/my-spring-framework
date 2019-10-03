package com.bytedance.spring.aop;

import com.bytedance.spring.aop.adapter.AspectAfterReturningInterceptor;
import com.bytedance.spring.aop.adapter.AspectAfterThrowingInterceptor;
import com.bytedance.spring.aop.adapter.AspectBeforeInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AdvisedSupport {

    private Object target;

    private Class<?> targetClass;
    //配置
    public AopConfig config;

    public Pattern pattern;
    //spring中也是保存methodCache
    public Map<Method, List<Object>> methodCache;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> chain = methodCache.get(method);
        if (null == chain) {
            method = targetClass.getMethod(method.getName(), method.getParameterTypes());
            chain = methodCache.get(method);
            methodCache.put(method, chain);
        }
        return chain;
    }


    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        initPattern();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    private void initPattern() {
        //类匹配
        //public .* com.bytedance.myservice.service..*ServiceImpl..*(.*)
        String pointCut = this.config.getPointCut();
        String pointCutReplace = pointCut
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        String pointCutRegex = pointCutReplace.substring(0, pointCutReplace.lastIndexOf("\\(") - 4);
        String regex = "class " + pointCutRegex.substring(pointCutRegex.lastIndexOf(" ") + 1);
        pattern = Pattern.compile(regex);

        //创建method的通知链
        try {
            String aspectClass = this.config.getAspectClass();
            Class<?> aspectClazz = Class.forName(aspectClass);
            Map<String, Method> aspectMap = new HashMap<>();
            Method[] aspectClassMethods = aspectClazz.getDeclaredMethods();
            for (Method method : aspectClassMethods) {
                aspectMap.put(method.getName(), method);
            }

            methodCache = new HashMap<>();
            //方法匹配，每个方法对应一个执行器链
            Pattern methodPattern = Pattern.compile(pointCutReplace);
            Method[] methods = this.getTargetClass().getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.toString();
                if (methodName.contains("throws")) {
                    methodName = methodName.substring(0, methodName.lastIndexOf("throws"));
                }
                boolean matches = methodPattern.matcher(methodName).matches();
                //如果匹配
                if (matches) {
                    //创建拦截链
                    List<Object> advised = new LinkedList<>();
                    Object aspectInstance = aspectClazz.newInstance();
                    //添加前置通知
                    String aspectBefore = this.config.getAspectBefore();
                    if (null != aspectBefore && !"".equals(aspectBefore)) {
                        advised.add(new AspectBeforeInterceptor(aspectInstance, aspectMap.get(aspectBefore)));
                    }
                    //添加后置通知
                    String aspectAfter = this.config.getAspectAfter();
                    if (null != aspectAfter && !"".equals(aspectAfter)) {
                        advised.add(new AspectAfterReturningInterceptor(aspectInstance, aspectMap.get(aspectAfter)));
                    }
                    //添加异常通知
                    String aspectAfterThrowing = this.config.getAspectAfterThrowing();
                    if (null != aspectAfterThrowing && !"".equals(aspectAfterThrowing)) {
                        advised.add(new AspectAfterThrowingInterceptor(aspectInstance, aspectMap.get(aspectAfterThrowing)).setThrowName(this.config.getThrowName()));
                    }
                    methodCache.put(method, advised);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean pointCutMatch() {
        return pattern.matcher(this.targetClass.toString()).matches();
    }
}
