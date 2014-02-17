package com.jayway.android.robotium.solo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * 
 * A Class that enables to override Solo services. For example, the
 * takeScreenshot
 * 
 * @author Itai.
 * 
 */
public class SoloEnhanced extends Solo {

	private static final int JPEG_QUALITY = 100;
	private final String LOG_TAG = "SoloEnhanced";

	public SoloEnhanced(Instrumentation instrumentation, Activity activity) {
		super(instrumentation, activity);
	}

	public SoloEnhanced(Instrumentation instrumentation) {
		super(instrumentation);
	}

	/**
	 * 
	 * @param blocking
	 *            This parameter is not used. We just had to make the method
	 *            signature different from the one in Solo.
	 * @return byte[] - representation of the JPEG screenshot or null if fails
	 *         to get the screenshot
	 */
	public byte[] takeScreenshot(boolean blocking) {
		int counter = 0;
		while (counter<10){
			try{
				View decorView = super.viewFetcher.getRecentDecorView(super.viewFetcher.getWindowDecorViews());
				Log.i("TcpServer", "take screenshot took: " + counter + " tries");
				return takeScreenshot(decorView);
			}
			catch(Throwable t){
				Log.e("TcpServer", t.getMessage(), t);
			}
			counter++;
		}
		return null;
	}

	public byte[] takeScreenshot(final View view) {
		if (view != null) {
			Bitmap b;

			if (view instanceof WebView) {
				b = getBitmapOfWebView((WebView) view);
			} else {
				b = getBitmapOfView(view);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			b = Bitmap.createScaledBitmap(b, b.getWidth() /2, b.getHeight() /2 , false);
			try {
				if (b.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, bos) == false) {
					Log.d(LOG_TAG, "Compress/Write failed");
					return null;
				}
				return bos.toByteArray();

			} finally {
				view.destroyDrawingCache();
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException e) {
						Log.d(LOG_TAG, "Closing byteArrayOutputStream failed");
					}
				}

			}

		}
		return null;

	}

	private Bitmap getBitmapOfWebView(final WebView webView) {
		Picture picture = webView.capturePicture();
		Bitmap b = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		picture.draw(c);
		return b;
	}

	private Bitmap getBitmapOfView(final View view) {
		GetBitmap getBitmap = new GetBitmap(view);
		
		this.instrumentation.runOnMainSync(getBitmap);
		instrumentation.waitForIdleSync();
		return getBitmap.getBitMap();
	}
	
	private class GetBitmap implements Runnable{

		private View view;
		
		public GetBitmap(View view){
			this.view = view;
		}
		
		private Bitmap bmp;
		
		@Override
		public void run() {
			view.destroyDrawingCache();
			view.buildDrawingCache(false);
			bmp = view.getDrawingCache();
		}
		
		public Bitmap getBitMap(){
			return this.bmp;
		}
		
	}

}
