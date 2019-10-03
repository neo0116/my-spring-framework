package com.bytedance.spring.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class ViewResolver {

    public static final String DEFULT_SUFFIX = ".html";

    public File file;

    public ViewResolver(String templateDirectory) {
        String file = this.getClass().getClassLoader().getResource(templateDirectory).getFile();
        this.file = new File(file);
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (null == viewName || "".equals(viewName)) {return null;}
        //加后缀
        viewName = viewName.endsWith(DEFULT_SUFFIX) ? viewName : viewName + DEFULT_SUFFIX;
        return new View((this.file.getPath() + "\\" + viewName).replaceAll("/+", "/"));
    }

}
