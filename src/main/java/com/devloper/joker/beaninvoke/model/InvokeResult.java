package com.devloper.joker.beaninvoke.model;


public class InvokeResult {

    /**
     * 方法实体信息
     */
    private InvokeMethodEntity methodEntity;

    /**
     * 方法最终执行的参数
     */
    private Object[] parameters;

    /**
     * 方法返回值
     */
    private Object returnVal;

    /**
     * 方法返回值是否为void
     */
    private Boolean returnValIsVoid;


    public InvokeMethodEntity getMethodEntity() {
        return methodEntity;
    }

    public void setMethodEntity(InvokeMethodEntity methodEntity) {
        this.methodEntity = methodEntity;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Object getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Object returnVal) {
        this.returnVal = returnVal;
    }

    public Boolean getReturnValIsVoid() {
        return returnValIsVoid;
    }

    public void setReturnValIsVoid(Boolean returnValIsVoid) {
        this.returnValIsVoid = returnValIsVoid;
    }
}
