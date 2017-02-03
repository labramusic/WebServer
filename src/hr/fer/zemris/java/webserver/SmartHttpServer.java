package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.util.Util;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * A Http server which can run smart scripts, workers from the predefined
 * package, .text, .html and .png image files on the client browser. The program
 * accepts the path of the server configuration file. The server is started
 * after loading the properties, and can be stopped at any time by the user.
 * 
 * @author labramusic
 *
 */
public class SmartHttpServer {

	/**
	 * The package where the workers are placed.
	 */
	final static String PACKAGE = "hr.fer.zemris.java.webserver.workers";

	/**
	 * The host address.
	 */
	private String address;

	/**
	 * The port of the host.
	 */
	private int port;

	/**
	 * Number of worker threads.
	 */
	private int workerThreads;

	/**
	 * The time of the session timeout.
	 */
	private int sessionTimeout;

	/**
	 * Map of mime types.
	 */
	private Map<String, String> mimeTypes = new HashMap<>();

	/**
	 * The server thread.
	 */
	private ServerThread serverThread;

	/**
	 * The refresher thread.
	 */
	private RefresherThread refresherThread;

	/**
	 * The thread pool.
	 */
	private ExecutorService threadPool;

	/**
	 * The document root path.
	 */
	private Path documentRoot;

	/**
	 * The workers map.
	 */
	private Map<String, IWebWorker> workersMap = new HashMap<>();

	/**
	 * The sessions map.
	 */
	private Map<String, SessionMapEntry> sessions = new HashMap<>();

	/**
	 * The random session sid generator.
	 */
	private Random sessionRandom = new Random();

	/**
	 * The main method.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Expected main properties file as argument.");
			return;
		}

		SmartHttpServer server = new SmartHttpServer(args[0]);
		server.start();
		System.out.println("Type \"stop\" to stop the server.");
		Scanner sc = new Scanner(System.in);
		while (true) {
			String line = sc.nextLine();
			if (line.equalsIgnoreCase("stop")) {
				server.stop();
				break;
			}
		}
		sc.close();
		System.out.println("Server has been shutdown.");
	}

	/**
	 * Initializes a SmartHttpServer.
	 * 
	 * @param configFileName
	 *            configurations filename
	 */
	public SmartHttpServer(String configFileName) {
		Properties properties = new Properties();

		try {
			properties.load(Files.newInputStream(Paths.get(configFileName)));
		} catch (IOException e) {
			System.err.println("Error while reading configuration file: " + e);
			System.exit(1);
		}
		getProperties(properties);
	}

	/**
	 * Loads the properties from the given properties file.
	 * 
	 * @param properties
	 *            the properties
	 */
	private void getProperties(Properties properties) {
		address = properties.getProperty("server.address");
		Objects.requireNonNull(address);
		port = Util.getAsInt(properties.getProperty("server.port"));
		workerThreads = Util.getAsInt(properties.getProperty("server.workerThreads"));
		String document = properties.getProperty("server.documentRoot");
		Objects.requireNonNull(document);
		documentRoot = Paths.get(document);
		sessionTimeout = Util.getAsInt(properties.getProperty("session.timeout"));

		String mime = properties.getProperty("server.mimeConfig");
		Objects.requireNonNull(mime);
		Path mimePath = Paths.get(mime);
		getMimeProperties(mimePath);

		String workers = properties.getProperty("server.workers");
		Objects.requireNonNull(workers);
		Path workersPath = Paths.get(workers);
		getWorkers(workersPath);
	}

	/**
	 * Loads the mime properties from the given path.
	 * 
	 * @param mimePath
	 *            mime path
	 */
	private void getMimeProperties(Path mimePath) {
		Properties mimeProperties = new Properties();
		try {
			mimeProperties.load(Files.newInputStream(mimePath));
		} catch (IOException e) {
			System.err.println("Error while reading configuration file: " + e);
			System.exit(1);
		}

		for (Map.Entry<Object, Object> entry : mimeProperties.entrySet()) {
			mimeTypes.put(entry.getKey().toString(), entry.getValue().toString());
		}
	}

