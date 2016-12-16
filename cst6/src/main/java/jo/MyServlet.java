package jo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.BasicBSONObject;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.MongoURI;
import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;

import java.io.*;


/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet  {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		HttpSession session = request.getSession();
		
		if (request.getParameter("js") != null) {
			response.setContentType("text/javascript");
			String fileName = request.getParameter("js");             
		    FileInputStream fis = new FileInputStream(new File("C:/Users/oikaw/Desktop/Oikawacst6/cst6/src/main/js/jo/" + fileName));
		    BufferedInputStream bis = new BufferedInputStream(fis);
		    for (int data; (data = bis.read()) > -1;) {
		    	response.getWriter().write(data);
		    }
		} else if (request.getParameter("css") != null) {
			response.setContentType("text/css");
			String fileName = request.getParameter("css");             
		    FileInputStream fis = new FileInputStream(new File("C:/Users/oikaw/Desktop/Oikawacst6/cst6/src/main/css/jo/" + fileName));
		    BufferedInputStream bis = new BufferedInputStream(fis);
		    for (int data; (data = bis.read()) > -1;) {
		    	response.getWriter().write(data);
		    }
		} else if (request.getParameter("image") != null) {
			try{
			     String fileName = request.getParameter("image");             
			     FileInputStream fis = new FileInputStream(new File("C:/Users/oikaw/Desktop/Oikawacst6/cst6/src/main/resources/" + fileName));
			     BufferedInputStream bis = new BufferedInputStream(fis);             
			     response.setContentType("image/gif");
			     BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
			     for (int data; (data = bis.read()) > -1;) {
			    	 output.write(data);
			     }             
			  }
			  catch(IOException e) {
			
			  } finally{
			      // close the streams
			  }
		} else if (request.getParameter("stats") != null) {
			String username = (String) session.getAttribute("username");
			DBObject user = getUser(username);
			DBObject currentGame = (DBObject) user.get("currentGame");
			request.setAttribute("username", username);
			request.setAttribute("isGameInProgress", user.get("isGameInProgress"));
			
			BasicBSONObject wl = ((BasicBSONObject) user.get("winloss"));
			BasicBSONObject easywl = ((BasicBSONObject) wl.get("easy"));			
			request.setAttribute("easyTotal", easywl.get("total"));	
			request.setAttribute("easyWin", easywl.get("wins"));
			

			BasicBSONObject normalwl = ((BasicBSONObject) wl.get("normal"));			
			request.setAttribute("normalTotal", normalwl.get("total"));	
			request.setAttribute("normalWin", normalwl.get("wins"));
			

			BasicBSONObject hardwl = ((BasicBSONObject) wl.get("hard"));			
			request.setAttribute("hardTotal", hardwl.get("total"));	
			request.setAttribute("hardWin", hardwl.get("wins"));
			
			request.getRequestDispatcher("stats.jsp").forward(request, response);
		} else if (session.getAttribute("username") == null) {
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} else if (session.getAttribute("username") != null) {
			String username = (String) session.getAttribute("username");
			DBObject user = getUser(username);
			System.out.println("Username:  " + username);
			System.out.println(user);
			if (user != null && (Boolean)user.get("isGameInProgress") == true) {
				DBObject currentGame = (DBObject) user.get("currentGame");
				String imagePath = Integer.toString((Integer) currentGame.get("wrong") + 1);
				String currentGameGuesses = (String) currentGame.get("guesses");
				request.setAttribute("image", imagePath);
				request.setAttribute("time", currentGame.get("time"));
				request.setAttribute("guess", Hangman.generateGuessString((String) currentGame.get("word"), currentGameGuesses));
				
				if (Hangman.isGameWon((String) currentGame.get("word"), currentGameGuesses) == true) {
					request.getRequestDispatcher("hgwin.jsp").forward(request, response);
					updateWinLoss(username, (String) currentGame.get("difficulty"), true);
					updateAttribute(username, "isGameInProgress", false);
				} else if ((Integer) currentGame.get("wrong") == 6) {
					request.setAttribute("word", currentGame.get("word"));
					updateWinLoss(username, (String) currentGame.get("difficulty"), false);
					updateAttribute(username, "isGameInProgress", false);
					request.getRequestDispatcher("hglose.jsp").forward(request, response);
				} else {
					if ((Boolean) currentGame.get("newGuess") == false) {
						request.getRequestDispatcher("hgcont.jsp").forward(request, response);
					} else {
						request.setAttribute("lastGuess", currentGameGuesses.charAt(currentGameGuesses.length() - 1));
						request.getRequestDispatcher("hgcontbad.jsp").forward(request, response);
					}
				}
			} else {
				request.getRequestDispatcher("hangman.jsp").forward(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String rawCookie = request.getHeader("Cookie");
		String[] rawCookieParams = rawCookie.split(";");
		for(String rawCookieNameAndValue :rawCookieParams) {
			String[] rawCookieNameAndValuePair = rawCookieNameAndValue.split("=");
			System.out.println("IMPORTNAT: " + rawCookieNameAndValuePair[0]);
			if (rawCookieNameAndValuePair[0].equals("MongoUsername")) {
				username = rawCookieNameAndValuePair[1];
			}
		}
		System.out.println("username is now: " + username);
		System.out.println("username is now: " + request.getParameter("difficulty"));
		HttpSession session = request.getSession();
		// TODO Auto-generated method stub
		Hangman hangmanGame = (Hangman)request.getSession().getAttribute("game");
		if (request.getParameter("difficulty") != null) {
			startNewGame(username, request.getParameter("difficulty"));
		} else if (username != null) {
			if (getUser(username) == null) {
				createNewUser(username);
			}
			response.addCookie(new Cookie("MongoUsername", request.getParameter("username")));
		} else if (request.getParameter("logout") != null) {
			response.addCookie(new Cookie("MongoUsername", null));
		} else {
			if (request.getParameter("guess").length() >= 1) {
				String guess = request.getParameter("guess").toLowerCase().substring(0, 1);
				addGuess(username, guess);
			}
			updateTime(username, Integer.parseInt(request.getParameter("time")));
		}
		doGet(request, response);
	}

	private void createNewUser(String username) {
		DBObject dbObject = (DBObject)JSON.parse("{'username' : '" + username + "'}");
		dbObject.put("isGameInProgress", false);
		dbObject.put("currentGame", (DBObject)JSON.parse(generateGameJSONString("", "", 0, false, 0, "")));
		// add guesses and win loss
		DBCollection stats = getStatsCollection();
		dbObject.put("winloss", (BasicDBObject)JSON.parse(generateWLJSONString()));
		stats.insert(dbObject);
	}
	
	private DBCollection getStatsCollection() {
		MongoClientURI uri  = new MongoClientURI("mongodb://testtest:testtest@ds133428.mlab.com:33428/hangman438"); 
	    MongoClient client = new MongoClient(uri);
	    DB db = client.getDB(uri.getDatabase());
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
	
	private boolean startNewGame(String username, String difficulty) {
		System.out.println("Starting new game");
		System.out.println("Username: " + username);
		String newGameJSON = generateGameJSONString(Hangman.randomWord(difficulty), "", 0, false, 0, difficulty);
		BasicDBObject dbObject = (BasicDBObject)JSON.parse(newGameJSON);
		updateAttribute(username, "currentGame", dbObject);
		updateAttribute(username, "isGameInProgress", true);
		
		System.out.println("Username: " + username);
		DBObject user = getUser(username);
		System.out.println(user);
		System.out.println(user.get("currentGame"));
		
		return true;
	}
	
	private boolean updateAttribute(String username, String attribute, boolean value) {
		DBCollection stats = getStatsCollection();
		
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.append("$set", new BasicDBObject().append(attribute, value));

		BasicDBObject searchQuery = new BasicDBObject().append("username", username);

		stats.update(searchQuery, newDocument);
		return true;
	}
	
	private boolean updateAttribute(String username, String attribute, BasicDBObject bdbo) {
		DBCollection stats = getStatsCollection();
		
		BasicDBObject updatedDocument = new BasicDBObject();
		updatedDocument.append("$set", new BasicDBObject().append(attribute, bdbo));

		BasicDBObject searchQuery = new BasicDBObject().append("username", username);

		stats.update(searchQuery, updatedDocument);
		return true;
	}
	
	private boolean updateWinLoss(String username, String difficulty, boolean win) {
		DBCollection stats = getStatsCollection();
		DBObject user = getUser(username);
		BasicDBObject updatedDocument = new BasicDBObject();
		BasicBSONObject wl = ((BasicBSONObject) user.get("winloss"));
		BasicBSONObject diffwl = ((BasicBSONObject) wl.get(difficulty));
		diffwl.put("total", (Integer) diffwl.get("total") + 1);
		if (win) {
			diffwl.put("wins", (Integer) diffwl.get("wins") + 1);
		}
		wl.put(difficulty, diffwl);
		updatedDocument.append("$set", new BasicDBObject().append("winloss", wl));
		
		BasicDBObject searchQuery = new BasicDBObject().append("username", username);
		stats.update(searchQuery, updatedDocument);
		return true;
	}
	
	private boolean addGuess(String username, String guess) {
		DBCollection stats = getStatsCollection();
		DBObject user = getUser(username);
		System.out.println("THis: " + user);
		BasicDBObject updatedDocument = new BasicDBObject();
		System.out.println("THias: " + user.get("currentGame"));
		BasicBSONObject currentGame = ((BasicBSONObject) user.get("currentGame"));
		
		String currentGuesses = (String) currentGame.get("guesses");
		if (!currentGuesses.contains(guess)) {
			currentGame.put("guesses", currentGuesses + guess);
			currentGame.put("newGuess", false);
			if (!((String) currentGame.get("word")).contains(guess)) {
				currentGame.put("wrong", (Integer) currentGame.get("wrong") + 1);
				currentGame.put("newGuess", true);
			}
		}	
		
		updatedDocument.append("$set", new BasicDBObject().append("currentGame", currentGame));
		
		BasicDBObject searchQuery = new BasicDBObject().append("username", username);
		stats.update(searchQuery, updatedDocument);
		
		return true;
	}
	
	private boolean updateTime(String username, int time) {
		DBCollection stats = getStatsCollection();
		DBObject user = getUser(username);
		BasicDBObject updatedDocument = new BasicDBObject();
		BasicBSONObject currentGame = ((BasicBSONObject) user.get("currentGame"));
		currentGame.put("time", time);
		
		updatedDocument.append("$set", new BasicDBObject().append("currentGame", currentGame));
		
		BasicDBObject searchQuery = new BasicDBObject().append("username", username);
		stats.update(searchQuery, updatedDocument);
		return true;
	}
	
	private String generateGameJSONString(String word, String guesses, int wrong, boolean newGuess, int time, String difficulty) {	
		String gameString = "{";
		gameString += "'word' : '" + word + "',";
		gameString += "'guesses' : '" + guesses + "',";
		gameString += "'wrong' : " + wrong + ",";
		gameString += "'newGuess' : " + newGuess + ",";
		gameString += "'time' : " + time + ",";
		gameString += "'difficulty' : '" + difficulty + "'";
		gameString += "}";
		return gameString;
	}
	
	private String generateWLJSONString() {	
		String wlString = "{";
		wlString += "'easy': { 'total' : 0, 'wins' : 0 },";
		wlString += "'normal': { 'total' : 0, 'wins' : 0 },";
		wlString += "'hard': { 'total' : 0, 'wins' : 0 }";
		wlString += "}";
		return wlString;
	}

}
