package com.heimdall.feign.core;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * @author crh
 * @date 2020-09-12
 */
@Slf4j
public class OkHttpRetryInterceptor implements Interceptor {

    /**
     * 最大重试次数
     */
    private int maxRetry;

    /**
     * 重试的间隔, 毫秒
     */
    private long retryInterval;

    public OkHttpRetryInterceptor(int maxRetry, long retryInterval) {
        this.maxRetry = maxRetry;
        this.retryInterval = retryInterval;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = this.retry(chain);
        int retryNum = 0;
        while (!response.isSuccessful() && retryNum < maxRetry) {
            response.close();
            retryNum++;
            try {
                log.info("retry wait for: {}ms, retryNum: {}", retryInterval, retryNum);
                Thread.sleep(this.retryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
            response = this.retry(chain);
        }
        return response;
    }

    private Response retry(Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }
}