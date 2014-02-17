package com.ironsource.mobile.fiddler;

public class FiddlerApi {

	/**
	 * set the path of the json type we want to inject 
	 */
	public static String setOfferwallJsonPath(String type, String path) {
		return "{'method':'setOfferwallJsonPath','params':['"+ type +"','"+ path +"'],'id':1}";
	}
	
	/**
	 * set session by name to null
	 *  {offerwall, stickeez, slider, all}
	 */
	public static String clearInjection(String type) {
		return "{'method':'clearInjection','params':[\""+ type +"\"],'id':1}";
	}
	
	/**
	 * set session by name to null
	 *  {offerwallSession, stickeezSession, sliderSession, all}
	 */
	public static String clearSession(String sessionName) {
		return "{'method':'clearSession','params':[\""+ sessionName +"\"],'id':1}";
	}
	
	/**
	 * send shutdown signal to the fiddler program
	 */
	public static String shutdown() {
		return "{'method':'shutdown','params':[],'id':1}";
	}
	
	/**
	 * ping the jsonRpcConnection and return pong if connected.
	 */
	public static String ping() {
		return "{'method':'ping','params':[],'id':1}";
	}
	public static String pingNew() {
		return "{'method':'pingNew','params':[],'id':1}";
	}
	
	//pram example
//	public static String getProtocol(String protocol) {
//		return "{'method':'getProtocol','params':[\"" + protocol +"\"],'id':1}";
//	}
}
