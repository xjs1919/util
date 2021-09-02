package com.github.xjs.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.xjs.util.DateUtil;

import java.io.IOException;
import java.util.Date;

/**
 * @author jiashuai.xujs
 * @date 2021/9/2 10:54
 */
public class DateToStringSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (date != null) {
            jsonGenerator.writeString(DateUtil.format(date,DateUtil.FORMAT_YMDHMS));
        } else {
            jsonGenerator.writeNull();
        }
    }

}
