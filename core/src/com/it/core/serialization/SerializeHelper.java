package com.it.core.serialization;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class SerializeHelper {
	public static <T> T deserialize(String json, Class<T> type){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (T)mapper.readValue(json, type);
		} catch (JsonParseException e) {
			Log.d("JSON", json);
			e.printStackTrace();
		} catch (JsonMappingException e) {
			Log.d("JSON", json);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("JSON", json);
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> ArrayList<T> deserializeList(String json, Class<T> type){
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory t = TypeFactory.defaultInstance();
		try {
			return mapper.readValue(json, t.constructCollectionType(ArrayList.class, type));
		} catch (JsonParseException e) {
			Log.d("JSON", json);
			e.printStackTrace();
		} catch (JsonMappingException e) {
			Log.d("JSON", json);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("JSON", json);
			e.printStackTrace();
		}
		return null;
	}

	public static String serialize(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}