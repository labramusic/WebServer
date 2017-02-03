package hr.fer.zemris.java.webserver;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

public class RequestContextTests {

	private ByteArrayOutputStream os;
	private ByteArrayInputStream is;
	private RequestContext rc;

	@Before
	public void initialize() throws IOException {
		os = new ByteArrayOutputStream();
		rc = new RequestContext(os, new HashMap<String, String>(),
				new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding("UTF-8");
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("test");
	}

	@Test
	public void testHeader1() throws IOException {
		rc.addRCCookie(new RCCookie("korisnik", "perica", "127.0.0.1", "/", 3600));
		rc.addRCCookie(new RCCookie("zgrada", "B4", null, "/", null));
		rc.write("Čevapčići i Šiščevapčići.");
		is = new ByteArrayInputStream(((ByteArrayOutputStream)rc.getOutputStream()).toByteArray());
		Scanner sc = new Scanner(is);
		String header = "";
		while (sc.hasNextLine()) {
			header += sc.nextLine() + "\r\n";
		}
		sc.close();
		String string = "HTTP/1.1 205 test\r\n"+
				"Content-Type: text/plain; charset=UTF-8\r\n"+
				"Set-Cookie: korisnik=\"perica\"; Domain=127.0.0.1; Path=/; Max-Age=3600\r\n"+
				"Set-Cookie: zgrada=\"B4\"; Path=/\r\n"+
				"\r\n"+
				"Čevapčići i Šiščevapčići.\r\n";
		assertEquals(string, header);
	}

	@Test
	public void testHeader2() throws IOException {
		rc.write("Čevapčići i Šiščevapčići.");
		is = new ByteArrayInputStream(((ByteArrayOutputStream)rc.getOutputStream()).toByteArray());
		Scanner sc = new Scanner(is);
		String header = "";
		while (sc.hasNextLine()) {
			header += sc.nextLine() + "\r\n";
		}
		sc.close();
		String string = "HTTP/1.1 205 test\r\n"+
				"Content-Type: text/plain; charset=UTF-8\r\n"+
				"\r\n"+
				"Čevapčići i Šiščevapčići.\r\n";
		assertEquals(string, header);
	}

	@Test
	public void testHeader3() throws IOException {
		rc.addRCCookie(new RCCookie("korisnik", "perica", null, "/", 3600));
		rc.addRCCookie(new RCCookie("zgrada", "B4", null, null, null));
		rc.write("Čevapčići i Šiščevapčići.");
		is = new ByteArrayInputStream(((ByteArrayOutputStream)rc.getOutputStream()).toByteArray());
		Scanner sc = new Scanner(is);
		String header = "";
		while (sc.hasNextLine()) {
			header += sc.nextLine() + "\r\n";
		}
		sc.close();
		String string = "HTTP/1.1 205 test\r\n"+
				"Content-Type: text/plain; charset=UTF-8\r\n"+
				"Set-Cookie: korisnik=\"perica\"; Path=/; Max-Age=3600\r\n"+
				"Set-Cookie: zgrada=\"B4\"\r\n"+
				"\r\n"+
				"Čevapčići i Šiščevapčići.\r\n";
		assertEquals(string, header);
	}

	@Test
	public void testHeader4() throws IOException {
		rc.addRCCookie(new RCCookie("korisnik", "perica", null, null, 3600));
		rc.addRCCookie(new RCCookie("zgrada", "B4", "127.0.0.1", "/path", null));
		rc.addRCCookie(new RCCookie("grad", "Zagreb", null, "/", null));
		rc.write("Čevapčići i Šiščevapčići.");
		is = new ByteArrayInputStream(((ByteArrayOutputStream)rc.getOutputStream()).toByteArray());
		Scanner sc = new Scanner(is);
		String header = "";
		while (sc.hasNextLine()) {
			header += sc.nextLine() + "\r\n";
		}
		sc.close();
		String string = "HTTP/1.1 205 test\r\n"+
				"Content-Type: text/plain; charset=UTF-8\r\n"+
				"Set-Cookie: korisnik=\"perica\"; Max-Age=3600\r\n"+
				"Set-Cookie: zgrada=\"B4\"; Domain=127.0.0.1; Path=/path\r\n"+
				"Set-Cookie: grad=\"Zagreb\"; Path=/\r\n"+
				"\r\n"+
				"Čevapčići i Šiščevapčići.\r\n";
		assertEquals(string, header);
	}

	@After
	public void finish() throws IOException {
		os.close();
	}

}
