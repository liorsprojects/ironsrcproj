package il.co.topq.mobile.viewexpr;

/**
 * Signals that something went wrong while processing the expression.
 * 
 * @author Itai Agmon
 * 
 */
public class ViewExpressionException extends Exception {

	private static final long serialVersionUID = -4689460727045771268L;

	public ViewExpressionException(String message) {
		super(message);
	}

	public ViewExpressionException(String message, Throwable t) {
		super(message, t);
	}

}
