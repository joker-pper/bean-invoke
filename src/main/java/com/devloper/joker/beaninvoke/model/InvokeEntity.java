package com.devloper.joker.beaninvoke.model;


import java.util.ArrayList;
import java.util.List;

public class InvokeEntity {

    private String beanName;

    /**
     * true时,通过beanName作为className(主要用于执行该类的静态方法)
     * 获取对应的方法、执行对应的方法
     */
    private boolean byClass;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private List<String> parametersType = new ArrayList<String>();

    /**
     * 参数值
     */
    private List parametersValue = new ArrayList();

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    public boolean isByClass() {
        return byClass;
    }

    public void setByClass(boolean byClass) {
        this.byClass = byClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParametersType() {
        return parametersType;
    }

    public void setParametersType(List<String> parametersType) {
        this.parametersType = parametersType;
    }

    public List getParametersValue() {
        return parametersValue;
    }

    public void setParametersValue(List parametersValue) {
        this.parametersValue = parametersValue;
    }
}
