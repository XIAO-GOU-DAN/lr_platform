package com.lr.platform.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.lr.platform.annotations.PrivacyEncrypt;
import com.lr.platform.enums.PrivacyType;
import com.lr.platform.utils.PrivacyUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class PrivacySerializer extends JsonSerializer<String> implements ContextualSerializer {

    private PrivacyType privacyType;

    private Integer prefix;

    private Integer suffix;

    private String symbol;

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isNotBlank(s) && privacyType != null){
            switch (privacyType){
                case DEFAULT:
                    jsonGenerator.writeString(PrivacyUtil.desValue(s,prefix,suffix,symbol));
                    break;
                case QQ:
                    jsonGenerator.writeString(PrivacyUtil.hideQQ(s));
                    break;
                case NAME:
                    jsonGenerator.writeString(PrivacyUtil.hideChineseName(s));
                    break;
                case STUDENT_NUMBER:
                    jsonGenerator.writeString(PrivacyUtil.hideStudentNumber(s));
                    break;
                case PHONE:
                    jsonGenerator.writeString(PrivacyUtil.hidePhone(s));
                    break;
                case EMAIL:
                    jsonGenerator.writeString(PrivacyUtil.hideEmail(s));
                    break;
                case IP:
                    jsonGenerator.writeString(PrivacyUtil.hideIp(s));
                    break;
                default:
                    throw new IllegalArgumentException("unknown privacy type"+privacyType);
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null){
            if (Objects.equals(beanProperty.getType().getRawClass(),String.class)){
                PrivacyEncrypt privacyEncrypt = beanProperty.getAnnotation(PrivacyEncrypt.class);
                if (privacyEncrypt == null){
                    privacyEncrypt = beanProperty.getContextAnnotation(PrivacyEncrypt.class);
                }
                if (privacyEncrypt != null){
                    return new PrivacySerializer(privacyEncrypt.type(),
                            privacyEncrypt.prefix(),
                            privacyEncrypt.suffix(),
                            privacyEncrypt.symbol());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(),beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}
