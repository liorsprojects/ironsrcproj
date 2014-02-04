package com.ironsource.mobile.test;

import il.co.topq.mobile.client.impl.MobileClient;
import il.co.topq.mobile.client.impl.WebElement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import jsystem.extensions.report.html.Report;
import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.ReporterHelper;
import junit.framework.SystemTestCase4;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.python.modules.re;
import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.ObjInfo;
import org.topq.uiautomator.Selector;

import android.content.res.ObbInfo;

import com.android.ddmlib.logcat.LogCatMessage;
import com.android.uiautomator.core.UiObject;
import com.ironsource.mobile.ADBConnection;
import com.ironsource.mobile.FlowCode;
import com.ironsource.mobile.LogcatHelper;
import com.ironsource.mobile.MobileCoreMsgCode;
import com.ironsource.mobile.MobileSO;
import com.ironsource.mobile.RSCode;
import com.ironsource.mobile.fiddler.FiddlerJsonRpcClient;
import com.ironsource.mobile.reporters.ImageFlowHtmlReport;
import com.jhlabs.image.WoodFilter;

public class MCTesterTests extends SystemTestCase4 {

	private static MobileSO mobile;
	private static ADBConnection adb;
	private static AutomatorService uiautomatorClient;
	private static MobileClient robotiumClient;
	private static List<WebElement> offerwallElements;
	private static FiddlerJsonRpcClient fiddlerJsonRpcClient; 
	ImageFlowHtmlReport flowHtmlReport;
	
	public static final String MCTESTER_ACTIVITY = "com.mobilecore.mctester.MainActivity";

	private int logcatReportTimeout = 30000;
	
	
	@Before
	public void setup() throws Exception{
		if(mobile == null) {
			report.report("init mobile system object");
			mobile = (MobileSO) system.getSystemObject("mobile");
		}
		
		if(fiddlerJsonRpcClient == null) {
			report.report("init fiddler client");
			fiddlerJsonRpcClient = (FiddlerJsonRpcClient) system.getSystemObject("fiddlerJsonRpcClient");
		}
		
		if(uiautomatorClient == null) {
			report.report("retrieving uiautomator client");
			uiautomatorClient = mobile.getUiAutomatorClient();
		}
		
		if(adb == null) {
			report.report("retrieving adb connection");
			adb = mobile.getAdbConnection();
		}
		
		flowHtmlReport = new ImageFlowHtmlReport();
		
		if(adb != null) {
			report.report("clearing logcat");
			adb.clearLogcat();
		}
		
	}

