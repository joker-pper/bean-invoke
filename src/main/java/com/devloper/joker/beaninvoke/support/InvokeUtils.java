package com.devloper.joker.beaninvoke.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.devloper.joker.beaninvoke.model.*;
import com.devloper.joker.beaninvoke.resolver.InvokeParameterResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class InvokeUtils {

    public static InvokeResult getInvokeResult(InvokeEntity devDomain) {
        return getInvokeResult(null, devDomain, (InvokeParameterResolver) null);
    }

    public static InvokeResult getInvokeResult(InvokeEntity devDomain, InvokeParameterResolver resolver) {
        return getInvokeResult(null, devDomain, resolver);
    }


    public static InvokeResult getInvokeResult(Object bean, InvokeEntity model) {
        return getInvokeResult(bean, model, null);
    }

    /**
     * 当isByClass时通过beanName获取对应的classType去获取对应的结果
     * @param bean
     * @param model
     * @param resolver
     * @return
     */
    public static InvokeResult getInvokeResult(Object bean, InvokeEntity model, InvokeParameterResolver resolver) {
        Assert.notNull(model, "invoke entity must not be null");
        Class classType = null;
        List<Class> parameterTypes = null;
        try {
            if (model.isByClass()) {
                String className = model.getBeanName();
                Assert.isNotEmpty(className, "when by class, bean name must be not null");
                classType = getClassByName(className);
            }
            parameterTypes = getParameterTypes(model.getParametersType());
        } catch (ClassNotFoundException e) {
            Assert.isTrue(false, e.getMessage());
        }
        List parametersValue = model.getParametersValue();
        return getInvokeResult(bean, classType, model.getMethodName(), parameterTypes, parametersValue, resolver);
    }


    public static InvokeResult getInvokeResult(Object bean, String methodName, List<Class> parameterTypes, List parameterValues) {
        return getInvokeResult(bean, methodName, parameterTypes, parameterValues, null);
    }

    /**
     * 通过参数获取该bean对象对应方法的执行结果
     *
     * @param bean
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @param resolver
     * @return
     */
    public static InvokeResult getInvokeResult(Object bean, String methodName, List<Class> parameterTypes, List parameterValues, InvokeParameterResolver resolver) {
        return getInvokeResult(bean, null, methodName, parameterTypes, parameterValues, resolver);
    }


    public static InvokeResult getInvokeResult(Class beanClassType, String methodName, List<Class> parameterTypes, List parameterValues) {
        return getInvokeResult(beanClassType, methodName, parameterTypes, parameterValues, null);
    }

    /**
     * 通过参数获取该类对应方法的执行结果
     *
     * @param beanClassType
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @param resolver
     * @return
     */
    public static InvokeResult getInvokeResult(Class beanClassType, String methodName, List<Class> parameterTypes, List parameterValues, InvokeParameterResolver resolver) {
        return getInvokeResult(null, beanClassType, methodName, parameterTypes, parameterValues, resolver);
    }


    /**
     * 当beanClassType为null时,通过bean获取类型,最后通过参数返回对应的结果
     *
     * @param bean
     * @param beanClassType
     * @param methodName      方法名
     * @param parameterTypes  各个参数的类型
     * @param parameterValues 各个参数的值
     * @param resolver        用于解析指定类型时的参数
     * @return
     */
    private static InvokeResult getInvokeResult(Object bean, Class beanClassType, String methodName, List<Class> parameterTypes, List parameterValues, InvokeParameterResolver resolver) {
        Assert.isNotEmpty(methodName, "method name must not be blank");
        if (beanClassType == null) {
            Assert.notNull(bean, "bean must not be null");
            beanClassType = bean.getClass();
        }
        //获取method
        Method method = getMethod(beanClassType, methodName, parameterTypes);
        //获取转换后的数据
        List convertParameterValues = getConvertParameterValues(method, beanClassType, bean, parameterValues, resolver);
        //返回结果
        return getInvokeResult(method, bean, convertParameterValues);
    }


    public static List getConvertParameterValues(Method method, List parameterValues) {
        return getConvertParameterValues(method, null, null, parameterValues, null);
    }

    public static List getConvertParameterValues(Method method, Class beanClassType, List parameterValues, InvokeParameterResolver resolver) {
        return getConvertParameterValues(method, beanClassType, null, parameterValues, resolver);
    }

    public static List getConvertParameterValues(Method method, Object bean, List parameterValues, InvokeParameterResolver resolver) {
        return getConvertParameterValues(method, null, bean, parameterValues, resolver);
    }

    /**
     * 通过method及相关参数获取转换后的参数值
     * (beanClassType与bean可传null,它们决定了当resolver存在时该两项参数值是否存在）
     * @param method
     * @param beanClassType bean类型
     * @param bean
     * @param parameterValues 参数值列表
     * @param resolver 用于解析指定类型时的参数
     * @return
     */
    private static List getConvertParameterValues(Method method, Class beanClassType, Object bean, List parameterValues, InvokeParameterResolver resolver) {
        if (beanClassType == null) {
            if (bean != null) {
                beanClassType = bean.getClass();
            }
        } else {
            if (bean != null) {
                Assert.isTrue(beanClassType.equals(bean.getClass()), "bean type must be eq bean class type");
            }
        }

        //处理转换后的数据
        List convertParameterValues = new ArrayList();
        Type[] types = method.getGenericParameterTypes();

        int typesLength = types.length;
        int parameterValuesSize = 0;
        if (parameterValues != null) {
            parameterValuesSize = parameterValues.size();
        }
        if (typesLength == 0) {
            Assert.isTrue(parameterValuesSize == 0, method.toGenericString() + " expect parameter size is 0");
        }
        for (int i = 0; i < typesLength; i ++) {
            Object val = null;
            if (i < parameterValuesSize) {
                val = parameterValues.get(i);
            }
            Type classType = types[i];

            String errorMsg = method.toGenericString() + " \n covert data " + val + " to " + classType.toString() +
                    " error, caused by : ";

            //当前值是否已处理
            boolean flag = false;
            Class currentValClass = null;

            if (!flag) {
                if (classType instanceof Class) {
                    currentValClass = (Class) classType;
                    try {
                        val = TypeUtils.castToJavaBean(val, currentValClass);
                        flag = true;
                    } catch (Exception e) {
                    }
                }
            }

            if (!flag && val != null) {
                try {
                    if (resolver != null) {
                        if (resolver.supportType(classType)) {
                            //通过resolver转换
                            val = resolver.convert(val, classType, beanClassType, bean, method, i);
                            flag = true;
                        }
                    }

                    if (!flag) {
                        //最后通过json解析成对应的参数
                        val = JSON.parseObject(val.toString(), classType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.isTrue(false, errorMsg + e.getMessage());
                }
            }

            if (currentValClass != null && currentValClass.isPrimitive()) {
                Assert.notNull(val, errorMsg + "primitive class val not allowed null");
            }
            convertParameterValues.add(val);
        }
        return convertParameterValues;
    }

    /**
     * 获取方法的执行结果
     * @param method
     * @param bean
     * @param convertParameterValues 转换后符合实际类型的值
     * @return
     */
    public static InvokeResult getInvokeResult(Method method, Object bean, List convertParameterValues) {
        Object[] parameterValuesArray = null;
        if (convertParameterValues != null && !convertParameterValues.isEmpty()) {
            parameterValuesArray = convertParameterValues.toArray(new Object[convertParameterValues.size()]);
        }
        Object methodValue = getMethodValue(method, bean, parameterValuesArray);
        InvokeResult result = new InvokeResult();
        result.setMethodEntity(getInvokeMethodEntity(method));
        result.setReturnVal(methodValue);
        result.setParameters(parameterValuesArray);
        result.setReturnValIsVoid(method.getReturnType().equals(void.class));
        return result;
    }

    /**
     * 获取method
     *
     * @param beanClassType
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class beanClassType, String methodName, List<Class> parameterTypes) {
        Method method = null;
        try {
            Class[] parameterTypesArray = null;
            if (parameterTypes != null && !parameterTypes.isEmpty()) {
                parameterTypesArray = parameterTypes.toArray(new Class[parameterTypes.size()]);
            }
            method = getDeclaredMethod(beanClassType, methodName, parameterTypesArray);
        } catch (NoSuchMethodException e) {
            Assert.notNull(method, beanClassType.getName() + " has not found method : " + e.getMessage());
        }
        return method;
    }


    /**
     * 获取当前class的所有方法信息
     *
     * @param clazz
     * @return
     */
    public static List<InvokeMethodEntity> getInvokeMethodEntitys(Class<?> clazz) {
        List<InvokeMethodEntity> propertyList = new ArrayList<InvokeMethodEntity>();
        for (Method method : getDeclaredMethod(clazz)) {
            propertyList.add(getInvokeMethodEntity(method));
        }
        return propertyList;
    }

    public static InvokeMethodEntity getInvokeMethodEntity(Method method) {
        InvokeMethodEntity property = new InvokeMethodEntity();
        if (method != null) {
            String name = method.getName();
            property.setName(name);
            String classType = method.getDeclaringClass().getName();
            property.setClassType(classType);
            String genericAllName = method.toGenericString();
            property.setGenericAllName(genericAllName);

            //去除方法所携带的类名
            property.setGenericName(genericAllName.replace(classType + "." + name, name));
            List<String> parameterTypes = new ArrayList<String>();
            for (Class type : method.getParameterTypes()) {
                //处理成友好的class name
                parameterTypes.add(getClassName(type));
            }
            property.setParameterTypes(parameterTypes);
            property.setReturnType(getClassName(method.getReturnType()));
        }
        return property;
    }

    /**
     * 转换class Name列表为class列表
     *
     * @param types
     * @return
     * @throws ClassNotFoundException
     */
    public static List<Class> getParameterTypes(List<String> types) throws ClassNotFoundException {
        List<Class> parameterTypes = null;
        if (types != null) {
            parameterTypes = new ArrayList<Class>();
            for (String type : types) {
                parameterTypes.add(getClassByName(type));
            }
        }
        return parameterTypes;
    }


    /**
     * 获取class name,并将数组class转换成相应的格式
     *
     * @param classzz
     * @return e.g char[][].class --> char[][]
     */
    public static String getClassName(Class classzz) {
        String className = classzz.getName();
        if (className.contains("[")) {
            //是数组时获取个数
            int count = appearNumber(className, "[");
            String simpleType = "";
            if (!className.endsWith(";")) {
                String temp = className.substring(count);

                Map<Class, String> basicTypeMap = new HashMap<Class, String>();
                basicTypeMap.put(int.class, "I");
                basicTypeMap.put(long.class, "J");
                basicTypeMap.put(short.class, "S");
                basicTypeMap.put(boolean.class, "Z");
                basicTypeMap.put(float.class, "F");
                basicTypeMap.put(double.class, "D");
                basicTypeMap.put(byte.class, "B");
                basicTypeMap.put(char.class, "C");

                for (Class key : basicTypeMap.keySet()) {
                    String val = basicTypeMap.get(key);
                    if (val.equals(temp)) {
                        simpleType = key.getName();
                        break;
                    }
                }
            } else {
                simpleType = className.substring(count + 1, className.length() - 1);
            }

            className = simpleType;
            for (int i = 0; i < count; i++) {
                className += "[]";
            }
        }
        return className;
    }


    /**
     * 根据className获取对应的class
     *
     * @param className int/int[]/com.xxx.User[]
     * @return
     * @throws ClassNotFoundException
     */
    public static Class getClassByName(String className) throws ClassNotFoundException {

        Map<Class, String> basicTypeMap = new HashMap<Class, String>();
        basicTypeMap.put(int.class, "I");
        basicTypeMap.put(long.class, "J");
        basicTypeMap.put(short.class, "S");
        basicTypeMap.put(boolean.class, "Z");
        basicTypeMap.put(float.class, "F");
        basicTypeMap.put(double.class, "D");
        basicTypeMap.put(byte.class, "B");
        basicTypeMap.put(char.class, "C");

        String basicTypeComplex = null;
        for (Class key : basicTypeMap.keySet()) {
            String simpleName = key.getSimpleName();
            if (className.startsWith(simpleName)) {
                //说明是基本类型时
                if (className.equals(simpleName)) {
                    return key;
                } else {
                    Assert.isTrue(className.matches(simpleName + "(\\[\\])+"), className + " not support");
                    basicTypeComplex = basicTypeMap.get(key);
                    break;
                }
            }
        }

        if (basicTypeComplex == null) {
            List<Class> classList = (List) Arrays.asList(String.class, Integer.class, Long.class,
                    Short.class, Boolean.class, Date.class,
                    Float.class, Double.class, Byte.class,
                    Character.class, List.class, Map.class,
                    Set.class, ArrayList.class, HashMap.class,
                    HashSet.class);

            for (Class val : classList) {
                String simpleName = val.getSimpleName();
                if (className.startsWith(simpleName)) {
                    if (className.equals(simpleName)) {
                        return val;
                    } else {
                        Assert.isTrue(className.matches(simpleName + "(\\[\\])+"), className + " not support");
                        className = className.replace(val.getSimpleName(), val.getName());
                        break;
                    }
                }
            }
        }

        if (className.endsWith("[]")) {
            //数组时,e.g Object[].class ==> java.lang.Object[]
            String current = "";
            int count = appearNumber(className, "[]");
            for (int i = 0; i < count; i++) {
                current += "[";
            }
            if (basicTypeComplex != null) {
                //基本类型的数组时
                current += basicTypeComplex;
            } else { //为其他类型时
                current += "L" + className.substring(0, className.length() - 2 * count) + ";";
            }
            className = current;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("not found class named " + className, e);
        }
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText
     * @param findText
     * @return
     */
    private static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }


    /**
     * 获取该类的全部方法
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    public static List<Method> getDeclaredMethod(Class<?> clazz) {
        List<Method> methodList = new ArrayList<Method>();
        if (clazz != null) {
            for (; clazz != null; clazz = clazz.getSuperclass()) {
                List<Method> currentMethodList = getActualDeclaredMethod(clazz);
                if (methodList.isEmpty()) {
                    methodList.addAll(currentMethodList);
                } else {
                    for (Method method : currentMethodList) {
                        boolean contains = false;
                        for (Method current : methodList) {
                            if (method.getName().equals(current.getName())) {

                                if (Arrays.asList(method.getParameterTypes()).containsAll(Arrays.asList(current.getParameterTypes()))) {
                                    contains = true;
                                    break;
                                }
                            }
                        }

                        if (!contains) {
                            methodList.add(method);
                        }
                    }

                }
            }
        }
        return methodList;
    }

    public static List<Method> getActualDeclaredMethod(Class<?> clazz) {
        List<Method> methodList = new ArrayList<>();
        if (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                methodList.add(method);
            }
        }
        return methodList;
    }


    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param objectClass    : 类名
     * @param methodName     : 方法名
     * @param parameterTypes : 方法参数类型
     * @return 父类中的方法对象
     */

    public static Method getDeclaredMethod(Class<?> objectClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = null;
        NoSuchMethodException exception = null;
        for (Class<?> clazz = objectClass; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                method = getActualDeclaredMethod(clazz, methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                exception = e;
            }
            if (method != null) {
                break;
            }
        }
        if (method == null) {
            if (exception != null) {
                throw exception;
            }
            throw new NoSuchMethodException();
        }
        return method;
    }

    public static Method getActualDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = null;
        if (clazz != null) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                throw e;
            } catch (SecurityException e) {
            }
        }
        if (method == null) {
            throw new NoSuchMethodException();
        }
        return method;
    }

    /**
     * 获取方法的值
     *
     * @param method
     * @param object
     * @param args
     * @return
     */
    public static Object getMethodValue(Method method, Object object, Object... args) {
        Object result = null;
        if (method != null) {
            method.setAccessible(true);
            try {
                result = method.invoke(object, args);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                Throwable throwable = e.getTargetException();
                throw new IllegalStateException(throwable);
            }
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        Method method = getDeclaredMethod(InvokeUtils.class, "getMethodValue", Method.class, Object.class, Object[].class);
        System.out.println(method);

        System.out.println(getClassByName("List[][][][][]"));
        System.out.println(List[][][][][].class);

        System.out.println(char[][][][][].class.getName());
        System.out.println(getClassName(char[][][][][].class));

        System.out.println(getClassByName(getClassName(char[][][][][].class)) == char[][][][][].class);

    }

}
