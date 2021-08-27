package io.shulie.takin.cloud.common.serialize;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 说明:
 *
 * @author shulie
 * @version v1.0
 * @Date: Create in 2018/8/23 17:36
 */
public class DateToLongFormatSerialize extends JsonSerializer<Date> {

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
            gen.writeString(String.valueOf(date.getTime()));
        }
    }
}
