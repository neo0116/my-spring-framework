package com.bytedance.spring.webmvc.servlet;

import com.bytedance.spring.context.annotation.BDRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {


    boolean supports(Object handler) {
        return handler instanceof HandlerMapping;
    }

    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMapping handlerMapping = (HandlerMapping) handler;
        Object controller = handlerMapping.getController();
        Method method = handlerMapping.getMethod();
        //参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //实参
        Object[] objects = new Object[parameterTypes.length];
        //参数对应位置
        Map<String, Integer> sortMap = new HashMap<>();
        //排序
        sortParams(method, parameterTypes, sortMap);
        //request参数
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            if (sortMap.containsKey(key)) {
                Integer index = sortMap.get(key);
                objects[index] = convert(parameterTypes[index], entry.getValue()[0]);
            }
        }
        String requestTypeName = HttpServletRequest.class.getTypeName();
        if (sortMap.containsKey(HttpServletRequest.class.getTypeName())) {
            Integer index = sortMap.get(requestTypeName);
            objects[index] = request;
        }
        String responseTypeName = HttpServletResponse.class.getTypeName();
        if (sortMap.containsKey(HttpServletResponse.class.getTypeName())) {
            Integer index = sortMap.get(responseTypeName);
            objects[index] = response;
        }

        Object invoke = method.invoke(controller, objects);
        if (null == invoke || invoke instanceof Void) {
            return null;
        }
        if (invoke instanceof ModelAndView) {
            return (ModelAndView) invoke;
        }
        return null;
    }


    private Object convert(Class<?> parameterType, String param) {
        if (parameterType == String.class) {
            return param;
        } else if (parameterType == Integer.class) {
            return Integer.parseInt(param);
        } else if (parameterType == Double.class) {
            return Double.parseDouble(param);
        } else {
            return param;
        }
    }

    private void sortParams(Method method, Class<?>[] parameterTypes, Map<String, Integer> sortMap) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] annotation = annotations[i];
            for (int j = 0; j < annotation.length; j++) {
                Annotation an = annotation[j];
                if (an instanceof BDRequestParam) {
                    String value = ((BDRequestParam) an).value();
                    if (!"".equals(value)) {
                        sortMap.put(value, i);
                    }
                }
            }
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                sortMap.put(parameterType.getTypeName(), i);
            }
            if (parameterType == HttpServletResponse.class) {
                sortMap.put(parameterType.getTypeName(), i);
            }
        }
    }
}
