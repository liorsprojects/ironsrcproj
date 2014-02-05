package com.ironsource.mobile.webview;

import il.co.topq.mobile.client.impl.WebElement;

import java.util.ArrayList;
import java.util.List;

public class InnerItemWebElement {


	private String appName = "undefined";
	private List<WebElement> innerElements;
	private WebElement itemWraper;
	
	public InnerItemWebElement() {
		innerElements = new ArrayList<WebElement>();
	}
	
	public WebElement getItemWraper() {
		return itemWraper;
	}

	public void setItemWraper(WebElement itemWraper) {
		this.itemWraper = itemWraper;
	}


	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<WebElement> getInnerElements() {
		return innerElements;
	}

	public void setInnerElements(List<WebElement> innerElements) {
		this.innerElements = innerElements;
	}
	
	public void addElement(WebElement element) {
		innerElements.add(element);
	}
	
	
	
	
}
