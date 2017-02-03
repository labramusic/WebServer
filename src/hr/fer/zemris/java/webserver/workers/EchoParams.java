package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.util.Map;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Worker which prints the given parameters in a table.
 * @author labramusic
 *
 */
public class EchoParams implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) {

		Map<String,String> params = context.getParameters();
		try {
			context.write("<html><body>");
			context.write("<h1>Defined parameters</h1>");
			context.write("<table border='1'>");
			for (Map.Entry<String, String> e : params.entrySet()) {
				context.write("<tr><td>");
				context.write(e.getKey());
				context.write("</td><td>");
				context.write(e.getValue());
				context.write("</td></tr>");
			}
			context.write("</body></html>");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

}
