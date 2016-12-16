/**
 *  System test of MyHttpServer
 */

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MyServerSystemTest {

	static final String PREFIX = "http://localhost:8080/";

	static final String expectedBody = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head>"
			+ "<body><h2>Hangman</h2><img src=\"h1.gif\"><h2 style=\"font-family:'Lucida Console', monospace\">"
			+ " _ _ _ _ _ </h2><form action=\"/\" method=\"get\">  Guess a character <input type=\"text\" name=\"guess\">"
			+ "<br><input type=\"submit\" value=\"Submit\"></form></body></html>";

	static final String expectedBody2 = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head>"
			+ "<body><h2>Hangman</h2><img src=\"h1.gif\"><h2 style=\"font-family:'Lucida Console', monospace\">"
			+ " a _ _ _ _ </h2><form action=\"/\" method=\"get\">  Guess a character <input type=\"text\" name=\"guess\">"
			+ "<br><input type=\"submit\" value=\"Submit\"></form></body></html>";

	static final String expectedBody3 = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head>"
			+ "<body><h2>Hangman</h2><img src=\"h2.gif\"><h2 style=\"font-family:'Lucida Console', monospace\">"
			+ " a _ _ _ _ </h2><form action=\"/\" method=\"get\">  There are no m's. <input type=\"text\" name=\"guess\">"
			+ "<br><input type=\"submit\" value=\"Submit\"></form></body></html>";

	HttpURLConnection con;

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

//	@Test
//	public void test1() throws Exception {
//		// start a game
//		URL url = new URL(PREFIX + "/cst6/MyServlet");
//		con = (HttpURLConnection) url.openConnection();
//		int rc = con.getResponseCode();
//		assertEquals("Bad return code.", 200, rc);
////		List<String> list = con.getHeaderFields().get("Set-cookie");
//		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String actualBody = "";
//		String outputLine;
//		while ((outputLine = br.readLine()) != null)
//			actualBody = actualBody + outputLine;
//		con.disconnect();
//		System.out.println("HEY");
//		System.out.println(actualBody);
////		assertNotNull("Missing cookie.", list);
//	}
	
	@Test
	public void test2() throws Exception {
		URL url = new URL(PREFIX + "cst6/MyServlet");
		con = (HttpURLConnection) url.openConnection();
		int rc = con.getResponseCode();
		
		con.disconnect();
		String urlParameters = "difficulty=easy";
		
		con.setDoOutput(true);
		con.connect();
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		
	}
	

	
	

//	public String startGame() throws Exception {
//		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String actualBody = "";
//		String outputLine;
//		while ((outputLine = br.readLine()) != null)
//			actualBody = actualBody + outputLine;
//		con.disconnect();
//		assertEquals("Bad response body.", expectedBody, actualBody);
//		return cookie;
//	}
	
	public String guessLetter(String letter, String cookie, String expectedOutput) throws Exception {
		URL url = new URL(PREFIX + "?guess="+letter);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Cookie", cookie);
		int rc = con.getResponseCode();
		assertEquals("Bad return code.", 200, rc);
		assertEquals("Bad content type.", "text/html", con.getContentType());
		List<String> list = con.getHeaderFields().get("Set-cookie");
		assertNotNull("Missing cookie.", list);
		String cookie2 = list.get(0);
		assertNotEquals("Cookie did not change value.", cookie2, cookie);
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String actualBody = "";
		String outputLine;
		while ((outputLine = br.readLine()) != null)
			actualBody = actualBody + outputLine;
		con.disconnect();
		assertEquals("Bad response body.", expectedOutput, actualBody);
		con.disconnect();
		return cookie2;
	}
	
	public void testGoodFile(String file, int expectedLength, String expectedContentType) throws Exception {
		URL url = new URL(PREFIX + file);
		con = (HttpURLConnection) url.openConnection();
		int rc = con.getResponseCode();
		assertEquals("Bad return code.", 200, rc);
		assertEquals("Content length incorrect.", expectedLength, con.getContentLength());
		assertEquals("Bad content type.", expectedContentType , con.getContentType());
		con.disconnect();
	}
	
	public void testBadFile(String file) throws Exception {
		URL url = new URL(PREFIX + file);
		con = (HttpURLConnection) url.openConnection();
		int rc = con.getResponseCode();
		assertEquals("Bad return code.", 404, rc);
		con.disconnect();
	}

}
