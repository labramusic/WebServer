package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The Class RequestContext.
 */
public class RequestContext {

	/**
	 * The output stream.
	 */
	private OutputStream outputStream;

	/**
	 * The charset.
	 */
	private Charset charset;

	/**
	 * The encoding.
	 */
	private String encoding;

	/**
	 * The status code.
	 */
	private int statusCode;

	/**
	 * The status text.
	 */
	private String statusText;

	/**
	 * The mime type.
	 */
	private String mimeType;

	/**
	 * The parameters map.
	 */
	private Map<String, String> parameters;

	/**
	 * The temporary parameters map.
	 */
	private Map<String, String> temporaryParameters;

	/**
	 * The persistent parameters map.
	 */
	private Map<String, String> persistentParameters;

	/**
	 * The output cookies.
	 */
	private List<RCCookie> outputCookies;

	/**
	 * Determines if header has been generated.
	 */
	private boolean headerGenerated;

	/**
	 * The default encoding.
	 */
	private final static String DEFAULT_ENCODING = "UTF-8";

	/**
	 * The default status code.
	 */
	private final static int DEFAULT_STATUS_CODE = 200;

	/**
	 * The default status text.
	 */
	private final static String DEFAULT_STATUS_TEXT = "OK";

	/**
	 * The default mime type.
	 */
	private final static String DEFAULT_MIME_TYPE = "text/html";

	/**
	 * The default buffer size.
	 */
	private final static int BUFFER_SIZE = 4096;

	/**
	 * Initializes a request context.
	 * 
	 * @param outputStream
	 *            the output stream
	 * @param parameters
	 *            parameters
	 * @param persistentParameters
	 *            persistent parameters
	 * @param outputCookies
	 *            output cookies
	 */
	public RequestContext(OutputStream outputStream, Map<String, String> parameters,
			Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
		if (outputStream == null) {
			throw new IllegalArgumentException("Output stream must not be null!");
		}
		this.outputStream = outputStream;
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		this.parameters = parameters;
		if (persistentParameters == null) {
			persistentParameters = new HashMap<>();
		}
		this.persistentParameters = persistentParameters;
		if (outputCookies == null) {
			outputCookies = new ArrayList<>();
		}
		this.outputCookies = outputCookies;
		this.encoding = DEFAULT_ENCODING;
		this.statusCode = DEFAULT_STATUS_CODE;
		this.statusText = DEFAULT_STATUS_TEXT;
		this.mimeType = DEFAULT_MIME_TYPE;
	}

	/**
	 * Gets the output stream.
	 *
	 * @return the output stream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Sets the encoding.
	 *
	 * @param encoding
	 *            the new encoding
	 */
	public void setEncoding(String encoding) {
		if (headerGenerated) {
			throw new RuntimeException("Encoding cannot be changed after header has been generated!");
		}
		this.encoding = encoding;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode
	 *            the new status code
	 */
	public void setStatusCode(int statusCode) {
		if (headerGenerated) {
			throw new RuntimeException("Status code cannot be changed after header has been generated!");
		}
		this.statusCode = statusCode;
	}

	/**
	 * Sets the status text.
	 *
	 * @param statusText
	 *            the new status text
	 */
	public void setStatusText(String statusText) {
		if (headerGenerated) {
			throw new RuntimeException("Status text cannot be changed after header has been generated!");
		}
		this.statusText = statusText;
	}

	/**
	 * Sets the mime type.
	 *
	 * @param mimeType
	 *            the new mime type
	 */
	public void setMimeType(String mimeType) {
		if (headerGenerated) {
			throw new RuntimeException("Mime type cannot be changed after header has been generated!");
		}
		this.mimeType = mimeType;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Gets the temporary parameters.
	 *
	 * @return the temporary parameters
	 */
	public Map<String, String> getTemporaryParameters() {
		return temporaryParameters;
	}

	/**
	 * Sets the temporary parameters.
	 *
	 * @param temporaryParameters
	 *            the temporary parameters
	 */
	public void setTemporaryParameters(Map<String, String> temporaryParameters) {
		this.temporaryParameters = temporaryParameters;
	}

	/**
	 * Gets the persistent parameters.
	 *
	 * @return the persistent parameters
	 */
	public Map<String, String> getPersistentParameters() {
		return persistentParameters;
	}

	/**
	 * Sets the persistent parameters.
	 *
	 * @param persistentParameters
	 *            the persistent parameters
	 */
	public void setPersistentParameters(Map<String, String> persistentParameters) {
		this.persistentParameters = persistentParameters;
	}

	/**
	 * Retrieves value from parameters map or null if mapping doesn't exist.
	 * 
	 * @param name
	 *            parameter name
	 * @return value
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * Retrieves names of all parameters in the parameters map. The returned set
	 * is unmodifiable.
	 * 
	 * @return names of all parameters
	 */
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}

	/**
	 * Retrieves value from persistent parameters map or null if mapping doesn't
	 * exist.
	 * 
	 * @param name
	 *            parameter name
	 * @return value
	 */
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}

