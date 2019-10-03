package com.bytedance.spring.webmvc.servlet;

import com.bytedance.spring.context.ApplicationContext;
import com.bytedance.spring.context.annotation.BDController;
import com.bytedance.spring.context.annotation.BDRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    public final static String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    /**
     * List of HandlerMappings used by this servlet.
     */
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        String configInitParameter = config.getInitParameter(CONTEXT_CONFIG_LOCATION);
        ApplicationContext context = new ApplicationContext(configInitParameter);
        initStrategies(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping handler = getHandler(req);
        if (null == handler) {
            processDispatchResult(req, resp, new ModelAndView("404"));
            return;
        }

        HandlerAdapter ha = getHanlderAdapter(handler);

        ModelAndView mv = ha.handle(req, resp, handler);

        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView mv) throws Exception {
        //ModelAndView解析成不同的模板，jsp，html，freemark，thymeleaf
        String viewName = mv.getViewName();
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, null);
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private HandlerAdapter getHanlderAdapter(HandlerMapping handlerMapping) {
        HandlerAdapter handlerAdapter = this.handlerAdapters.get(handlerMapping);
        if (handlerAdapter.supports(handlerMapping)) {
            return handlerAdapter;
        }
        return null;
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            Pattern pattern = handlerMapping.getPattern();
            String uri = req.getRequestURI();
            String contextPath = req.getContextPath();
            uri = uri.replace(contextPath, "").replace("/+", "/");
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                return handlerMapping;
            }
        }
        return null;
    }

    private void initStrategies(ApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //国际化
        initLocaleResolver(context);
        //初始化主题解析器
        initThemeResolver(context);
        //必须实现
        initHandlerMappings(context);
        //初始化参数适配器
        //必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图解析器
        //必须实现
        initViewResolvers(context);
        //初始化flashMap，解决重定向可以带参数
        initFlashMapManager(context);
    }

    private void initFlashMapManager(ApplicationContext context) {
    }

    private void initViewResolvers(ApplicationContext context) {
        String templateRootDirectory = context.getReader().getProperties().getProperty("templateRootDirectory");
        String template = this.getClass().getClassLoader().getResource(templateRootDirectory).getFile();
        File file = new File(template);
        String[] templateRoots = file.list();
        //加载多个模板
        for (String templateRoot : templateRoots) {
            //只选一个模板
            this.viewResolvers.add(new ViewResolver(templateRootDirectory));
            return;
        }
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object bean = context.getBean(beanName);
                Class<?> clazz = bean.getClass();
                //是否有BDController注解
                if (!clazz.isAnnotationPresent(BDController.class)) { continue; }
                String baseUrl = "";
                //是否有BDRequestMapping注解
                if (clazz.isAnnotationPresent(BDRequestMapping.class)) {
                    BDRequestMapping requestMapping = clazz.getAnnotation(BDRequestMapping.class);
                    String value = requestMapping.value();
                    if (!"".equals(value)) {
                        baseUrl += "/" + value;
                    }
                }

                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    String url = "";
                    if (!method.isAnnotationPresent(BDRequestMapping.class)) { continue; }
                    BDRequestMapping requestMapping = method.getAnnotation(BDRequestMapping.class);
                    String value = requestMapping.value();
                    if (!"".equals(value)) {
                        url = baseUrl + "/" + value;
                    }
                    Pattern pattern = Pattern.compile(url.replaceAll("/+", "/"));
                    handlerMappings.add(new HandlerMapping(bean, method, pattern));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initThemeResolver(ApplicationContext context) {

    }

    private void initLocaleResolver(ApplicationContext context) {
    }

    private void initMultipartResolver(ApplicationContext context) {
    }



}
