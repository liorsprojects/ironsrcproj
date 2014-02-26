package com.ironsource.mobile.test;

import il.co.topq.mobile.client.impl.MobileClient;
import il.co.topq.mobile.client.impl.WebElement;
import il.co.topq.mobile.common.datamodel.CommandResponse;

import java.util.List;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.topq.uiautomator.AutomatorService;
import org.topq.uiautomator.Selector;

import com.ironsource.mobile.ADBConnection;
import com.ironsource.mobile.MobileSO;
import com.ironsource.mobile.enums.FlowCode;
import com.ironsource.mobile.enums.MobileCoreMsgCode;
import com.ironsource.mobile.enums.PlayStoreMessage;
import com.ironsource.mobile.enums.RSCode;
import com.ironsource.mobile.fiddler.FiddlerApi;
import com.ironsource.mobile.fiddler.FiddlerJsonRpcClient;
import com.ironsource.mobile.reporters.ImageFlowHtmlReport;
import com.ironsource.mobile.webview.InnerItemWebElement;
import com.ironsource.mobile.webview.OfferWallWebView;

public class MCTesterTests extends SystemTestCase4 {

	private static MobileSO mobile;
	private static ADBConnection adb;
	private static AutomatorService uiautomatorClient;
	private static MobileClient robotiumClient;
	private static OfferWallWebView offerWallWebView;
	private static FiddlerJsonRpcClient fiddlerJsonRpcClient; 
	private ImageFlowHtmlReport flowHtmlReport;
	
	public static final String ROBOTIUM_MCTESTER_PACKAGE = "com.mobilecore.mctester";
	public static final String ROBOTIUM_MCTESTER_ACTIVITY = "com.mobilecore.mctester.MainActivity";


	private int logcatReportTimeout = 30000;
	private boolean landscapeMode = false;
	private boolean removeInstalledApp = true;
	private long waitForRsPlus = 150000;
	
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
		
		if(adb != null) {
			report.report("clearing logcat");
			adb.clearLogcat();
		}
		flowHtmlReport = new ImageFlowHtmlReport();
		
		adb.clearLogcatMessageRecorder();
		
