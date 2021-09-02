package com.github.xjs.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.xjs.util.DateUtil;
import com.github.xjs.util.StringUtil;

import java.io.IOException;
import java.util.Date;

/**
 * @author jiashuai.xujs
 * @date 2021/9/2 10:56
 */
public class String2DateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String text = jsonParser.getText();
        if (StringUtil.isEmpty(text)) {
            return null;
        }
        if (text.contains(":")) {
            return DateUtil.parse(text, DateUtil.FORMAT_YMDHMS);
        } else  {
            return DateUtil.parse(text, DateUtil.FORMAT_YMD);
        }
    }
}
