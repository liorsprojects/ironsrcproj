package com.ironsource.mobile.fiddler;

public class FiddlerApi {

	/**
	 * send shutdown signal to the fiddler program
	 * 
	 * @return
	 */
	public static String shutdown() {
		return "{'method':'shutdown','params':[],'id':1}";
	}
	
	/**
	 * ping the jsonRpcConnection and return pong if connected.
	 * 
	 * @return
	 */
	public static String ping() {
		return "{'method':'ping','params':[],'id':1}";
	}
	
	//pram example
//	public static String getProtocol(String protocol) {
//		return "{'method':'getProtocol','params':[\"" + protocol +"\"],'id':1}";
//	}
}
