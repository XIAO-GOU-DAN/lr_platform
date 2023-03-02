package com.lr.platform.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lr.platform.Serializer.PrivacySerializer;
import com.lr.platform.enums.PrivacyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = PrivacySerializer.class)
public @interface PrivacyEncrypt {
    PrivacyType type() default PrivacyType.DEFAULT;

    int prefix() default 1;

    int suffix() default 1;

    String symbol() default "*";
}
