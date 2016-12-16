/**
 *  System test of MyHttpServer
 */

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class MyDBTests {
	static final String MONGO_URI = "mongodb://testtest:testtest@ds133428.mlab.com:33428/hangman438";
	static final String MONGO_DB = "DB{name='hangman438'}";
	static final String MONGO_COLLECTION = "DBCollection{database=DB{name='hangman438'}, name='hangmanstats'}";
	static final String PREFIX = "http://localhost:8080/cst6/MyServlet";
	static final String TEST_USERNAME = "jontest";

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

	@Test
	public void testConnection() throws Exception {
		DBCollection stats = getStatsCollection();
		assertEquals("Bad MongoCollection", MONGO_COLLECTION, stats.toString());
	}
	
	@Test
	public void testDBFunctions() throws IOException, InterruptedException {
		DBCollection stats = getStatsCollection();
		BasicDBObject document = new BasicDBObject();
		stats.remove(document);
		assertEquals("Not clearing DB properly", 0, stats.getStats().get("count"));
		
		
		// Create new user
		URL url = new URL(PREFIX);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Cookie", "MongoUsername=" + TEST_USERNAME);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String charset = "UTF-8";
        String s = "username=" + URLEncoder.encode(TEST_USERNAME, charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();
        
        Thread.sleep(5000);
		assertEquals("New player not added correctly", 1, stats.getStats().get("count"));
		
		// Get user
		BasicDBObject fields = new BasicDBObject();
		fields.put("username", TEST_USERNAME);
		DBCursor cursor = stats.find(fields);
		DBObject user = null;
		if (cursor.hasNext()) {
			user = cursor.next();
		}
		assertNotNull("User wasn't saved or accessed properly", user);
		
		// Ensure no game is running
		assertFalse("Account wasn't initialized properly", (Boolean) user.get("isGameInProgress"));
		
		// User creates new game
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Cookie", "MongoUsername=" + TEST_USERNAME);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        s = "difficulty=" + URLEncoder.encode("normal", charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();
        
        Thread.sleep(5000);

        // Ensure game is created correctly
        user = getUser(TEST_USERNAME);
        assertNotNull("User wasn't retrieved properly", user);
		assertTrue("Game wasn't set up properly", (Boolean) user.get("isGameInProgress"));
	}
	
	
	private DBCollection getStatsCollection() {
		MongoClientURI uri  = new MongoClientURI("mongodb://testtest:testtest@ds133428.mlab.com:33428/hangman438"); 
		assertEquals("Bad MongoClientURI", MONGO_URI, uri.toString());
	    MongoClient client = new MongoClient(uri);
	    DB db = client.getDB(uri.getDatabase());
		assertEquals("Bad DB", MONGO_DB, db.toString());
		return db.getCollection("hangmanstats");
	}
	
	private DBObject getUser(String username) {
		DBCollection stats = getStatsCollection();
		BasicDBObject fields = new BasicDBObject();
		fields.put("username", username);
		DBCursor cursor = stats.find(fields);
		DBObject user = null;
		if (cursor.hasNext()) {
			user = cursor.next();
		}
		return user;
	}
}
