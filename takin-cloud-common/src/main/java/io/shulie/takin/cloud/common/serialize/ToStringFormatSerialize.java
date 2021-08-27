package io.shulie.takin.cloud.common.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 字符串格式序列化类
 *
 * @author shulie
 * @2018年5月21日
 * @version v1.0
 */
public class ToStringFormatSerialize extends JsonSerializer<String>{

	/**
	 * 字符串转序列化方法
	 * @author shulie
	 * @2018年5月21日
	 * @param value 字符串value
	 * @param gen JsonGenerator gen
	 * @param serializers JsonGenerator serializers
	 * @version v1.0
	 */
	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeString(subZeroAndDot(value.toString()));

	}

	/**
	 * 字符串转换
	 * 这个不知道有啥用
	 * @author shulie
	 * @2018年5月21日
	 * @param s 字符串s
	 * @return 字符串s
	 * @version v1.0
	 */
  public String subZeroAndDot(String s){
	  return s;
  }

}
