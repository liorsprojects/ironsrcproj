package il.co.topq.mobile.server.impl;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;

import com.jayway.android.robotium.solo.Solo;

public class SoloUtils {
	
	public enum AXIS{X, Y}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static float convertRelativeToAbsolute(float toConvert, AXIS axis, Solo solo){
		Point size = new Point();
		Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
		float length;
		if (Build.VERSION.SDK_INT >=13){
			display.getSize(size);
		}
		else {
			size.set(display.getWidth(), display.getHeight());
		}
		if (axis==AXIS.X){
			length = size.x;
		}
		else {
			length = size.y;
		}
		
		return length * toConvert;
	}
	
}
