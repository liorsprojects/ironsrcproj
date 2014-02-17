package il.co.topq.mobile.viewexpr;

import il.co.topq.mobile.viewexpr.seeker.TreeSeekerI;

import java.util.ArrayList;
import java.util.List;


import android.view.View;

/**
 * 
 * @author Itai Agmon
 * 
 */
public class ViewExpressionInterperter {

	private final View root;

	private SeekersFactory factory;

	public ViewExpressionInterperter(View root) {
		this.root = root;
		factory = new SeekersFactory();
	}

	/**
	 * Evaluates Xpath style expression and returns all the Android views that
	 * Corresponds to the expression
	 * 
	 * @param expression
	 * @return list of views
	 * @throws ViewExpressionException
	 */
	public List<View> evaluate(String expression) throws ViewExpressionException {
		List<View> result = new ArrayList<View>();
		result.add(root);
		for (TreeSeekerI step : getFactory().build(expression)) {
			result = step.perform(result);
		}	
		return result;
	}

	/**
	 * Mostly for unit testing
	 * 
	 * @return
	 */
	SeekersFactory getFactory() {
		return factory;
	}

}
