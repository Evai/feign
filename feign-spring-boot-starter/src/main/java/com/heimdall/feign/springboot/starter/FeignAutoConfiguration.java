package com.heimdall.feign.springboot.starter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.heimdall.feign.core.IJsonSerializer;
import com.heimdall.feign.core.JacksonSerializer;
import com.heimdall.feign.core.OkHttpRetryInterceptor;
import com.heimdall.feign.core.OkHttpTemplate;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author crh
 * @date 2020/10/10
 */
@Configuration
@ConditionalOnClass
@EnableConfigurationProperties(FeignConfigurationProperties.class)
public class FeignAutoConfiguration {

    private final FeignConfigurationProperties feignConfigurationProperties;

    public FeignAutoConfiguration(FeignConfigurationProperties feignConfigurationProperties) {
        this.feignConfigurationProperties = feignConfigurationProperties;
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(feignConfigurationProperties.getMaxIdleConnections(), feignConfigurationProperties.getKeepAliveDuration(), TimeUnit.MINUTES);
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpTemplate okHttpClient(ConnectionPool connectionPool) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (feignConfigurationProperties.isLogging()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        if (feignConfigurationProperties.isRetryOnConnectionFailure() && feignConfigurationProperties.getMaxRetry() > 0) {
            builder.addInterceptor(new OkHttpRetryInterceptor(feignConfigurationProperties.getMaxRetry(), feignConfigurationProperties.getRetryInterval()));
        }
        OkHttpClient okHttpClient = builder
                .connectionPool(connectionPool)
                .connectTimeout(feignConfigurationProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(feignConfigurationProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(feignConfigurationProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .build();

        return new OkHttpTemplate(okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public IJsonSerializer jsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略值为NULL的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略json字符串中存在，但Java对象中不存在对应属性的错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new JacksonSerializer(objectMapper);
    }

}