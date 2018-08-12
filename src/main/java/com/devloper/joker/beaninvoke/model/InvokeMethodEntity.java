package com.devloper.joker.beaninvoke.model;

import java.util.List;

public class InvokeMethodEntity {

    /**
     * 方法名称
     */
    private String name;

    /**
     * 不包含类名的方法信息
     */
    private String genericName;

    /**
     * method.toGenericString()
     */
    private String genericAllName;
    /**
     * 方法参数类型名称
     */
    private List<String> parameterTypes;

    /**
     * 返回值类型名称
     */
    private String returnType;

    /**
     * 方法所属类的名称
     */
    private String classType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getGenericAllName() {
        return genericAllName;
    }

    public void setGenericAllName(String genericAllName) {
        this.genericAllName = genericAllName;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }
}
