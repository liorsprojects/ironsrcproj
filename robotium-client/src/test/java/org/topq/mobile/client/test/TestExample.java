package org.topq.mobile.client.test;

import java.util.List;

import il.co.topq.mobile.client.impl.MobileClient;
import il.co.topq.mobile.client.impl.WebElement;
import il.co.topq.mobile.client.interfaces.MobileClientInterface;
import il.co.topq.mobile.common.client.enums.HardwareButtons;
import il.co.topq.mobile.common.datamodel.CommandResponse;

import org.junit.Before;
import org.junit.Test;

import sun.awt.windows.WWindowPeer;

/**
 * 
 * @author tal ben shabtay this class is the main test class which includes a
 *         single main method that shows the usage of the mobile client
 *         interface.
 * 
 */
public class TestExample {

	private static final String IP = "192.168.56.101";
	private MobileClientInterface mobile;
	
	
	@Before
	public void setUp() throws Exception{
		mobile = new MobileClient(IP, 4321);
		mobile.launch("com.mobilecore.mctester.MainActivity");
	}
	
	@Test
	public void testExample() throws Exception {
		Thread.sleep(3000);
		mobile.clickOnButtonWithText("Show if ready");
		Thread.sleep(3000);
		
		List<WebElement> elements = mobile.getCurrentWebElements();
		printElements(elements);
		
		mobile.clickOnScreen(360, 980, false);
//		for (WebElement webElement : elements) {
//			if("button_inner".equals(webElement.getClassName()) || "download_button".equals(webElement.getId())) {
//				System.out.println("Button inner found");
//				CommandResponse res = 
//				if(res.isSucceeded()) {
//					System.out.println("Clicked response well");
//				}
//			}
//		}
		Thread.sleep(2000);
		//mobile.finishOpenedActivities();
		//mobile.closeConnection();
	}
	
	private void printElements(List<WebElement> list) {
		
		StringBuffer sb = new StringBuffer("\n\n++Web Elements++\n\n");
		for (WebElement webElement : list) {
			sb.append("==ELEMENT==").append("\n");
			sb.append("tag: " + webElement.getTag()).append("\n");
			sb.append("id: " + webElement.getId()).append("\n");
			sb.append("class: " +webElement.getClassName()).append("\n");
			sb.append("text: " +webElement.getText()).append("\n");
			sb.append("X: " +webElement.getX()).append("\n");
			sb.append("Y: " +webElement.getY()).append("\n");
			sb.append("===========\n");
		}
		System.out.println(sb.toString());
	}
	
	
	
//		mobile.enterText(0, "tal@tal.com");
//		mobile.enterText(1, "1234567");
//		mobile.clickOnButtonWithText("Sign in or register");
//		Thread.sleep(1000 * 5);
//		mobile.clickOnButtonWithText("Ok");
//		mobile.clickOnButtonWithText("Sign in or register");
//		Thread.sleep(1000 * 5);
//		mobile.clickOnButtonWithText("Ok");
//	}
//	
//	@Test
//	@Ignore
//	public void testClickByExpression() throws Exception {
//		mobile.enterText(0, "tal@tal.com");
//		mobile.enterText(1, "1234567");
//		mobile.click("//Button[@id='2131165190']");
//		Thread.sleep(1000 * 5);
//		mobile.click("//Button[@text='Ok']");
//		
//	}


}
