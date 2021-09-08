package io.shulie.takin.cloud.common.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * long型转换成字符串序列化类
 *
 * @author shulie
 * @version v1.0
 * @2018年5月21日
 */
public class LongToStringFormatSerialize extends JsonSerializer<Long> {

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s 字符串
     * @return -
     * @author shulie
     * @2018年5月21日
     * @version v1.0
     */
    public String subZeroAndDot(String s) {
        if (s == null) {
            return s;
        }
		if (s.contains(".")) {
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
        return s;
    }

    /**
     * 继承父类的serialize方法
     *
     * @param value       字符串value
     * @param gen         JsonGenerator gen
     * @param serializers JsonGenerator serializers
     * @author shulie
     * @2018年5月21日
     * @version v1.0
     */
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
        gen.writeString(subZeroAndDot(value.toString()));
    }

}
