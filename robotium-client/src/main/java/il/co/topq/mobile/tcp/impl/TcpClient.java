package il.co.topq.mobile.tcp.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * @author limor bortman, tal ben shabtay
 * this class is a standard TCP client connection
 */
public class TcpClient {
	
	private static Logger logger = Logger.getLogger(TcpClient.class);
	private String lastResult;
	private final String host;
	private final int port;

	/**
	 * this CTRO will init the params of the server
	 * @param host the server ip
	 * @param port the server port
	 * @throws Exception
	 */
	public TcpClient(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
	}

	/**
	 * will send the input string to the server
	 * @param data the string to send to the server
	 * @return response from the server
	 */
	public String sendData(String data) {
		Socket socket = null;
		BufferedReader input = null;
		try {
			socket = new Socket(this.host, this.port);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			output.println(data);
			output.flush();
			this.lastResult = input.readLine();
		} 
		catch (UnknownHostException e) {
			logger.error("Uknown host ");
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			logger.error("Failed sending data due to ", e);
			e.printStackTrace();
			return null;
		} 
		finally {
			try {
				if (input != null) {
					input.close();
				}
				if (socket != null) {
					socket.close();
				}
			} 
			catch (Exception e) {
				logger.error("Failed closing resources due to ", e);
			}
		}
		return this.lastResult;
	}

	/**
	 * will get the last response from the server
	 * @return the last response that server sent
	 * @throws IOException
	 */
	public String getData() throws IOException {
		return this.lastResult;
	}
}