package il.co.topq.mobile.common.datamodel;

import il.co.topq.mobile.common.client.enums.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 
 * @author tal ben shabtay
 *
 *	this class is a basic data model for properties
 *	that the client can send to the server
 */
public class ClientConfiguration {
	
	private Properties clientProperties;
	
	/**
	 * DEFAULT CTOR
	 */
	public ClientConfiguration() {
		this.clientProperties = new Properties();
	}
	
	/**
	 * builder method that add the input property and value
	 * @param property
	 * @param value
	 * @return
	 */
	public ClientConfiguration buildProperty(ClientProperties property,String value) {
		this.clientProperties.put(property.name(), value);
		return this;
	}
	
	/**
	 * checks if a property already exists
	 * @param property the property to verify
	 * @return true/false if property exists
	 */
	public boolean isPropertyExist(ClientProperties property) {
		final String value = this.clientProperties.getProperty(property.name());
		return value != null && !value.isEmpty();
	}
	
	/**
	 * will get the property value
	 * @param property the property to get the value
	 * @return the value of the input property
	 */
	public String getProperty(ClientProperties property) {
		return this.clientProperties.getProperty(property.name());
	}
	
	/**
	 * checks if the object is empty
	 * @return true/false if empty
	 */
	public boolean isEmpty() {
		return this.clientProperties.isEmpty();
	}
	
	/**
	 * will convert the object to a hash map
	 * @return map representation of the current properties
	 */
	public Map<String,String> toMap() {
		Map<String,String> result = new HashMap<String, String>();
		for (Object property : this.clientProperties.keySet()) {
			String propertyString = (String)property;
			result.put(propertyString, this.clientProperties.getProperty(propertyString));
		}
		return result;
	}

}
