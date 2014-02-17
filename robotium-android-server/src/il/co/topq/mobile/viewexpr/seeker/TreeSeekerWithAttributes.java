package il.co.topq.mobile.viewexpr.seeker;

import il.co.topq.mobile.viewexpr.ViewFilterI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.view.View;
import android.widget.TextView;


/**
 * 
 * @author Itai Agmon
 * 
 */
public class TreeSeekerWithAttributes extends TreeSeekerImpl {
	private final Map<String, String> viewAttributes;

	public TreeSeekerWithAttributes(Class<?> clazz, boolean recursive, Map<String, String> viewAttributes) {
		super(clazz, recursive);
		this.viewAttributes = viewAttributes;
	}

	protected List<ViewFilterI> getFilters() {
		List<ViewFilterI> filters = new ArrayList<ViewFilterI>();
		filters.add(new ViewFilterI() {
			public boolean valid(View view) {
				if (view.getClass() == getClazz()) {
					return true;
				}
				return false;
			}
		});
		filters.addAll(getParametersFilters());
		return filters;
	}

	private List<ViewFilterI> getParametersFilters() {
		List<ViewFilterI> conditions = new ArrayList<ViewFilterI>();
		for (String param : viewAttributes.keySet()) {
			if (param.equals("id")) {
				final int id = Integer.parseInt(viewAttributes.get(param));
				conditions.add(new ViewFilterI() {
					public boolean valid(View view) {
						if (view.getId() == id) {
							return true;
						}
						return false;
					}
				});
			} else if (param.equals("tag")) {
				final String tag = viewAttributes.get(param);
				conditions.add(new ViewFilterI() {
					public boolean valid(View view) {
						if (view.getTag() != null && view.getTag().toString().equals(tag)) {
							return true;
						}
						return false;
					}
				});
			} else if (param.equals("text")) {
				final String text = viewAttributes.get(param);
				conditions.add(new ViewFilterI() {
					public boolean valid(View view) {
						if (view instanceof TextView && ((TextView) view).getText() != null
								&& ((TextView) view).getText().equals(text)) {
							return true;
						}
						return false;
					}
				});
			} else if (param.equals("content")) {
				final String text = viewAttributes.get(param);
				conditions.add(new ViewFilterI() {
					public boolean valid(View view) {
						if (view.getContentDescription() != null && view.getContentDescription().equals(text)) {
							return true;
						}
						return false;
					}
				});
			}

		}
		return conditions;
	}

}
