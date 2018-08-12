package com.devloper.joker.beaninvoke.support;

import com.alibaba.fastjson.JSONObject;
import com.devloper.joker.beaninvoke.model.InvokeEntity;
import com.devloper.joker.beaninvoke.model.InvokeResult;
import com.devloper.joker.beaninvoke.resolver.InvokeParameterResolver;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InvokeUtilsTest {

    @Test
    public void testAdd() {
        Handler handler = new Handler();
        InvokeEntity entity = new InvokeEntity();

        entity.setMethodName("add");
        entity.setParametersType(Arrays.asList("int", "int"));
        entity.setParametersValue(Arrays.asList("1", "100"));
        InvokeResult result = InvokeUtils.getInvokeResult(handler, entity);
        System.out.println(JSONObject.toJSONString(result));
    }


    @Test
    public void testShowOrder() {
        Handler handler = new Handler();
        InvokeEntity entity = new InvokeEntity();
        entity.setMethodName("showOrder");
        entity.setParametersType(Arrays.asList(
                Order.class.getName(),
                Order.class.getName(),
                "com.devloper.joker.beaninvoke.support.Order[]"
        ));

        entity.setParametersValue(Arrays.asList(new Order("订单1", 100), "{'name': '订单2', 'price': 66}"));
        InvokeResult result = InvokeUtils.getInvokeResult(handler, entity);
        System.out.println(JSONObject.toJSONString(result));
    }


    /**
     * 测试转换指定类型,用于无法直接转换的值
     */
    @Test
    public void testConvertPage() {
        Handler handler = new Handler();
        InvokeEntity entity = new InvokeEntity();
        entity.setMethodName("convertPage");
        entity.setParametersType(Arrays.asList(
                Page.class.getName()
        ));

        entity.setParametersValue(Arrays.asList("{'page': 1, 'num': 15}"));
        InvokeResult result = InvokeUtils.getInvokeResult(handler, entity, new InvokeParameterResolver() {
            @Override
            public boolean supportType(Type type) {
                return type.equals(Page.class);
            }

            @Override
            public Object convert(Object val, Type type, Class beanClass, Object bean, Method method, int index) {
                JSONObject jsonObject = JSONObject.parseObject(val.toString());
                Page page = new Page();
                page.setPageIndex(jsonObject.getIntValue("page"));
                page.setPageNumber(jsonObject.getIntValue("num"));
                return page;
            }
        });
        System.out.println(JSONObject.toJSONString(result));
    }

    @Test
    public void test() throws Exception {
        //直接传入参数执行
        Handler handler = new Handler();

        InvokeResult result = InvokeUtils.getInvokeResult(handler, "add",
                (List) Arrays.asList(int.class, int.class),
                Arrays.asList(1, 3));
        System.out.println(JSONObject.toJSONString(result));

        //调用static方法
        result = InvokeUtils.getInvokeResult(handler, "getNow",
                (List) Arrays.asList(),
                null);
        System.out.println(JSONObject.toJSONString(result));

        result = InvokeUtils.getInvokeResult(Handler.class, "getNow",
                        null,
                null);
        System.out.println(JSONObject.toJSONString(result));

        //获取对应的方法
        Method method = InvokeUtils.getMethod(Handler.class, "add", InvokeUtils.getParameterTypes(Arrays.asList("int", "int")));
        //根据方法及其他参数去转换参数值后获取结果
        result = InvokeUtils.getInvokeResult(method, handler, InvokeUtils.getConvertParameterValues(method, Arrays.asList(null, "200")));
        System.out.println(JSONObject.toJSONString(result));

        //未转换时参数错误
        try {
            result = InvokeUtils.getInvokeResult(method, handler, Arrays.asList(100, "200"));
            System.out.println(JSONObject.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class Handler {
    public int add(int a, int b) {
        return a + b;
    }

    public void showOrder(Order order1, Order order2, Order... orders) {
        System.out.println("show order1 : " + JSONObject.toJSONString(order1));
        System.out.println("show order2 : " + JSONObject.toJSONString(order2));
    }

    public Page convertPage(Page page) {
        return page;
    }

    public static String getNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}

class Order {
    private String name;
    private int price;

    public Order() {
    }

    public Order(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}


class Page {

    private int pageIndex;
    private int pageNumber;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}