package com.heimdall.feign.springboot.starter;

import com.heimdall.feign.core.IFallbackFactory;
import com.heimdall.feign.core.IJsonSerializer;
import com.heimdall.feign.core.OkHttpTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

/**
 * @author crh
 * @date 2020-09-12
 */
public class FeignFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    /**
     * 代理接口
     */
    private Class<T> proxyInterface;

    private FeignClient feignClient;

    private ApplicationContext applicationContext;

    public void setFeignClient(FeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public void setProxyInterface(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        OkHttpTemplate wildHttpClient = applicationContext.getBean(OkHttpTemplate.class);
        IJsonSerializer jsonSerializer = applicationContext.getBean(IJsonSerializer.class);
        Class<?> factoryClass = feignClient.fallbackFactory();
        String beanName = this.lowerCaseFirst(factoryClass.getSimpleName());
        IFallbackFactory<?> fallbackFactory = null;
        if (applicationContext.containsBean(beanName)) {
            fallbackFactory = (IFallbackFactory<?>) applicationContext.getBean(beanName);
        }

        PropertySourcesPlaceholdersResolver resolver = new PropertySourcesPlaceholdersResolver(applicationContext.getEnvironment());
        String url = String.valueOf(resolver.resolvePlaceholders(feignClient.url()));

        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{this.proxyInterface}, new FeignProxy(wildHttpClient, jsonSerializer, fallbackFactory, url));
    }

    @Override
    public Class<T> getObjectType() {
        return this.proxyInterface;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String lowerCaseFirst(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }
        char[] cs = str.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

}
