package com.github.xjs.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * @author jiashuai.xujs
 * @date 2021/9/2 10:52
 */
public class DateToLongSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (Objects.nonNull(date)) {
            jsonGenerator.writeNumber(date.getTime());
        } else {
            jsonGenerator.writeNull();
        }
    }
}
