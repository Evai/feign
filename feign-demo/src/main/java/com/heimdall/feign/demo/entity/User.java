package com.heimdall.feign.demo.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author crh
 * @date 2020-09-12
 */
@Data
public class User {

    @NotNull
    private Integer id;

    @NotBlank
    private String name;

}
