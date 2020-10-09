package com.heimdall.feign.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

/**
 * @author crh
 * @date 2020-09-12
 */
@AllArgsConstructor
public class JacksonSerializer implements IJsonSerializer {

    private final ObjectMapper objectMapper;

    @Override
    public String serializer(Object obj) throws Exception {
        if (obj == null) {
            return "{}";
        }
        return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
    }

    @Override
    public <T> T deserializer(String json, Class<?> clz, Class<?>... genericElements) throws Exception {
        if (json == null || clz == null) {
            return null;
        }
        JavaType javaType = this.getJavaType(clz, genericElements);
        return this.deserializer(json, javaType);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializer(String str, JavaType javaType) throws JsonProcessingException {
        if (str == null || javaType == null) {
            return null;
        }
        return (T) (javaType.getRawClass().equals(String.class) ? str : objectMapper.readValue(str, javaType));
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param clz
     * @param genericElements
     * @return
     */
    public JavaType getJavaType(Class<?> clz, Class<?>... genericElements) {
        return objectMapper
                .getTypeFactory()
                .constructParametricType(clz, genericElements);
    }

}
