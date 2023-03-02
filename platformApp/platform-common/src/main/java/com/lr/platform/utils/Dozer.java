package com.lr.platform.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.Mapper;
import com.lr.platform.entity.GoogleResponse;
import com.lr.platform.entity.JWTClaims;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class Dozer {

    public static void main(String[] args){
        GoogleResponse googleResponse=null;
        JWTClaims test=convert(googleResponse, JWTClaims.class);
        System.out.println(test);
    }

    /**
     * dozer转换的核心mapper对象
     */
    private static final ObjectMapper objectMapper=new ObjectMapper();

    private static Mapper dozerMapper;

    @Autowired
    private Mapper mapper;

    @PostConstruct
    private void construct() {
        Dozer.setDozerMapper(mapper);
    }

    private static void setDozerMapper(Mapper dozerMapper) {
        Dozer.dozerMapper = dozerMapper;
    }

    /**
     * 转换实体为另一个指定的实体
     * 任意一个参数为NULL时 会抛出NPE
     * {@link com.github.dozermapper.core.util.MappingValidator#validateMappingRequest}
     *
     * @param source 源实体 不能为NULL
     * @param clazz 目标实体 不能为NULL
     * @param <T> 泛型
     * @return 转换后的结果
     */
    @Nullable
    public static <T> T convert(Object source, @NonNull Class<T> clazz) {
        if (source==null)return null;
        return  dozerMapper.map(source, clazz);
    }

    /**
     * 转换List实体为另一个指定的实体
     * source如果为NULL 会使用空集合
     * 在目标实体为NULL时 会抛出NPE
     * {@link com.github.dozermapper.core.util.MappingValidator#validateMappingRequest}
     *
     * @param source 源集合 可以为NULL
     * @param clazz 目标实体 不能为NULL
     * @param <T> 泛型
     * @return 转换后的结果
     */
    @Nullable
    public static <T> List<T> convert(@Nullable List<?> source, @NonNull Class<T> clazz) {
        return Optional.ofNullable(source)
                .orElse(Collections.emptyList())
                .stream()
                .map(bean -> dozerMapper.map(bean, clazz))
                .collect(Collectors.toList());
    }

    public static <T> T mapToObject(Map<String,?> source,Class<T> tClass){
        return objectMapper.convertValue(source,tClass);
    }

    public static String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        }catch (Exception e){
            return null;
        }
    }

    public static <T> T toEntity(String str,Class<T> tClass){
        try {
            return objectMapper.readValue(str,tClass);
        }catch (Exception e){
            return null;
        }
    }
}

