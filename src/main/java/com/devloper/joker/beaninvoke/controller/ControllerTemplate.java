package com.devloper.joker.beaninvoke.controller;

import com.devloper.joker.beaninvoke.model.InvokeEntity;
import com.devloper.joker.beaninvoke.resolver.InvokeParameterResolver;
import com.devloper.joker.beaninvoke.support.Assert;
import com.devloper.joker.beaninvoke.support.InvokeUtils;

public abstract class ControllerTemplate {

    public abstract InvokeParameterResolver parameterResolver();

    public abstract Object getBean(String beanName);

    public ResponseResult getInvokeResult(InvokeEntity model) {
        ResponseResult result = new ResponseResult();
        try {
            String beanName = model.getBeanName();
            Assert.isNotEmpty(beanName, "bean name must be not blank");

            Object bean = null;
            if (!model.isByClass()) {
                //获取bean对象
                try {
                    bean = getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Assert.notNull(bean, "not found bean named : " + beanName);
            }
            result.setData(InvokeUtils.getInvokeResult(bean, model, parameterResolver()));
            result.setMsg("加载执行结果成功");
        } catch (Exception e) {
            result.setStatus(false);
            e.printStackTrace();
            result.setMsg(e.getMessage() == null ? "发生异常" : e.getMessage());
        }
        return result;
    }

    public ResponseResult getInvokeMethodEntitys(String beanName, boolean isByClass) {
        ResponseResult result = new ResponseResult();
        try {
            Assert.isNotEmpty(beanName, "bean name must be not blank");

            Class classType;
            if (isByClass) {
                classType = InvokeUtils.getClassByName(beanName);
            } else {
                Object bean = null;
                try {
                    bean = getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Assert.notNull(bean, "not found bean named : " + beanName);
                classType = bean.getClass();
            }
            result.setData(InvokeUtils.getInvokeMethodEntitys(classType));
            result.setMsg("加载方法列表成功");
        } catch (Exception e) {
            result.setStatus(false);
            e.printStackTrace();
            result.setMsg(e.getMessage() == null ? "发生异常" : e.getMessage());
        }
        return result;
    }


}
