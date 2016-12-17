/**
 *  System test of MongoLab
 */

import static org.junit.Assert.*;

import org.bson.BasicBSONObject;
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
		post("username", TEST_USERNAME);
        
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
		post("difficulty", "normal");
        
        Thread.sleep(5000);

        // Ensure game is created correctly
        user = getUser(TEST_USERNAME);
        assertNotNull("User wasn't retrieved properly", user);
		assertTrue("Game wasn't set up properly", (Boolean) user.get("isGameInProgress"));
		
		// Solve the game
		post("guess", "n");
        Thread.sleep(5000);
		post("guess", "o");
        Thread.sleep(5000);
		post("guess", "r");
        Thread.sleep(5000);
		post("guess", "m");
        Thread.sleep(5000);
		post("guess", "a");
		
		Thread.sleep(5000);

        user = getUser(TEST_USERNAME);
		DBObject currentGame = (DBObject) user.get("currentGame");
        assertNotNull("Game wasn't retrieved", currentGame);
		assertEquals("Guesses aren't being recorded properly", "norma", currentGame.get("guesses"));
		assertEquals("Wrong guess counter", 0, currentGame.get("wrong"));

		Thread.sleep(5000);

		post("guess", "t");
        Thread.sleep(5000);
		post("guess", "s");		
        Thread.sleep(5000);
        user = getUser(TEST_USERNAME);
		currentGame = (DBObject) user.get("currentGame");
		assertEquals("Guesses aren't being recorded properly", "normats", currentGame.get("guesses"));
		assertEquals("Wrong guess counter", 2, currentGame.get("wrong"));
		
		post("guess", "l");
		
		Thread.sleep(5000);
		
		// User creates new game
		post("difficulty", "easy");
        
        Thread.sleep(5000);

        // Ensure game is created correctly
        user = getUser(TEST_USERNAME);
        assertNotNull("User wasn't retrieved properly", user);
		assertTrue("Game wasn't set up properly", (Boolean) user.get("isGameInProgress"));
		
		post("guess", "a");
        Thread.sleep(5000);
		post("guess", "b");
        Thread.sleep(5000);
		post("guess", "c");
        Thread.sleep(5000);
		post("guess", "d");
        Thread.sleep(5000);
		post("guess", "f");
        Thread.sleep(5000);
		post("guess", "g");
		
		Thread.sleep(15000);

        user = getUser(TEST_USERNAME);
		DBObject winloss = (DBObject) user.get("winloss");
		currentGame = (DBObject) user.get("currentGame");
        assertNotNull("Win/loss data wasn't retrieved", currentGame);
		BasicBSONObject diffwl = ((BasicBSONObject) winloss.get("normal"));
        assertNotNull("Normal data wasn't retrieved", currentGame);

        assertEquals("Normal total incorrectly calculated", 1, diffwl.get("total"));
        assertEquals("Normal wins incorrectly calculated", 1, diffwl.get("total"));
		diffwl = ((BasicBSONObject) winloss.get("easy"));
        assertNotNull("Easy data wasn't retrieved", currentGame);

        assertEquals("Easy total incorrectly calculated", 1, diffwl.get("total"));
        assertEquals("Easy wins incorrectly calculated", 1, diffwl.get("total"));
	}
	
	private void post(String key, String value) throws IOException {
		URL url = new URL(PREFIX);
        String charset = "UTF-8";
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Cookie", "MongoUsername=" + TEST_USERNAME);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String s = key + "=" + URLEncoder.encode(value, charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();
        
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