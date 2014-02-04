package il.co.topq.mobile.client.impl;

public class WebElement {

	private String tag;
	private String id;
	private String className;
	private String text;
	private int x;
	private int y;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("==WebElemnt==\n");
		sb.append("tag: " + tag).append("\n");
		sb.append("class: " + className).append("\n");
		sb.append("id: " + id).append("\n");
		sb.append("text: " + text).append("\n");
		sb.append("x: " + x).append("\n");
		sb.append("y: " + y).append("\n").append("===========\n");
		return sb.toString();
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
}
