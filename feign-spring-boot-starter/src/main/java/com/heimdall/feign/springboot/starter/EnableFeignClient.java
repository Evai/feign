package com.heimdall.feign.springboot.starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author crh
 * @date 2020-09-12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignRegister.class)
public @interface EnableFeignClient {

    /**
     * Base packages to scan for annotated components.
     * @return
     */
    String[] basePackages();

}
