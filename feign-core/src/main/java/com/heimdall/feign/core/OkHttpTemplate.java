package com.heimdall.feign.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.RequestBody;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author crh
 * @date 2020-09-12
 */
@AllArgsConstructor
public class OkHttpTemplate implements IHttpClient {

    @Getter
    private final OkHttpClient okHttpClient;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient getClient() {
        return okHttpClient;
    }

    public Request.Builder newGetBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .get();
    }

    public Request.Builder newGetBuilder(String url, Map<String, Object> params) {
        HttpUrl httpUrl = checkAndParseUrl(url);
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();

        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (Objects.nonNull(param.getValue())) {
                    httpBuilder.addQueryParameter(param.getKey(), String.valueOf(param.getValue()));
                }
            }
        }
        return new Request.Builder()
                .url(httpBuilder.build())
                .get();
    }

    public Request.Builder newPostFormBuilder(String url, Map<String, Object> params) {
        checkAndParseUrl(url);
        FormBody.Builder formBuilder = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (Objects.nonNull(param.getValue())) {
                    formBuilder.add(param.getKey(), String.valueOf(param.getValue()));
                }
            }
        }

        return new Request.Builder()
                .url(url)
                .post(formBuilder.build());
    }

    public Request.Builder newJsonBuilder(String url, String json) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        return new Request.Builder()
                .url(url)
                .post(requestBody);
    }

    private HttpUrl checkAndParseUrl(String url) {
        if (url == null) {
            throw new RuntimeException("url must be not null!");
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new RuntimeException("url parse error!");
        }
        return httpUrl;
    }

    public Response doRequest(Request request) throws IOException {
        return doRequest(request, null);
    }

    public void doRequestAsync(Request request, Callback callback) throws IOException {
        doRequest(request, callback);
    }

    private Response doRequest(Request request, Callback callback) throws IOException {
        Call call = okHttpClient.newCall(request);
        if (Objects.nonNull(callback)) {
            call.enqueue(callback);
            return null;
        }
        return call.execute();
    }

}
