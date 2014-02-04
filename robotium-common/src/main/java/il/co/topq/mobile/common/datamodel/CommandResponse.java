package il.co.topq.mobile.common.datamodel;

/**
 * 
 * @author tal ben shabtay
 * this data model represents the response from the server
 */
public class CommandResponse {
	
	private String originalCommand;
	private String[] params;
	private String response;
	private boolean isSucceeded;
	
	/**
	 * inits the command response with default values
	 */
	public CommandResponse() {
		this.originalCommand = null;
		this.params = null;
		this.response = null;
		this.isSucceeded = false;
	}
	
	/**
	 * will get the string response
	 * @return string response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * will set the string response
	 * @param response the response string to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * will get true/false if command succeeds
	 * @return true/false if command succeeds
	 */
	public boolean isSucceeded() {
		return isSucceeded;
	}

	/**
	 * will set if the command succeeds
	 * @param isSucceeded true/false if command succeeds
	 */
	public void setSucceeded(boolean isSucceeded) {
		this.isSucceeded = isSucceeded;
	}

	/**
	 * will get the original string command
	 * @return the string of the original command
	 */
	public String getOriginalCommand() {
		return originalCommand;
	}

	/**
	 * will get the params of the original command
	 * @return the paramters of the original command
	 */
	public String[] getParams() {
		return params;
	}
	
	/**
	 * will set the original command
	 * @param originalCommand the string of the original command
	 */
	public void setOriginalCommand(String originalCommand) {
		this.originalCommand = originalCommand;
	}

	/**
	 * will set the parameters of the original command
	 * @param params the parameters of the original command
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

}
