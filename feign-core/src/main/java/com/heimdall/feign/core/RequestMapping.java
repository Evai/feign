package com.heimdall.feign.core;

import java.lang.annotation.*;

/**
 * @author crh
 * @date 2020-09-12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    /**
     * uri
     */
    String value() default "";

    MethodType methodType() default MethodType.GET;

    String[] headers() default {};

    enum MethodType {
        /**
         * request method type
         */
        GET,
        POST,
        ;
    }
}