	/**
	 * Loads the workers from the workers path.
	 * 
	 * @param workersPath
	 *            the workers path
	 */
	private void getWorkers(Path workersPath) {
		try (Scanner sc = new Scanner(workersPath)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (line == null || line.isEmpty() || line.startsWith("#"))
					continue;
				String[] lineArgs = line.split("=");
				if (lineArgs.length != 2)
					continue;
				String path = lineArgs[0].trim();
				path = Paths.get(documentRoot + path).toString();
				String fqcn = lineArgs[1].trim();

				IWebWorker iww = getWorkerFromName(fqcn);
				if (workersMap.containsKey(path)) {
					throw new RuntimeException("Path " + path + "already contains class " + workersMap.get(path) + "!");
				}

				workersMap.put(path, iww);
			}

		} catch (IOException e) {
			System.err.println("Couldn't read workers configuration file!");
			System.exit(1);
		}
	}

	/**
	 * Starts the server and refresher threads if not already running.
	 */
	protected synchronized void start() {
		// initialize thread pool
		threadPool = Executors.newFixedThreadPool(workerThreads);

		if (serverThread == null) {
			serverThread = new ServerThread();
			serverThread.setDaemon(true);
		}
		if (refresherThread == null) {
			refresherThread = new RefresherThread();
			refresherThread.setDaemon(true);
		}
		// start server thread if not already running
		if (!serverThread.isAlive()) {
			serverThread.start();
		}
		if (!refresherThread.isAlive()) {
			refresherThread.start();
		}
	}

	/**
	 * Stops the server.
	 */
	protected synchronized void stop() {
		// signal server thread to stop running
		serverThread.kill();
		refresherThread.kill();
		// shutdown threadpool
		threadPool.shutdown();
	}

	/**
	 * The server thread submits new client workers as long as it's alive.
	 * 
	 * @author labramusic
	 *
	 */
	protected class ServerThread extends Thread {

		/**
		 * True if thread needs to stop.
		 */
		private boolean dead;

		@Override
		public void run() {
			dead = false;
			try {
				// open serverSocket on specified port
				ServerSocket serverSocket = new ServerSocket(port);

				while (true) {
					if (dead)
						break;
					Socket client = serverSocket.accept();
					ClientWorker cw = new ClientWorker(client);
					// submit cw to threadpool for execution
					threadPool.submit(cw);
				}
				serverSocket.close();

			} catch (IOException e) {
				System.err.println("Cannot open socket on port " + port);
				System.exit(1);
			}
		}

		/**
		 * Sets the thread as dead.
		 */
		public void kill() {
			dead = true;
		}
	}

	/**
	 * Thread which refreshes sessions each 5 minutes by removing expired ones.
	 * 
	 * @author labramusic
	 *
	 */
	protected class RefresherThread extends Thread {

		/**
		 * True if thread needs to stop.
		 */
		private boolean dead;

		@Override
		public void run() {
			dead = false;
			// periodically (e.g. each 5 minutes) go through all session records
			// and that will remove records for expired sessions from sessions
			// map.
			try {
				while (true) {
					if (dead)
						break;
					Thread.sleep(1000 * 60 * 5);
					for (Map.Entry<String, SessionMapEntry> s : sessions.entrySet()) {
						if (s.getValue().validUntil < new Date().getTime() / 1000) {
							sessions.remove(s.getKey());
						}
					}
				}
			} catch (InterruptedException e) {
				System.err.println("The refreshing thread was interrupted!");
			}

		}

		/**
		 * Sets the thread dead.
		 */
		public void kill() {
			dead = true;
		}
	}

	/**
	 * Represents the client on this server.
	 * 
	 * @author labramusic
	 *
	 */
	private class ClientWorker implements Runnable {

		/**
		 * The client socket.
		 */
		private Socket csocket;

		/**
		 * Client input stream.
		 */
		private PushbackInputStream istream;

		/**
		 * Client output stream.
		 */
		private OutputStream ostream;

		/**
		 * Version of this request.
		 */
		private String version;

		/**
		 * The method used in this request.
		 */
		private String method;

		/**
		 * The parameters map.
		 */
		private Map<String, String> params = new LinkedHashMap<String, String>();

		/**
		 * The permanent parameters map.
		 */
		private Map<String, String> permPrams = null;

		/**
		 * The output cookies list.
		 */
		private List<RCCookie> outputCookies = new ArrayList<>();

		/**
		 * Initializes a ClientWorker.
		 * 
		 * @param csocket
		 *            client socket
		 */
		public ClientWorker(Socket csocket) {
			this.csocket = csocket;
		}

		@Override
		public void run() {

			try {
				istream = new PushbackInputStream(csocket.getInputStream());
				ostream = csocket.getOutputStream();
			} catch (IOException e) {
				System.err.println("The socket's streams couldn't be reached.");
				System.exit(1);
			}
			List<String> request = readRequest();
			String firstLine = request.get(0);
			String[] firstArgs = firstLine.split(" ");
			if (firstArgs.length != 3 || !firstArgs[0].equals("GET")
					|| (!firstArgs[2].equals("HTTP/1.0") && !firstArgs[2].equals("HTTP/1.1"))) {
				sendError(400, "Bad Request");
				return;
			}
			method = firstArgs[0];
			String requestedPath = firstArgs[1];
			version = firstArgs[2];

			checkSession(request);

			String path = requestedPath;
			String paramString;
			if (requestedPath.contains("?")) {
				String[] reqPathSplit = requestedPath.split("\\?");
				path = reqPathSplit[0];
				paramString = reqPathSplit[1];
				parseParameters(paramString);
			}
			requestedPath = Paths.get(documentRoot + path).toString();

			RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
			rc.setStatusCode(200);

			if (path.startsWith("/ext/")) {
				String name = Paths.get(path).getFileName().toString();
				getWorkerFromName(PACKAGE + "." + name).processRequest(rc);
			} else if (workersMap.get(requestedPath) != null) {
				workersMap.get(requestedPath).processRequest(rc);
			} else {
				sendRequestToClient(rc, requestedPath);
			}

			try {
				csocket.close();
			} catch (IOException e) {
				System.err.println("The socket couldn't be closed!");
			}
		}

		/**
		 * Sends the request context to the client.
		 * 
		 * @param rc
		 *            request context
		 * @param requestedPath
		 *            requested client path
		 */
		private void sendRequestToClient(RequestContext rc, String requestedPath) {
			Path filePath = Paths.get(requestedPath);
			if (!Files.exists(filePath) || !Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
				sendError(404, "Not Found");
				return;
			}

			String fileExt = requestedPath.split("\\.")[1];
			String mimeType = mimeTypes.get(fileExt);

			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}

			if (fileExt.equals("smscr")) {
				DocumentNode documentNode = getDocumentNode(requestedPath);
				new SmartScriptEngine(documentNode, rc).execute();
			} else {
				rc.setMimeType(mimeType);
				try {
					byte[] data = Files.readAllBytes(Paths.get(requestedPath));
					rc.write(data);
				} catch (IOException e) {
					System.err.println("The requested file " + requestedPath + " couldn't be read.");
				}
			}
		}

		/**
		 * Sends the error with the appropriate status code and status text to
		 * the client.
		 * 
		 * @param statusCode
		 *            the status code
		 * @param statusText
		 *            the status text
		 */
		private void sendError(int statusCode, String statusText) {
			statusText = "Error " + statusCode + " " + statusText;
			RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
			rc.setStatusCode(statusCode);
			rc.setStatusText(statusText);
			try {
				rc.write(statusText);
			} catch (IOException e) {
				System.err.println("Couldn't write to socket output stream!");
			}

			try {
				csocket.close();
			} catch (IOException e) {
				System.err.println("The socket couldn't be closed!");
			}
		}

		/**
		 * Checks the session for cookies.
		 * 
		 * @param header
		 *            response header
		 */
		private synchronized void checkSession(List<String> header) {
			String sidCandidate = null;

			for (String line : header) {
				if (!line.startsWith("Cookie:"))
					continue;
				line = line.replaceFirst("Cookie: ", "").trim();
				String[] cookies = line.split(";");
				for (String cookie : cookies) {
					String[] cookieSplit = cookie.split("=");
					String name = cookieSplit[0];
					String value = cookieSplit[1].replaceAll("\"", "").trim();
					if (name.equals("sid")) {
						sidCandidate = value;
					}
				}
			}

			SessionMapEntry entry = sessions.get(sidCandidate);
			if (sidCandidate == null || entry == null || entry.validUntil < new Date().getTime() / 1000) {
				entry = createSessionEntry();
			} else {
				long now = new Date().getTime() / 1000; // in seconds
				sessions.get(sidCandidate).validUntil = now + sessionTimeout;
			}

			permPrams = entry.map;
		}

		/**
		 * Creates a new session entry and the corresponding cookie with a new
		 * SID.
		 * 
		 * @return created session entry
		 */
		private SessionMapEntry createSessionEntry() {
			String sid = "";
			for (int i = 0; i < 20; ++i) {
				char letter = (char) (sessionRandom.nextInt(26) + 65);
				sid += letter;
			}

			long now = new Date().getTime() / 1000; // in seconds
			long validUntil = now + sessionTimeout;

			SessionMapEntry entry = new SessionMapEntry(sid, validUntil);

			sessions.put(sid, entry);
			// create cookie
			RCCookie cookie = new RCCookie("sid", sid, address, "/", null);
			cookie.setHttpOnly(true);
			outputCookies.add(cookie);

			return entry;
		}

		/**
		 * Reads the request from the socket input stream.
		 * 
		 * @return request lines
		 */
		private List<String> readRequest() {
			List<String> lines = new ArrayList<>();
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new BufferedInputStream(istream), StandardCharsets.ISO_8859_1));
				String line;
				while (true) {
					line = br.readLine();
					if (line == null || line.isEmpty())
						break;
					lines.add(line);
				}
			} catch (IOException e) {
				System.err.println("IOException occurred: " + e + ".");
				System.err.println("The requested file couldn't be read.");
			}
			return lines;
		}

		/**
		 * Parses the parameters from the given string and places them in the
		 * parameters map.
		 * 
		 * @param paramString
		 *            string containing parameters
		 */
		private void parseParameters(String paramString) {
			final int args = 2;
			String[] paramSplit = paramString.split("&");
			int paramsLen = paramSplit.length;
			String paramsArray[][] = new String[paramsLen][args];
			for (int i = 0; i < paramsLen; ++i) {
				String[] param = paramSplit[i].split("=");
				if (param.length != 2) {
					System.err.println("Illegal number of arguments.");
					return;
				}
				paramsArray[i] = param;
			}

			for (int i = 0; i < paramsLen; ++i) {
				params.put(paramsArray[i][0], paramsArray[i][1]);
			}
		}

		/**
		 * Extracts the document node from the given path name.
		 * 
		 * @param pathname
		 *            path name
		 * @return the document node
		 */
		private DocumentNode getDocumentNode(String pathname) {
			Path path = Paths.get(pathname);
			String document = Util.getStringFromPath(path);
			return new SmartScriptParser(document).getDocumentNode();
		}
	}

	/**
	 * Returns the web worker with the given name.
	 * 
	 * @param name
	 *            web worker name
	 * @return web worker instance
	 */
	private IWebWorker getWorkerFromName(String name) {
		Class<?> referenceToClass = null;
		try {
			referenceToClass = this.getClass().getClassLoader().loadClass(name);
		} catch (ClassNotFoundException e) {
			System.err.println("Class " + name + " not found!");
			return null;
		}
		Object newObject = null;
		try {
			newObject = referenceToClass.newInstance();
		} catch (Exception ingorable) {
		}
		return (IWebWorker) newObject;
	}

	/**
	 * Represents a session map entry with his SID, time until valid and map for
	 * storing cookie data.
	 * 
	 * @author labramusic
	 *
	 */
	private static class SessionMapEntry {

		/**
		 * The session id.
		 */
		protected String sid;

		/**
		 * Time until valid.
		 */
		protected long validUntil;

		/**
		 * Session map.
		 */
		protected Map<String, String> map;

		/**
		 * Initializes a SessionMapEntry.
		 * 
		 * @param sid
		 *            session id
		 * @param validUntil
		 *            time until valid
		 */
		public SessionMapEntry(String sid, long validUntil) {
			this.sid = sid;
			this.validUntil = validUntil;
			map = new ConcurrentHashMap<>();
		}
	}
}
