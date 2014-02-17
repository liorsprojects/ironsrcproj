package il.co.topq.mobile.viewexpr.seeker;

import il.co.topq.mobile.viewexpr.ViewFilterI;

import java.util.ArrayList;
import java.util.List;


import android.view.View;
import android.view.ViewGroup;

/**
 * Steps down in the tree.
 * 
 * @author Itai Agmon
 * 
 */
public class TreeSeekerImpl implements TreeSeekerI {

	private final Class<?> clazz;

	private boolean recursive;

	/**
	 * 
	 * @param clazz
	 *            Class to search in the tree.
	 * @param recursive
	 *            Search for the class recursively
	 */
	public TreeSeekerImpl(Class<?> clazz, boolean recursive) {
		super();
		this.clazz = clazz;
		this.recursive = recursive;
	}

	/**
	 * @see il.co.topq.mobile.viewexpr.seeker.TreeSeekerI
	 */
	public List<View> perform(List<View> views) {
		List<View> result = new ArrayList<View>();
		findViewes(views, getFilters(), result, recursive);
		return result;

	}

	/**
	 * Override this method if you want to add additional conditions for the
	 * selected view.
	 * 
	 * @return Array of additional conditions.
	 */
	protected List<ViewFilterI> getFilters() {
		List<ViewFilterI> conditions = new ArrayList<ViewFilterI>();
		conditions.add(new ViewFilterI() {
			public boolean valid(View view) {
				if (view.getClass() == getClazz()) {
					return true;
				}
				return false;
			}
		});
		return conditions;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	void findViewes(List<View> views, List<ViewFilterI> conditions, List<View> result, boolean recursive) {
		for (View view : views) {
			boolean valid = true;
			for (ViewFilterI condition : conditions) {
				valid &= condition.valid(view);
			}
			if (valid) {
				result.add(view);
			}
			if (recursive && view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
				ViewGroup group = (ViewGroup) view;
				List<View> childViews = new ArrayList<View>();
				for (int i = 0; i < group.getChildCount(); i++) {
					childViews.add(group.getChildAt(i));
				}
				findViewes(childViews, conditions, result, recursive);
			}
		}
	}

}
