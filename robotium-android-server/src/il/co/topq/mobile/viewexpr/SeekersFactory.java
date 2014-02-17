package il.co.topq.mobile.viewexpr;

import il.co.topq.mobile.viewexpr.seeker.TreeSeekerI;
import il.co.topq.mobile.viewexpr.seeker.TreeSeekerImpl;
import il.co.topq.mobile.viewexpr.seeker.TreeSeekerWithAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewParent;

/**
 * Generate executable steps.
 * 
 * @author Itai Agmon
 * 
 */
public class SeekersFactory {

	/**
	 * Receives xpath style expression and generates an executable steps
	 * 
	 * @param expression
	 *            xpath style expression
	 * @return Executable steps
	 * @throws ViewExpressionException
	 */
	public List<TreeSeekerI> build(final String expression) throws ViewExpressionException {
		List<TreeSeekerI> seekers = new ArrayList<TreeSeekerI>();
		for (final String description : splitToSeekerDescriptions(expression)) {
			assertSyntax(description);
			if (isUp(description)) {
				seekers.add(new TreeSeekerI() {
					public List<View> perform(List<View> views) {
						List<View> result = new ArrayList<View>();
						for (View view : views) {
							if (view.getParent() != null) {
								ViewParent parent = view.getParent();
								for (View foundParent : result) {
									if (foundParent == parent) {
										continue;
									}
								}
								result.add((View) parent);
							}
						}
						return result;
					}
				});
			} else if (!hasAttributes(description)) {
				seekers.add(new TreeSeekerImpl(stringToClass(description), isRecusrsive(description)));

			} else if (hasAttributes(description)) {
				seekers.add(new TreeSeekerWithAttributes(stringToClass(description), isRecusrsive(description),
						getViewAttributes(description)));
			}
		}
		return seekers;
	}

	void assertSyntax(String description) {
		// TODO
	}

	/**
	 * If the expression has view attributes. For example 'view[@id='2']',
	 * return a map of the attributes.
	 * 
	 * @param description
	 *            Seeker description. For example '/view[@text='something']'
	 * @return Key is the name of the attribute and the value is the attribute
	 *         value.
	 */
	private Map<String, String> getViewAttributes(String description) {
		Map<String, String> parameters = new HashMap<String, String>();
		Pattern pattern = Pattern.compile("@(\\w+)=['\"]([\\w\\d]+)['\"]");
		Matcher matcher = pattern.matcher(description);
		while (matcher.find()) {
			parameters.put(matcher.group(1), matcher.group(2));
		}
		return parameters;
	}

	/**
	 * Do we need to go up in the components tree.
	 * 
	 * @param description
	 * @return
	 */
	private boolean isUp(String description) {
		return stepEquals(description, "..");
	}

	/**
	 * Is the view description includes attributes.
	 * 
	 * @param description
	 * @return
	 */
	private boolean hasAttributes(String description) {
		return description.matches(".+\\[.+\\]");
	}

	/**
	 * Do we need to search recursively in the tree.
	 * 
	 * @param description
	 * @return
	 */
	private boolean isRecusrsive(String description) {
		return description.startsWith("/");
	}

	Class<?> stringToClass(String description) throws ViewExpressionException {
		String classString = description.replace("/", "").replaceAll("\\[.+\\]", "");
		ClassLoader classLoader = SeekersFactory.class.getClassLoader();
		for (String classPackage : getClassPackages()) {
			try {
				return classLoader.loadClass(classPackage + "." + classString);
			} catch (Throwable t) {
				// Will try with another package
			}
		}
		throw new ViewExpressionException("Failed to find class " + classString);

	}

	String[] getClassPackages() {
		return new String[] { "android.view", "android.widget" };
	}

	@SuppressLint("DefaultLocale")
	private static boolean stepEquals(String step, String stepType) {
		return step.trim().toLowerCase().equals(stepType.toLowerCase());
	}

	static String[] splitToSeekerDescriptions(final String expression) {
		String[] descriptions = expression.split("(?<!/)/");
		return Arrays.copyOfRange(descriptions, 1, descriptions.length);
	}

}
