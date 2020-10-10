package com.heimdall.feign.springboot.starter;

import com.heimdall.feign.core.*;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author crh
 * @date 2020-09-12
 */
public class FeignProxy implements InvocationHandler {

    private final OkHttpTemplate okHttpTemplate;
    private final IJsonSerializer jsonSerializer;
    private final IFallbackFactory<?> fallbackFactory;
    private  Class<?> fallbackFactoryType;
    private final String url;

    private SoftReference<Map<Method, Method>> referenceMethod;
    private SoftReference<Map<Class<?>, List<Field>>> referenceField;
    private final TemplateParser templateParser;

    public FeignProxy(OkHttpTemplate okHttpTemplate, IJsonSerializer jsonSerializer, IFallbackFactory<?> fallbackFactory, String url) {
        this.okHttpTemplate = okHttpTemplate;
        this.jsonSerializer = jsonSerializer;
        this.fallbackFactory = fallbackFactory;
        this.url = url;
        if (fallbackFactory != null) {
            this.fallbackFactoryType = (Class<?>) ((ParameterizedType) fallbackFactory.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        }
        this.referenceMethod = new SoftReference<>(newMap());
        this.referenceField = new SoftReference<>(newMap());
        this.templateParser = new TemplateParser("{", "}");
    }

    private <K, V> Map<K, V> newMap() {
        return new ConcurrentHashMap<>(8);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        FeignClient feignClient = method.getDeclaringClass().getDeclaredAnnotation(FeignClient.class);
        RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);

        String url = this.url;

        String uri = this.wrapperPathVariableUri(method, args, requestMapping.value());

        url += uri;

        String[] headers1 = feignClient.headers();
        String[] headers2 = requestMapping.headers();

        String[] mergeHeaders = this.mergeArray(headers1, headers2);

        Map<String, String> headerMap = this.wrapperRequestHeader(mergeHeaders, method, args);

        Headers headers = Headers.of(headerMap);

        Request request;
        if (requestMapping.methodType() == RequestMapping.MethodType.GET) {
            Map<String, Object> paramMap = this.wrapperRequestParam(method, args);
            request = this.get(url, headers, paramMap);
        } else {
            String json = this.wrapperRequestBody(method, args);
            if (json != null) {
                request = this.postJson(url, headers, json);
            } else {
                Map<String, Object> paramMap = this.wrapperRequestParam(method, args);
                request = this.post(url, headers, paramMap);
            }
        }

        return this.request(request, method, args);
    }

    private String wrapperRequestBody(Method method, Object[] args) throws Exception {
        String json = null;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RequestBody requestBody = parameter.getDeclaredAnnotation(RequestBody.class);
            if (requestBody != null) {
                json = this.jsonSerializer.serializer(args[i]);
                break;
            }
        }
        return json;
    }

    private String wrapperPathVariableUri(Method method, Object[] args, String value) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> paramMap = new HashMap<>(4);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            PathVariable pathVariable = parameter.getDeclaredAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String parameterName = pathVariable.value().trim().isEmpty() ? parameter.getName() : pathVariable.value();
                paramMap.put(parameterName, args[i]);
            }
        }
        return templateParser.parse(value, paramMap);
    }

    private String[] mergeArray(String[] arr1, String[] arr2) {
        String[] mergeArray = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, mergeArray, arr1.length, arr2.length);
        return mergeArray;
    }

    private Request postJson(String url, Headers headers, String json) {
        return this.okHttpTemplate.newJsonBuilder(url, json)
                .headers(headers)
                .build();
    }

    private Request get(String url, Headers headers, Map<String, Object> paramMap) {
        return this.okHttpTemplate.newGetBuilder(url, paramMap)
                .headers(headers)
                .build();
    }

    private Request post(String url, Headers headers, Map<String, Object> paramMap) {
        return this.okHttpTemplate.newPostFormBuilder(url, paramMap)
                .headers(headers)
                .build();
    }

    private Map<String, Object> wrapperRequestParam(Method method, Object[] args) {
        Map<String, Object> paramMap = new HashMap<>(8);
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Object obj = args[i];
            if (obj != null) {
                if (ReflectUtils.isPrimitiveOrString(obj)) {
                    Parameter parameter = parameters[i];
                    String name = parameter.getName();
                    Param param = parameter.getDeclaredAnnotation(Param.class);
                    if (param != null) {
                        name = param.value();
                    }
                    paramMap.put(name, obj);
                } else {
                    this.wrapperObjectValue(paramMap, obj);
                }

            }
        }
        return paramMap;
    }

    private void wrapperObjectValue(Map<String, Object> paramMap, Object obj) {
        Map<Class<?>, List<Field>> listMap = referenceField.get();
        if (listMap == null) {
            listMap = newMap();
            referenceField = new SoftReference<>(listMap);
        }
        Class<?> valueClass = obj.getClass();
        List<Field> fields = listMap.get(valueClass);
        if (fields == null) {
            fields = ReflectUtils.getAllFields(valueClass);
            listMap.put(valueClass, fields);
            fields.forEach(field -> field.setAccessible(true));
        }
        for (Field field : fields) {
            Object value = ReflectionUtils.getField(field, obj);
            paramMap.put(field.getName(), value);
        }
    }

    private Map<String, String> wrapperRequestHeader(String[] headers, Method method, Object[] args) {
        Map<String, String> headerMap = new HashMap<>(headers.length);

        Parameter[] parameters = method.getParameters();

        for (String header : headers) {
            String[] split = header.split(":");
            headerMap.put(split[0], split[1]);
        }

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Header header = parameter.getDeclaredAnnotation(Header.class);
            if (header != null) {
                Object value = args[i];
                if (value != null) {
                    headerMap.put(parameter.getName(), value.toString());
                }
            }
        }

        return headerMap;
    }

    private Object request(Request request, Method method, Object[] args) throws Exception {
        try (Response response = this.okHttpTemplate.doRequest(request)) {

            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }

            String string = body.string();

            if (!response.isSuccessful()) {
                return exceptionHandler(method, args, new RequestException(string));
            }

            Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();

            if (genericReturnType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                Class<?>[] genericClasses = new Class[types.length];
                for (int i = 0; i < types.length; i++) {
                    genericClasses[i] = (Class<?>) types[i];
                }
                return jsonSerializer.deserializer(string, returnType, genericClasses);
            }
            return jsonSerializer.deserializer(string, returnType);
        } catch (IOException e) {
            return this.exceptionHandler(method, args, e);
        }
    }

    private Object exceptionHandler(Method method, Object[] args, Exception e) throws RuntimeException {
        if (this.fallbackFactory == null) {
            throw new RuntimeException(e);
        }
        if (!method.getDeclaringClass().equals(this.fallbackFactoryType)) {
            return this.fallbackFactory.create(e);
        }
        Object feign = this.fallbackFactoryType.cast(this.fallbackFactory.create(e));

        Map<Method, Method> methodMap = referenceMethod.get();
        if (methodMap == null) {
            methodMap = newMap();
            referenceMethod = new SoftReference<>(newMap());
        }
        try {
            Method feignMethod = methodMap.get(method);
            if (feignMethod == null) {
                feignMethod = feign.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                feignMethod.setAccessible(true);
                methodMap.put(method, method);
            }
            return feignMethod.invoke(feign, args);
        } catch (InvocationTargetException e1) {
            throw new RequestException(e1.getTargetException());
        } catch (Exception e2) {
            throw new RequestException(e2);
        }

    }

}
