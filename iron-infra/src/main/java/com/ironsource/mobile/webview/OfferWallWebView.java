package com.ironsource.mobile.webview;

import il.co.topq.mobile.client.impl.WebElement;

import java.util.ArrayList;
import java.util.List;

public class OfferWallWebView {

	private WebElement closeButton;
	private List<WebElement> ownElements;
	private List<InnerItemWebElement> innerItemWebElements;
	
	public OfferWallWebView(List<WebElement> allWebElements) {
		ownElements = new ArrayList<WebElement>();
		innerItemWebElements = new ArrayList<InnerItemWebElement>();
		InnerItemWebElement tempInnerItem = null;
		for (WebElement webElement : allWebElements) {
			if("noThanks".equals(webElement.getId()) || "noThanksOrangeOffer".equals(webElement.getId())) {
				closeButton = webElement;
			} else if("inner_item".equals(webElement.getClassName())){
				if(tempInnerItem != null) {
					innerItemWebElements.add(tempInnerItem);
				}
				tempInnerItem = new InnerItemWebElement();
				tempInnerItem.setItemWraper(webElement);
				continue;
			}
			if(tempInnerItem != null) {
				if("title ellipsed-text".equals(webElement.getClassName())) {
					tempInnerItem.setAppName(webElement.getText());	
				}
				tempInnerItem.addElement(webElement);
			}
		}
		innerItemWebElements.add(tempInnerItem);
	}

	public WebElement getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(WebElement closeButton) {
		this.closeButton = closeButton;
	}

	public List<WebElement> getOwnElements() {
		return ownElements;
	}

	public void setOwnElements(List<WebElement> ownElements) {
		this.ownElements = ownElements;
	}

	public List<InnerItemWebElement> getInnerItemWebElementList() {
		return innerItemWebElements;
	}

	public void setInnerItemElementList(List<InnerItemWebElement> innerItemWebElements) {
		this.innerItemWebElements = innerItemWebElements;
	}
	
	public void addOwnItem(WebElement element) {
		ownElements.add(element);
	}
	
	public void addInnerItemElement(InnerItemWebElement element) {
		innerItemWebElements.add(element);
	}

}
