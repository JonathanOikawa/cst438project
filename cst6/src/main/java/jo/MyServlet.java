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
