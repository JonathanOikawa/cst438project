/**
 * Unit test of MyHandler using mockup TestHttpExchange  
 */


import static org.junit.Assert.*;
import jo.MyServlet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.Headers;


public class MyServerUnitTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
    
	/**
	 * test for begin a new game.
	 * there is no cookie in the header.
	 * @throws Exception
	 */
	@Test
	public void test1() throws Exception {
		
		final String expectedBody = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head>"+
	                    "<body><h2>Hangman</h2><img src=\"h1.gif\"><h2 style=\"font-family:'Lucida Console', monospace\">"+
				        " _ _ _ _ _ </h2><form action=\"/\" method=\"get\">  Guess a character <input type=\"text\" name=\"guess\">"+
	                    "<br><input type=\"submit\" value=\"Submit\"></form></body></html>";
		
		final String expectedBody2 = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head>"+
                "<body><h2>Hangman</h2><img src=\"h1.gif\"><h2 style=\"font-family:'Lucida Console', monospace\">"+
		        " a _ _ _ _ </h2><form action=\"/\" method=\"get\">  Guess a character <input type=\"text\" name=\"guess\">"+
                "<br><input type=\"submit\" value=\"Submit\"></form></body></html>";
		
		final String expectedBody3 = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head>"+
                "<body><h2>Hangman</h2><img src=\"h2.gif\"><h2 style=\"font-family:'Lucida Console', monospace\">"+
		        " a _ _ _ _ </h2><form action=\"/\" method=\"get\">  There are no m's. <input type=\"text\" name=\"guess\">"+
				"<br><input type=\"submit\" value=\"Submit\"></form></body></html>";
		
		MyServlet servlet = new MyServlet();
		Headers header;	
		TestHttpExchange t;
		Headers response;
		String cookie1, cookie2;

		
		header = new Headers();
		t = new TestHttpExchange("/", header);
		servlet.doGet(null, null);
		handler.handle(t);
		response = t.getResponseHeaders();
		cookie1 = response.getFirst("Set-cookie");
		assertEquals("Bad content type", "text/html", response.getFirst("Content-type"));
		assertNotNull("No cookie returned", cookie1);
		assertEquals("Bad response code.",200, t.getResponseCode());
		assertEquals("Bad response body.",expectedBody, t.getOstream().toString());
		
		// test retrieval of gif
		header = new Headers();
		t = new TestHttpExchange("/h1.gif", header);
		handler.handle(t);
		response = t.getResponseHeaders();
		assertEquals("Bad content type", "image/gif", response.getFirst("Content-type"));
		assertEquals("Bad response code.",200, t.getResponseCode());
		assertEquals("Bad response length.","8581", response.getFirst("Content-length"));
		
		// test retrieval of unknown file
		header = new Headers();
		t = new TestHttpExchange("/xx.ico", header);
		handler.handle(t);
		response = t.getResponseHeaders();
		assertEquals("Bad response code.",404, t.getResponseCode());
		
		// test making a good guess of the letter 'a' in the word "apple"
		//  uri contains the value of parameter "guess"
		//  must send back cookie value from previous reply
		//  response must be a new cookie 
		header = new Headers();
		header.add("Cookie", cookie1);
		t = new TestHttpExchange("/?guess=a", header);    
		handler.handle(t);
		response = t.getResponseHeaders();
		cookie2 = response.getFirst("Set-cookie");
		assertEquals("Bad content type", "text/html", response.getFirst("Content-type"));
		assertNotNull("No cookie returned", cookie2);
		assertNotEquals("Cookie did not change value.", cookie1, cookie2);
		assertEquals("Bad response code.",200, t.getResponseCode());
		assertEquals("Bad response body.",expectedBody2, t.getOstream().toString());
		cookie1 = cookie2;
		
		// test a bad guess of the letter 'm'
		header = new Headers();
		header.add("Cookie", cookie1);
		t = new TestHttpExchange("/?guess=m", header);    
		handler.handle(t);
		response = t.getResponseHeaders();
		cookie2 = response.getFirst("Set-cookie");
		assertEquals("Bad content type", "text/html", response.getFirst("Content-type"));
		assertNotNull("No cookie returned", cookie2);
		assertNotEquals("Cookie did not change value.", cookie1, cookie2);
		assertEquals("Bad response code.",200, t.getResponseCode());
		assertEquals("Bad response body.",expectedBody3, t.getOstream().toString());
		cookie1 = cookie2;
	}

}
