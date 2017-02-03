package hr.fer.zemris.java.webserver;

/**
 * Interface which allows processing request from any object.
 * 
 * @author labramusic
 *
 */
public interface IWebWorker {

	/**
	 * Processes the current request and creates content for the client.
	 * 
	 * @param context
	 *            current request
	 */
	public void processRequest(RequestContext context);

}
