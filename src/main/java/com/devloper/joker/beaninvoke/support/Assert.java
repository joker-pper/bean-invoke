package com.devloper.joker.beaninvoke.support;

/**
 *
 * Copyright 2002-2017 the original for org.springframework.util.Assert author or authors.
 * copy with org.springframework.util.Assert to validate.
 *
 */
public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotEmpty(String value, String message) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isEmpty(String value, String message) {
        if (value != null && value.length() != 0) {
            throw new IllegalArgumentException(message);
        }
    }


}
