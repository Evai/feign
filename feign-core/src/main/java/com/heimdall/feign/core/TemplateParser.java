package com.heimdall.feign.core;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author crh
 * @date 2020/9/14
 */
public class TemplateParser {

    private final String start;
    private final String end;

    /**
     * 模板左边符号和右边符号
     *
     * @param start
     * @param end
     */
    public TemplateParser(String start, String end) {
        char[] startChars = start.toCharArray();
        char[] endChars = end.toCharArray();
        StringBuilder startStr = new StringBuilder();
        for (char startChar : startChars) {
            startStr.append("\\").append(startChar);
        }
        StringBuilder endStr = new StringBuilder();
        for (char endChar : endChars) {
            endStr.append("\\").append(endChar);
        }
        this.start = startStr.toString();
        this.end = endStr.toString();
    }

    /**
     * 解析模板，替换变量值
     *
     * @param params
     * @return
     */
    public String parse(String template, Map<String, Object> params) {
        String content = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String variable = this.start + key + this.end;
            content = content.replaceAll(variable, Matcher.quoteReplacement(String.valueOf(entry.getValue())));
        }
        return content;
    }

    /**
     * 匹配到模板中有该变量时，重新生成
     *
     * @param template
     * @param variable
     * @return
     */
    public boolean matchVariable(String template, String variable) {
        String key = this.start + variable + this.end;
        return Pattern.compile(key).matcher(template).find();
    }

}