	/**
	 * Retrieves names of all parameters in the persistent parameters map. The
	 * returned set is unmodifiable.
	 * 
	 * @return names of all parameters
	 */
	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}

	/**
	 * Stores a value to the persistent parameters map.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            value
	 */
	public void setPersistentParameter(String name, String value) {
		Objects.requireNonNull(name);
		persistentParameters.put(name, value);
	}

	/**
	 * Removes a value from the persistent parameters map.
	 * 
	 * @param name
	 *            parameter name
	 */
	public void removePersistentParameter(String name) {
		Objects.requireNonNull(name);
		persistentParameters.remove(name);
	}

	/**
	 * Retrieves value from the temporary parameters map or null if mapping
	 * doesn't exist.
	 * 
	 * @param name
	 *            parameter name
	 * @return value
	 */
	public String getTemporaryParameter(String name) {
		Objects.requireNonNull(name);
		return temporaryParameters.get(name);
	}

	/**
	 * Retrieves names of all parameters in the temporary parameters map. The
	 * returned set is unmodifiable.
	 * 
	 * @return names of all parameters
	 */
	public Set<String> getTemporaryParameterNames() {
		if (temporaryParameters == null) {
			temporaryParameters = new HashMap<>();
		}
		return Collections.unmodifiableSet(temporaryParameters.keySet());
	}

	/**
	 * Stores a value to the temporary parameters map.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            value
	 */
	public void setTemporaryParameter(String name, String value) {
		Objects.requireNonNull(name);
		if (temporaryParameters == null) {
			temporaryParameters = new HashMap<>();
		}
		temporaryParameters.put(name, value);
	}

	/**
	 * Removes a value from the temporary parameters map.
	 * 
	 * @param name
	 *            parameter name
	 */
	public void removeTemporaryParameter(String name) {
		Objects.requireNonNull(name);
		temporaryParameters.remove(name);
	}

	/**
	 * Writes the given byte array to the output stream. The header is generated
	 * first.
	 * 
	 * @param data
	 *            data
	 * @return the request context with written data
	 * @throws IOException
	 *             thrown if I/O exception occurs
	 */
	public RequestContext write(byte[] data) throws IOException {
		if (!headerGenerated) {
			// write header using encoding, statusCode, statusText,
			// mimeType and outputCookies
			charset = Charset.forName(encoding);
			byte[] header = generateHeader().getBytes(StandardCharsets.ISO_8859_1);
			writeDataToOutputStream(header);
			headerGenerated = true;
		}
		writeDataToOutputStream(data);
		return this;
	}

	/**
	 * Generates the header.
	 * 
	 * @return the header
	 */
	private String generateHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
		sb.append("Content-Type: " + mimeType);
		if (mimeType.startsWith("text/")) {
			sb.append("; charset=" + encoding);
		}
		sb.append("\r\n");
		if (!outputCookies.isEmpty()) {
			for (RCCookie cookie : outputCookies) {
				sb.append("Set-Cookie: " + cookie.name + "=\"" + cookie.value + "\"");
				if (cookie.domain != null) {
					sb.append("; Domain=" + cookie.domain);
				}
				if (cookie.path != null) {
					sb.append("; Path=" + cookie.path);
				}
				if (cookie.maxAge != null) {
					sb.append("; Max-Age=" + cookie.maxAge);
				}
				if (cookie.httpOnly) {
					sb.append("; HttpOnly");
				}
				sb.append("\r\n");
			}
		}
		sb.append("\r\n");
		return sb.toString();
	}

	/**
	 * Writes the given byte array to the output stream.
	 * 
	 * @param data
	 *            data
	 * @throws IOException
	 *             thrown if I/O exception occurs
	 */
	private void writeDataToOutputStream(byte[] data) throws IOException {
		try (InputStream is = new BufferedInputStream(new ByteArrayInputStream(data))) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int r;
			while ((r = is.read(buffer)) >= 1) {
				outputStream.write(buffer, 0, r);
			}
		}
	}

	/**
	 * Writes the given string to the output stream.
	 *
	 * @param text
	 *            text string
	 * @return the request context
	 * @throws IOException
	 *             thrown if I/O exception occurs
	 */
	public RequestContext write(String text) throws IOException {
		charset = Charset.forName(encoding);
		byte[] data = text.getBytes(charset);
		return write(data);
	}

	/**
	 * Adds the rcCookie to the cookies list.
	 * 
	 * @param rcCookie
	 *            cookie
	 */
	public void addRCCookie(RCCookie rcCookie) {
		outputCookies.add(rcCookie);
	}

	/**
	 * Models a http cookie.
	 * 
	 * @author labramusic
	 *
	 */
	public static class RCCookie {

		/**
		 * Parameter name.
		 */
		private String name;

		/**
		 * Parameter value.
		 */
		private String value;

		/**
		 * The domain.
		 */
		private String domain;

		/**
		 * The path.
		 */
		private String path;

		/**
		 * Cookie duration.
		 */
		private Integer maxAge;

		/**
		 * True if cookie is http only.
		 */
		private boolean httpOnly;

		/**
		 * Instantiates a new RCCookie.
		 * 
		 * @param name
		 *            parameter name
		 * @param value
		 *            parameter value
		 * @param domain
		 *            the domain
		 * @param path
		 *            the path
		 * @param maxAge
		 *            cookie duration
		 */
		public RCCookie(String name, String value, String domain, String path, Integer maxAge) {
			this.name = name;
			this.value = value;
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Gets the domain.
		 *
		 * @return the domain
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * Gets the path.
		 *
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Gets the max age.
		 *
		 * @return the max age
		 */
		public Integer getMaxAge() {
			return maxAge;
		}

		/**
		 * Sets the http only.
		 *
		 * @param httpOnly
		 *            the new http only
		 */
		public void setHttpOnly(boolean httpOnly) {
			this.httpOnly = httpOnly;
		}

	}

}
