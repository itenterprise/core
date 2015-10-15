package com.it.core.serialization;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateDeserializer extends JsonDeserializer<Date> {
	private static final Pattern pat = Pattern
			.compile("/Date\\((\\d+)\\)/");

	@Override
	public Date deserialize(JsonParser jsonparser,
			DeserializationContext deserializationcontext) 
					throws IOException, JsonProcessingException {

		String date = jsonparser.getText();
		try {
			Matcher m = pat.matcher(date);
			if (m.matches()) {
				long time = Long.valueOf(m.group(1));
				//int offset = Integer.valueOf(m.group(2));
				Date d = new Date(time);
				return d;
			}
            return null;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

}