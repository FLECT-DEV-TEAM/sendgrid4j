package jp.co.flect.sendgrid.json;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonByGson implements Json {
	
	public List<Map<String, Object>> parseArray(String str) {
		Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
		return new Gson().fromJson(str, type);
	}
	
	public Map<String, Object> parse(String str) {
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		return new Gson().fromJson(str, type);
	}
	
	public String serialize(Object obj) {
		return new Gson().toJson(obj);
	}
}
