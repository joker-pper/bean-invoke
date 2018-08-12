package com.devloper.joker.beaninvoke.resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public abstract class InvokeParameterResolver {

    /**
     * 判断当前类型是否支持
     * @param type
     * @return
     */
    public abstract boolean supportType(Type type);


    /**
     * 解析对应的结果(e.g 解析传入的json数据为分页对象)
     * @param val
     * @param type 当前参数类型
     * @param beanClass 所属类
     * @param bean 实例对象
     * @param method 方法
     * @param index 参数类型索引
     * @return
     */
    public abstract Object convert(Object val, Type type, Class beanClass, Object bean, Method method, int index);

}
