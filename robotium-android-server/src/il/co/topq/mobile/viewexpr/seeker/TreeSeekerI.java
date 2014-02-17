package il.co.topq.mobile.viewexpr.seeker;

import java.util.List;

import android.view.View;

public interface TreeSeekerI {

	/**
	 * 
	 * @param views
	 *            View tree
	 * @return Views that corresponds to the step definition.
	 */
	List<View> perform(List<View> views);
}
