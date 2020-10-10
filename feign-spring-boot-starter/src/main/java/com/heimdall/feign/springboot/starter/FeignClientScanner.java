package com.heimdall.feign.springboot.starter;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.MergedAnnotation;

import java.util.Arrays;
import java.util.Set;

/**
 * @author crh
 * @date 2020/9/14
 */
public class FeignClientScanner extends ClassPathBeanDefinitionScanner {
    public FeignClientScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        registerFilters();
    }

    public void registerFilters() {
        // include all interfaces
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    /**
     * @param basePackages
     * @return
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No feign client is found in package '" + Arrays.toString(basePackages) + "'.");
        }

        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            MergedAnnotation<FeignClient> mergedAnnotation = ((ScannedGenericBeanDefinition) definition)
                    .getMetadata()
                    .getAnnotations()
                    .get(FeignClient.class);

            String beanClassName = definition.getBeanClassName();
            definition.setBeanClass(FeignFactoryBean.class);

            definition.getPropertyValues().add("proxyInterface", beanClassName);
            definition.getPropertyValues().add("feignClient", mergedAnnotation.synthesize());
        }

        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().hasAnnotation(FeignClient.class.getName());
    }
}