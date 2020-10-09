package com.heimdall.feign.core;

import java.lang.annotation.*;

/**
 * @author crh
 * @date 2020-09-12
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

    Type value() default Type.JSON;

    enum Type {
        /**
         * body type
         */
        JSON,
        XML,
        ;
    }
}
