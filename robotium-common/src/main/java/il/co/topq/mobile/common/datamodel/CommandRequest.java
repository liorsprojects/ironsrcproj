package il.co.topq.mobile.common.datamodel;

/**
 * 
 * @author tal ben shabtay
 *
 *	the data model of the commands that are sent to the server
 *  from the client
 */
public class CommandRequest {
	
	private String command;
	private String[] params;
	
	/**
	 * DOES NOTHING! Only made for json parsing
	 */
	public CommandRequest() {}

	/**
	 * will init the object with the input values
	 * @param command the string command
	 * @param params the parameters of the command
	 */
	public CommandRequest(String command,String... params) {
		this.command = command;
		this.params = params;
	}
	
	/**
	 * will get the command
	 * @return command string
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * will get the parameters
	 * @return array of parameters
	 */
	public String[] getParams() {
		return params;
	}

}
