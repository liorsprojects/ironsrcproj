package org.topq.uiautomator.client.example;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.Selector;
import org.topq.uiautomator.client.DeviceClient;

import com.android.uiautomator.core.UiObjectNotFoundException;


public class Example {
	
	
	public static void main(String[] args)  {
		try{
		AutomatorService client = DeviceClient.getUiAutomatorClient("http://192.168.56.101:9008");
		client.click(new Selector().setText("MCTester"));
		client.click(new Selector().setText("Show (not force)"));
		String obj = client.getUiObject(new Selector().setClassName("android.webkit.WebView"));
		client.pressKey("down");
		client.pressKey("down");
		String ltt = client.getLastTraversedText();
		System.out.println(ltt);
		}catch (MalformedURLException e){
			System.out.println("Error while trying to connect to server");
		} catch (UiObjectNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not find object");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
