package il.co.topq.mobile.common.server.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * @author tal ben shabtay
 * this util class interprets the json to an object and vice versa
 */
public class JsonParser {
	private static final ObjectMapper mapper = new ObjectMapper();
	 
	/**
	 * will parse and object to json
	 * @param obj the object to parse
	 * @return the json of the object
	 */
	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} 
		catch (JsonGenerationException e) {
			e.printStackTrace();
		} 
		catch (JsonMappingException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	 
	/**
	 * will parse a json to an object
	 * @param json the input json represents an object
	 * @param clzz the class of the object to parse
	 * @return the object from the json
	 */
	public static <T> T fromJson(String json,Class<T> clzz) {				
		try {
			return mapper.readValue(json, clzz);
		} 
		catch (JsonParseException e) {
			e.printStackTrace();
		} 
		catch (JsonMappingException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}

}

