package com.github.xjs.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.xjs.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiashuai.xujs
 * @date 2021/9/2 10:59
 */
public class StringToLongListDeserializer extends JsonDeserializer<List<Long>> {
    @Override
    public List<Long> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (StringUtil.isEmpty(text)) {
            return null;
        }
        String[] items = text.split(",");
        List<Long> list = new ArrayList<>(items.length);
        for (String item : items) {
            if(StringUtil.isEmpty(item.trim())){
                continue;
            }
            list.add(Long.valueOf(item));
        }
        return list;
    }
}

