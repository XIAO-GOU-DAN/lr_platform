package com.lr.platform.annotations;

import com.lr.platform.enums.WarningLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuestLog {
    String tag();
    WarningLevel level();
}
