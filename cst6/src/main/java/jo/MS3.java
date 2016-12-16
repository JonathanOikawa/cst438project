package jo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletException;
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
public class MS3 extends HttpServlet  {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MS3() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		HttpSession session = request.getSession();
		System.out.println("THIS HERE IS THE USERNAME: " + session.getAttribute("UserName"));
	    session.setAttribute("UserName", "jonkjon");
	    
		MongoClientURI uri  = new MongoClientURI("mongodb://testtest:testtest@ds133428.mlab.com:33428/hangman438"); 
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());    
        System.out.println(db);
		DBCollection stats = db.getCollection("hangmanstats");
        System.out.println(stats);

        String json = "{'database' : 'mkyongDB','table' : 'hosting'," +
        		  "'detail' : {'records' : 99, 'index' : 'vps_index1', 'active' : 'true'}}}";

		DBObject dbObject = (DBObject)JSON.parse(json);

//		stats.insert(dbObject);
        
        System.out.println("stats.findOne()");
		System.out.println(stats.findOne());
		
		BasicDBObject fields = new BasicDBObject();
		fields.put("database", "mkyongDB");
		DBCursor cursor = stats.find(fields);
        System.out.println("stats.find()");
		while (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			System.out.println(dbo);
			System.out.println(dbo.get("detail"));
			BasicBSONObject bbson = ((BasicBSONObject) dbo.get("detail"));
			System.out.println(bbson.get("records"));
			bbson.put("records", 1992);
			System.out.println(dbo);
			
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.append("$set", new BasicDBObject().append("detail", bbson));

			BasicDBObject searchQuery = new BasicDBObject().append("database", "mkyongDB");

			stats.update(searchQuery, newDocument);
		}
		
		// TODO Auto-generated method stub
		if (request.getParameter("js") != null) {
			response.setContentType("text/javascript");
			String fileName = request.getParameter("js");             
		    FileInputStream fis = new FileInputStream(new File("C:/Users/oikaw/Desktop/Oikawacst6/cst6/src/main/js/jo/" + fileName));
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
		} else {
			Hangman hangmanGame;
			if (request.getSession().getAttribute("game") == null) {
				hangmanGame = new Hangman();
				request.getSession().setAttribute("game", hangmanGame);
				request.getRequestDispatcher("hangman.jsp").forward(request, response);
			} else if (((Hangman) request.getSession().getAttribute("game")).getWord() == null) {
				request.getRequestDispatcher("hangman.jsp").forward(request, response);
			} else {
				hangmanGame = (Hangman)request.getSession().getAttribute("game");
				String imagePath = Integer.toString(hangmanGame.getWrongGuesses() + 1);
				request.setAttribute("image", imagePath);
				request.setAttribute("time", hangmanGame.getCurrentTimePlaying());
				if (hangmanGame.isGameWon() == true) {
					request.getRequestDispatcher("hgwin.jsp").forward(request, response);
				} else if (hangmanGame.getWrongGuesses() == 6) {
					request.setAttribute("word", hangmanGame.getWord());
					request.getRequestDispatcher("hglose.jsp").forward(request, response);
				} else {
					request.setAttribute("word", hangmanGame.getWord());
					request.setAttribute("guess", hangmanGame.generateGuessString());
					if (!hangmanGame.isNewGuess() || hangmanGame.isCorrectNewLetter()) {
						request.getRequestDispatcher("hgcont.jsp").forward(request, response);
					} else {
						request.setAttribute("lastGuess", hangmanGame.getLastGuess());
						request.getRequestDispatcher("hgcontbad.jsp").forward(request, response);
					}
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Hangman hangmanGame = (Hangman)request.getSession().getAttribute("game");
		if (request.getParameter("difficulty") != null) {
			hangmanGame.setWord(request.getParameter("difficulty"));
			doGet(request, response);
		} else {
			hangmanGame.addGuess(request.getParameter("guess"));
			hangmanGame.setCurrentTimePlaying(Integer.parseInt(request.getParameter("time")));
			HttpSession session = request.getSession();
			doGet(request, response);
		}
	}

}