	/**
	 * 1. initialize all clients and servers.
	 * 2. launching the MCtester application.
	 * 3. click on button 'Show if ready' to show offerwall.
	 * 4. get all the webelements of the offerwall.
	 * 5. close all robotium related services and activities.  
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "capture test template", paramsInclude = {})
	public void testSetupTestsTemplate() throws Exception {
			
		report.report("retrieving robotium client");
		robotiumClient = (MobileClient) mobile.getRobotiumClient();
		offerwallElements = new ArrayList<WebElement>();
		
		captureWebview();		
	}

	
	/**
	 * Test close button of the offerwall
	 * 
	 * 1. open MCTester.
	 * 2. click on 'Show (not force)' button to show offerwall.
	 * 3. click on {X} button in the corner of the offerwall.
	 * 4. verify that report is submitted with RS Code Close ('-').
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "close offerwall with the {X} button", paramsInclude = {"logcatReportTimeout"})
	public void testCloseOfferwallWithX() throws Exception {
		
		adb.startActivity(adb.MCTESTER_PKG, adb.MCTESTER_ACTIVITY);

		flowHtmlReport.addTitledImage("In App", adb.getScreenshotWithAdb(null));
		
		report.step("MCTester Launched");
		
		uiautomatorClient.click(new Selector().setText("Show (not force)"));
		report.report("wait for transiton of webview to complete");
		
		mobile.waitForRSCode(RSCode.WALL, FlowCode.OFFERWALL, logcatReportTimeout);
		mobile.waitForRSCode(RSCode.IMPRESSION, FlowCode.OFFERWALL, logcatReportTimeout);
		
		flowHtmlReport.addTitledImage("Clicked on 'Show (not force)'", adb.getScreenshotWithAdb(null));
		boolean elementFound = false;
		for (WebElement element : offerwallElements) {
			if("noThanks".equals(element.getId())) {
				report.report("found element with id = 'noThanks' and about to click on it");
				uiautomatorClient.click(element.getX(), element.getY());
				report.step("clicked on {x} button");
				elementFound = true;
				break;
			}
		}
		if(!elementFound) {
			throw new Exception("Element with id = 'noThanks' wasent found");
		}
		mobile.waitForRSCode(RSCode.CLOSE, FlowCode.OFFERWALL, logcatReportTimeout);
		flowHtmlReport.addTitledImage("After click on {X}", adb.getScreenshotWithAdb(null));
		report.report("screen flow", flowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
	
	}
	
	
	//TODO - recover from not supported app and region
	/**
	 * Test full flow from click to install
	 * 
	 * 1. open MCTester.
	 * 2. click on 'Show (not force)' button to show offerwall.
	 * 3. verify that report is submitted with RS Code Wall ('W') and RS Code Impression ('D').
	 * 4. click on one of the items represent an application and redirect to the play store.
	 * 5. verify that report is submitted with RS Code Click ('C').
	 * 6. wait for play store to launch.
	 * 8. verify that report is submitted with RS Code Report ('S').
	 * 7. download and install the application and wait for completion.
	 * 8. verify that report is submitted with RS Code Install ('+'). 
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "full click to download flow", paramsInclude = {"logcatReportTimeout"})
	public void testFullClickToDownloadFlow() throws Exception {
		
		flowHtmlReport.addTitledImage("Before MCTester Launch", adb.getScreenshotWithAdb(null));
		
		adb.startActivity(adb.MCTESTER_PKG, adb.MCTESTER_ACTIVITY);
		
		if(!uiautomatorClient.waitForExists(new Selector().setText("Show (not force)"), 50000)) {
			throw new Exception("Application MCTester did not launched");
		} 
		flowHtmlReport.addTitledImage("In App", adb.getScreenshotWithAdb(null));
		
		report.step("MCTester Launched");
		
		mobile.waitForManagerMessageToContain(MobileCoreMsgCode.OFFERWALL_MANAGER, "from:LOADING , to:READY_TO_SHOW" , 15000);
		
		uiautomatorClient.click(new Selector().setText("Show (not force)"));
		report.report("wait for transiton of webview to complete");
		mobile.waitForRSCode(RSCode.WALL, FlowCode.OFFERWALL, 600000);
		mobile.waitForRSCode(RSCode.IMPRESSION, FlowCode.OFFERWALL, logcatReportTimeout);
		
		flowHtmlReport.addTitledImage("Clicked on 'Show (not force)'", adb.getScreenshotWithAdb(null));
		
		boolean elementFound = false;
		for (WebElement element : offerwallElements) {
			if("stars".equals(element.getClassName())) {
				report.report("found element with className = 'stars' and about to click on it");
				uiautomatorClient.click(element.getX(), element.getY());
				report.step("clicked on 'stars' of the first app");
				elementFound = true;
				break;
			}
		}
		if(!elementFound) {
			throw new Exception("Element with className = 'stars' wasen't found");
		}
		mobile.waitForRSCode(RSCode.CLICK, FlowCode.OFFERWALL, 10000);
		
		flowHtmlReport.addTitledImage("After click on application in offrewall", adb.getScreenshotWithAdb(null));
		
		report.step("waiting for playstore");
		if (!uiautomatorClient.waitForExists(new Selector().setText("INSTALL"), 5000)) {
		    throw new Exception("Did not navigated to Playstore (check internet connection");
		} 
		
		flowHtmlReport.addTitledImage("Playstore", adb.getScreenshotWithAdb(null));
		Thread.sleep(2000);
		report.report("click INSTALL");
		uiautomatorClient.click(new Selector().setText("INSTALL"));

		if (!uiautomatorClient.waitForExists(new Selector().setText("ACCEPT"), 5000)) {
			throw new Exception("Accept page not visible");
		} 
		flowHtmlReport.addTitledImage("After click INSTALL", adb.getScreenshotWithAdb(null));

		report.report("click ACCEPT");
		uiautomatorClient.click(new Selector().setText("ACCEPT"));
		
	
		
		if(uiautomatorClient.waitForExists(new Selector().setText("Downloading a large app"), 2000)) {
//			//TOOD - verify 'Download using Wi-Fi only' checkbox is checked.
//			ObjInfo obj  = uiautomatorClient.objInfo(new Selector().setText("Download using Wi-Fi only"));
//			obj.isChecked()
			uiautomatorClient.click(new Selector().setText("Proceed").setClassName("android.widget.Button"));
		}
		
		if (!uiautomatorClient.waitForExists(new Selector().setClassName("android.widget.ProgressBar"), 10000)) {
			throw new Exception("Installing not started");
			
		} 
		report.step("installing in progress...");
		flowHtmlReport.addTitledImage("while installing after accept", adb.getScreenshotWithAdb(null));

		report.report("waiting for install to finish");
		
		if (!uiautomatorClient.waitForExists(new Selector().setText("OPEN"), 600000)) {
			throw new Exception("Did not finish downloading after 10 minutes");
		}  
		report.step("Install Completed");
		Thread.sleep(2000);
		flowHtmlReport.addTitledImage("App Installed", adb.getScreenshotWithAdb(null));
		try{
			mobile.waitForRSCode(RSCode.INSATLL, FlowCode.OFFERWALL, 5*60000);
		} catch (Exception e) {
			report.report("rs code '+' wasn't reported after 5 min", Reporter.WARNING);
		}
		
		uiautomatorClient.click(new Selector().setText("UNINSTALL"));
		
		uiautomatorClient.registerClickUiObjectWatcher("uninstall", new Selector[]{new Selector().setText("Do you want to uninstall this app?")}, new Selector().setText("OK"));
		if (!uiautomatorClient.waitForExists(new Selector().setText("INSTALL"), 60000)) {
		    report.report("uninstall didnt complete after 10 minutes", Reporter.WARNING);
		} 
		report.report("screen flow", flowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
		
	}
	
	/**
	 * Test close offerwall using hardware button 'back'
	 * 
	 * 1. open MCTester.
	 * 2. click on 'Show (not force)' button to show offerwall.
	 * 3. verify that report is submitted with RS Code Wall ('W') and RS Code Impression ('D').
	 * 4. press on hardware button 'back'.
	 * 5. verify that report is submitted with RS Code Click ('Q').
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Test close offerwall using hardware button 'back'", paramsInclude = {"logcatReportTimeout"})
	public void testCloseApplicationUsingBackButton() throws Exception {
				
		adb.startActivity(adb.MCTESTER_PKG, adb.MCTESTER_ACTIVITY);
		
		if(!uiautomatorClient.waitForExists(new Selector().setText("Show (not force)"), 50000)) {
			throw new Exception("Application MCTester did not launched");
		} 
		flowHtmlReport.addTitledImage("In App", adb.getScreenshotWithAdb(null));
		
		report.step("MCTester Launched");
		
		uiautomatorClient.click(new Selector().setText("Show (not force)"));
		report.report("wait for transiton of webview to complete");
		mobile.waitForRSCode(RSCode.WALL, FlowCode.OFFERWALL, logcatReportTimeout);
		mobile.waitForRSCode(RSCode.IMPRESSION, FlowCode.OFFERWALL, logcatReportTimeout);
		
		flowHtmlReport.addTitledImage("Clicked on 'Show (not force)'", adb.getScreenshotWithAdb(null));
		
		report.report("about to press 'back' button");
		uiautomatorClient.pressKey("back");
		report.step("pressed back button");
		
		if(!uiautomatorClient.waitUntilGone(new Selector().setClassName("android.webkit.WebView"),5000)) {
			throw new Exception("The offerwall did'nt vanish after 5 seconds since 'back' pressed");
		}
		
		flowHtmlReport.addTitledImage("After \"Back\" button pressed", adb.getScreenshotWithAdb(null));
		mobile.waitForRSCode(RSCode.BACK, FlowCode.OFFERWALL, logcatReportTimeout);
		report.report("screen flow", flowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
	
	}
	
	//TODO - when we have all template we will know what to expect.
	@Test
	@TestProperties(name = "Test navigate to play store by clicking all offerwall elements", paramsInclude = {"logcatReportTimeout"})
	public void testPlaysotreNavigation() throws Exception {
	
		adb.startActivity(adb.MCTESTER_PKG, adb.MCTESTER_ACTIVITY);
		if(!uiautomatorClient.waitForExists(new Selector().setText("Show (not force)"), 50000)) {
			throw new Exception("Application MCTester did not launched");
		} 
		report.step("MCTester Launched");
		flowHtmlReport.addTitledImage("In App", adb.getScreenshotWithAdb(null));
		
		mobile.waitForManagerMessageToContain(MobileCoreMsgCode.OFFERWALL_MANAGER, "from:LOADING , to:READY_TO_SHOW" , 15000);
		
		uiautomatorClient.click(new Selector().setText("Show (not force)"));
		report.report("wait for transiton of webview to complete");

		mobile.waitForRSCode(RSCode.WALL, FlowCode.OFFERWALL, 600000);
		mobile.waitForRSCode(RSCode.IMPRESSION, FlowCode.OFFERWALL, logcatReportTimeout);
		
		flowHtmlReport.addTitledImage("Clicked on 'Show (not force)'", adb.getScreenshotWithAdb(null));
		
		Selector downloadSelector = new Selector().setText("INSTALL");
		Selector openSelector = new Selector().setText("OPEN");
		Selector notCountrySelector = new Selector().setText("This item isn't available in your country.");
		Selector notSupporSelector = new Selector().setText("Your device is�nt compatible with this version.");
		
		String currentApp = "";
		for(WebElement element : getClickToMarketElements()) {
			
			if("inner_item".equals(element.getClassName())) {
				currentApp = element.getText();
				break;
			}
			report.report("about to click on element with class '" + element.getClassName() + "' of app '"+ currentApp +"'");
			uiautomatorClient.click(element.getX(), element.getY());
			
			if(uiautomatorClient.waitForExists(downloadSelector, 10000) ||
					uiautomatorClient.waitForExists(openSelector, 10000) ||
						uiautomatorClient.waitForExists(notCountrySelector, 10000)||
							uiautomatorClient.waitForExists(notSupporSelector, 10000)) {		
				report.report("succeeded to navigate to playstore by pressing element with class: " + element.getClassName());
				flowHtmlReport.addTitledImage("After click on element with class: " +element.getClassName()+ " in offrewall", adb.getScreenshotWithAdb(null));
				uiautomatorClient.pressKey("back");
				
			}else {
				throw new Exception("did not succeed navigation to playstore by pressing element with class: " + element.getClassName());
			}	
		}
	}
	
	private List<WebElement> getClickToMarketElements() throws Exception {
		
		List<String> elements = new ArrayList<String>();
		elements.add("inner_item");
		elements.add("cover-img-icon");
		elements.add("button_inner");
		elements.add("stars");
		elements.add("icon_inner");
		elements.add("title");
		
		List<WebElement> elementsToClick = new ArrayList<WebElement>();
		for (WebElement webElement : offerwallElements) {
			if(elements.contains(webElement.getClassName())) {
				elementsToClick.add(webElement);
			}
		}
		return elementsToClick;
	}
	
	@After
	public void tearDown() throws Exception{
		if (!isPass()){
			flowHtmlReport.addTitledImage("Failed Here", adb.getScreenshotWithAdb(null));
			report.report("screen flow", flowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
		}
		adb.clearLogcat();
		mobile.clearRecentApps();
	}
	//all methods that are candidates to move to an upper abstraction level. 
	private void captureWebview () throws Exception {
		
		report.report("launch MCTester App");
		robotiumClient.launch(MCTESTER_ACTIVITY);
		mobile.waitForManagerMessageToContain(MobileCoreMsgCode.OFFERWALL_MANAGER, "from:LOADING , to:READY_TO_SHOW" , 15000);
		Thread.sleep(2000);
		robotiumClient.clickOnButtonWithText("Show if ready");
		Thread.sleep(8000);
		
		report.report("gather all offerwall elements");
		offerwallElements = robotiumClient.getCurrentWebElements();
		if(offerwallElements.size() == 0) {
			throw new Exception("could not capture offerwall elements");
		} else {
			report.startLevel("Offerwall Elements");
			StringBuffer sb = new StringBuffer("OfferWall Elements").append("\n")
					.append("============================================").append("\n");
			for (WebElement element : offerwallElements) {
				sb.append(element.toString());
			}
			report.report(sb.toString());
			report.stopLevel();
		}
		report.report("about to close MCTester and Robotium Server");
		robotiumClient.finishOpenedActivities();
		robotiumClient.closeConnection();
		adb.stopActivity(MCTESTER_ACTIVITY);
	}
	
	/**
	 * 1. press 'home' button.
	 * 2. press 'apps' button.
	 * 3. swipe screens until MCTester is found.
	 * 4. click it.
	 * 
	 * @throws Exception
	 */
	@Deprecated //this method visually scroll the application pages and click on the app (unnecessary)
	private void launchMCtester() throws Exception {
		uiautomatorClient.pressKey("home");
		flowHtmlReport.addTitledImage("Before MCTester Launch", adb.getScreenshotWithAdb(null));
		uiautomatorClient.click(new Selector().setDescription("Apps").setClassName("android.widget.TextView"));
		Thread.sleep(1500);
			
		boolean found = false;
		Selector mcTesterSelector = new Selector().setText("MCTester").setClassName("android.widget.TextView"); 
		long now = System.currentTimeMillis();
		while(!found) {
			if (System.currentTimeMillis() - now > 15000) {
				throw new Exception("Did not found mcTester app after: 15 seconds millis");
			}
			if(uiautomatorClient.waitForExists(mcTesterSelector, 500)) {
				uiautomatorClient.click(mcTesterSelector);
				found = true;
			} else {
				uiautomatorClient.swipe(200, 200, 0, 200, 5);
			}	
		}
		if(!uiautomatorClient.waitForExists(new Selector().setText("Show (not force)"), 50000)) {
			throw new Exception("Application MCTester did not launched");
		} 
		
	}
	
	//The setters and getters are the way to expose parameters to test
	
	public int getLogcatReportTimeout() {
		return logcatReportTimeout;
	}

	@ParameterProperties(description = "timeout in milliseconds to wait for RS code")
	public void setLogcatReportTimeout(int logcatReportTimeout) {
		this.logcatReportTimeout = logcatReportTimeout;
	}
}
