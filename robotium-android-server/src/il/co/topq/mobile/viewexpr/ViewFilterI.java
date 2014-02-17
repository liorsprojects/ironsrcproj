package il.co.topq.mobile.viewexpr;

import android.view.View;

/**
 * 
 * 
 * @author Itai Agmon
 * 
 */
public interface ViewFilterI {

	/**
	 * 
	 * @param view
	 * @return true if the view is answers to the filter condition
	 */
	boolean valid(View view);

}
