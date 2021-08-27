package io.shulie.takin.cloud.common.serialize;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 日期转换成字符串工具类
 *
 * @author shulie
 * @2018年5月16日
 * @version v1.0
 */
public class DateToStringFormatSerialize  extends JsonSerializer<Date> {

	//日期格式
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 继承父类的serialize方法
	 * @author shulie
	 * @2018年5月21日
	 * @param date 日期
	 * @param gen JsonGenerator gen
	 * @version v1.0
	 */
	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if(date == null){
			gen.writeString("");
		}else{
		    gen.writeString(DATE_FORMAT.format(date));
		}
	}
}