		//TODO - next version
//		if(landscapeMode) {
//			uiautomatorClient.setOrientation("l");
//		}
	    
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
	@TestProperties(name = "capture test template", paramsInclude = {"landscapeMode"})
	public void testSetupTestsTemplate() throws Exception {
			
		report.report("retrieving robotium client");
		robotiumClient = (MobileClient) mobile.getRobotiumClient();
		//String res = (String) fiddlerJsonRpcClient.execute(FiddlerApi.setOfferwallJsonPath("offerwall", "C:\\\\Users\\\\lior_g\\\\ow.json"));
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
		
		report.report("about to click on close button");
		clickOfferwallCloseButton();
		
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
	@TestProperties(name = "full click to download flow", paramsInclude = {"waitForRsPlus", "logcatReportTimeout", "removeInstalledApp"})
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
		mobile.waitForRSCode(RSCode.WALL, FlowCode.OFFERWALL, logcatReportTimeout);
		mobile.waitForRSCode(RSCode.IMPRESSION, FlowCode.OFFERWALL, logcatReportTimeout);
		
		flowHtmlReport.addTitledImage("Clicked on 'Show (not force)'", adb.getScreenshotWithAdb(null));
		Thread.sleep(2000);
		
		List<InnerItemWebElement> innerItems = offerWallWebView.getInnerItemWebElementList();
		Selector openSelector = new Selector().setText("OPEN");
		Selector notCountrySelector = new Selector().setText(PlayStoreMessage.COUNTRY_SUPPORT);
		Selector notSupporSelector = new Selector().setText(PlayStoreMessage.DEVICE_COMPATIBLE);
		
		String tempAppName = "not specified correctly";
		boolean foundValidInstall = false;
		for (InnerItemWebElement innerItemWebElement : innerItems) {
			tempAppName = innerItemWebElement.getAppName();
			report.step("clicking on item with title: " + innerItemWebElement.getAppName());
			uiautomatorClient.click(innerItemWebElement.getItemWraper().getX(), innerItemWebElement.getItemWraper().getX());
			mobile.waitForRSCode(RSCode.CLICK, FlowCode.OFFERWALL, 10000);
			flowHtmlReport.addTitledImage("After click on application in offrewall", adb.getScreenshotWithAdb(null));
			report.step("waiting for playstore");
		
			if (!uiautomatorClient.waitForExists(new Selector().setText("INSTALL"), 5000)) {
				flowHtmlReport.addTitledImage("Playstore for " + innerItemWebElement.getAppName(), adb.getScreenshotWithAdb(null));
				if(uiautomatorClient.exist(openSelector)) {
					report.report("the app: " + innerItemWebElement.getAppName() + "is allready installed");
				} else if (uiautomatorClient.exist(notCountrySelector)) {
					report.report("the app: " + innerItemWebElement.getAppName() + "is not suppoorted in our country");
				} else if (uiautomatorClient.exist(notSupporSelector)) {
					report.report("the app: " + innerItemWebElement.getAppName() + "is incompatible with yor device");
				} else {
					throw new Exception("Did not navigated to Playstore (check internet connection or proxy settings)");
				}
				report.report("about to try again for diffrent app");
				uiautomatorClient.pressKey("back");
				continue;
			} 
			foundValidInstall = true;
			flowHtmlReport.addTitledImage("valid Playstore with INSTALL button", adb.getScreenshotWithAdb(null));
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
				report.report("Did not finish downloading after 10 minutes", Reporter.WARNING);
			}  
			report.step("Install Completed");
			Thread.sleep(2000);
			flowHtmlReport.addTitledImage("App Installed", adb.getScreenshotWithAdb(null));
			
			try{
				mobile.waitForRSCode(RSCode.INSATLL, FlowCode.OFFERWALL, waitForRsPlus);
			} catch (Exception e) {
				report.report("rs code '+' wasn't reported after " + waitForRsPlus + " millisecond", Reporter.WARNING);
			}
			if(removeInstalledApp) {
				report.report("about to uninstall " + tempAppName);
				uiautomatorClient.click(new Selector().setText("UNINSTALL"));
				
				uiautomatorClient.registerClickUiObjectWatcher("uninstall", new Selector[]{new Selector().setText("Do you want to uninstall this app?")}, new Selector().setText("OK"));
				if (!uiautomatorClient.waitForExists(new Selector().setText("INSTALL"), 120000)) {
					report.report("uninstall didnt complete after 2 minutes", Reporter.WARNING);
				} 
			}
			break;
		}
		if(!foundValidInstall) {
			report.report("did not find valid installation in offerwall" ,Reporter.WARNING);
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
		
		List<InnerItemWebElement> innerItemWebElements = offerWallWebView.getInnerItemWebElementList();
		for (InnerItemWebElement innerItemWebElement : innerItemWebElements) {
			List<WebElement> clickElements = innerItemWebElement.getInnerElements();
			for (WebElement webElement : clickElements) {
				String elementId = "";
				String elementClass = "";
				if(!webElement.getClassName().isEmpty()) {
					elementClass = "class='" + webElement.getClassName() + "' ";
				}
				if(!webElement.getId().isEmpty()) {
					elementId = "id='" + webElement.getId() + "'";
				}
				String reportMsg = "about to click on element with "+ elementClass + elementId + " of app '"+ innerItemWebElement.getAppName() +"'";
				flowHtmlReport.addTitledImage(reportMsg, adb.getScreenshotWithAdb(null));
				report.report(reportMsg);
				uiautomatorClient.click(webElement.getX(), webElement.getY());
				if(uiautomatorClient.waitForExists(new Selector().setTextContains(innerItemWebElement.getAppName()).setPackageName("com.android.vending"), 5000)) {
					report.report("navigation to playstore secceeded after pressing element with " + elementClass + elementId);
				} else {
					report.report("navigation to playstore did not secceed after pressing element with " + elementClass + elementId, Reporter.FAIL);
				}	
				flowHtmlReport.addTitledImage("After click on element with class: " +webElement.getClassName()+ " in offrewall", adb.getScreenshotWithAdb(null));
				uiautomatorClient.pressKey("back");
			}
		}
		report.report("screen flow", flowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
	}
	
	
	@After
	public void tearDown() throws Exception{
		if (!isPass()){
			flowHtmlReport.addTitledImage("Failed Here", adb.getScreenshotWithAdb(null));
			report.report("screen flow", flowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
		}
		adb.clearLogcat();
		//TODO - next version
		//uiautomatorClient.freezeRotation(false);
		mobile.clearRecentApps();
	}
	
	//all methods that are candidates to move to an upper abstraction level. 
	private void captureWebview () throws Exception {
		
		report.report("launch MCTester App");
		robotiumClient.launch(ROBOTIUM_MCTESTER_ACTIVITY);
		mobile.waitForManagerMessageToContain(MobileCoreMsgCode.OFFERWALL_MANAGER, "from:LOADING , to:READY_TO_SHOW" , 15000);
		Thread.sleep(4000);
		CommandResponse res = robotiumClient.clickOnButtonWithText("Show if ready");
		Thread.sleep(8000);
		
		report.report("gather all offerwall elements");
		List<WebElement> offerwallElements = robotiumClient.getCurrentWebElements();
		if(offerwallElements.size() == 0) {
			throw new Exception("could not capture offerwall elements");
		} else {
			offerWallWebView = new OfferWallWebView(offerwallElements);
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
		adb.stopActivity(ROBOTIUM_MCTESTER_ACTIVITY);
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
	
	private void clickOfferwallCloseButton() {
		WebElement closeElement = offerWallWebView.getCloseButton();
		if(closeElement != null) {
			uiautomatorClient.click(closeElement.getX(), closeElement.getY());
		} else {
			report.report("close button didn't set properly", Reporter.FAIL);
		}
	}
	
	
	//The setters and getters are the way to expose parameters to test
	
	public int getLogcatReportTimeout() {
		return logcatReportTimeout;
	}

	@ParameterProperties(description = "default timeout in milliseconds to wait for RS code report")
	public void setLogcatReportTimeout(int logcatReportTimeout) {
		this.logcatReportTimeout = logcatReportTimeout;
	}
	
	public boolean isLandscapeMode() {
		return landscapeMode;
	}

	@ParameterProperties(description = "landscape mode")
	public void setLandscapeMode(boolean landscapeMode) {
		this.landscapeMode = landscapeMode;
	}
	
	public boolean isRemoveInstalledApp() {
		return removeInstalledApp;
	}

	@ParameterProperties(description = "remove apps that successfuly installed")
	public void setRemoveInstalledApp(boolean removeInstalledApp) {
		this.removeInstalledApp = removeInstalledApp;
	}

	public long getWaitForRsPlus() {
		return waitForRsPlus;
	}

	@ParameterProperties(description = "how long to wait for the rs '+' report in millisecond")
	public void setWaitForRsPlus(long waitForRsPlus) {
		this.waitForRsPlus = waitForRsPlus;
	}
}
