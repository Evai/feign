package com.heimdall.feign.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author crh
 * @date 2020-09-13
 */
public class ReflectUtils {

    /**
     * 判断一个对象是否是基本类型或基本类型的包装类型
     *
     * @param obj
     * @return
     */
    public static boolean isPrimitive(Object obj) {
        try {
            return ((Class<?>) obj
                    .getClass()
                    .getField("TYPE")
                    .get(null))
                    .isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean isPrimitiveOrString(Object obj) {
        if (obj == null) {
            return false;
        }
        return isPrimitive(obj) || String.class.equals(obj.getClass());
    }

    /**
     * 获取类所有字段（包括父类，不包括子类）
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        if (null == clazz) {
            return Collections.emptyList();
        }
        List<Field> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 过滤静态属性
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            // 过滤 transient 关键字修饰的属性
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            list.add(field);
        }
        // 获取父类字段
        Class<?> superClass = clazz.getSuperclass();
        if (Object.class.equals(superClass)) {
            return list;
        }
        list.addAll(getAllFields(superClass));
        return list;
    }

}
