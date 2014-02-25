package com.ironsource.mobile.notinuse;

import org.junit.Test;

import com.ironsource.mobile.fiddler.FiddlerApi;
import com.ironsource.mobile.fiddler.FiddlerJsonRpcClient;

public class FidderApiTests {

	@Test
	public void testFiddlerApiConnection() throws Exception {
		FiddlerJsonRpcClient fiddler = new FiddlerJsonRpcClient();
		fiddler.setHost("127.0.0.1");
		fiddler.setPort(3333);
		System.out.println(fiddler.execute(FiddlerApi.ping()));
		
	   // System.out.println(fiddler.execute(FiddlerApi.setOfferwallJsonPath("offerwall", "C:\\\\a\\\\b")));
	}
}
