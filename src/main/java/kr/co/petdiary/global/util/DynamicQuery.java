package kr.co.petdiary.global.util;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.function.Function;

public class DynamicQuery {
    public static <T> BooleanExpression generateEq(T value, Function<T, BooleanExpression> function) {
        if (value == null) {
            return null;
        }

        return function.apply(value);
    }
}
