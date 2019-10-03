package com.bytedance.spring.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {

    public static final String ContentType = "text/html;charset=utf-8";

    public File file;

    public Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);

    public View(String filePath) {
        this.file = new File(filePath);
    }

    public String getContentType() {
        return ContentType;
    }

    /**
     * Render the view given the specified model.
     * <p>The first step will be preparing the request: In the JSP case, this would mean
     * setting model objects as request attributes. The second step will be the actual
     * rendering of the view, for example including the JSP via a RequestDispatcher.
     * @param model a Map with name Strings as keys and corresponding model
     * objects as values (Map can also be {@code null} in case of empty model)
     * @param request current HTTP request
     * @param response he HTTP response we are building
     * @throws Exception if rendering failed
     */
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        //“只读”模式读取模板
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        StringBuilder sb = new StringBuilder();
        String line = null;
        while (null != (line = randomAccessFile.readLine())) {
            line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
            //配置当前行
            Matcher matcher = pattern.matcher(line);
            //当前行是否有配置
            while (matcher.find()) {
                //group()匹配正则表达式整个的结果（￥{name}）， group(1)匹配正则表达式中第一个子表达式。。。
                String paramkey = matcher.group();
                paramkey = paramkey.replaceAll("￥\\{|\\}","");
                //赋值
                Object o = model.get(paramkey);
                if (null == o) {continue;}
                //替换第一个匹配到的
                line = matcher.replaceFirst(dealStringForRegExp(o.toString()));
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }
//        response.setContentType(ContentType);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(sb.toString());
    }

    //处理特殊字符
    public static String dealStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }

}
