package com.heimdall.feign.core;

/**
 * @author crh
 * @date 2020-09-12
 */
public interface IFallbackFactory<T> {

    T create(Throwable throwable);

}
