package jo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Hangman {
	static ArrayList<String> wordlist = null;
	
	private String word;
	private ArrayList<Character> guesses;
	private int wrongGuesses;
	private boolean newGuess;
	
	public Hangman() {
		word = randomWord();
		guesses = new ArrayList<Character>();
		wrongGuesses = 0;
		newGuess = false;
	}
	
	public String getWord() {
		return word;
	}
	
	public void addGuess(String guess) {
		newGuess = true; 
		if (guess != null && guess.length() > 0) { // Make sure the user did make a guess
			Character nextGuess = guess.toLowerCase().charAt(0); // Only use the first, lowercase letter
			if (Character.getType(nextGuess) == Character.LOWERCASE_LETTER) { // Make sure it is not a symbol or special char
				for (Character c : guesses) {
					if (nextGuess.equals(c)) {
						newGuess = false;
						return;
					}
				}
				guesses.add(nextGuess);
				if (!isCorrectNewLetter()) {
					wrongGuesses++;
				}
			}
		}
	}
	
	// See if the most recent guess was a new character
	public boolean isNewGuess() {
		return newGuess;
	}
	
	// Check if the latest character added was a correct guess
	public boolean isCorrectNewLetter() {
		if (newGuess) {
			for (int i = 0; i < word.length(); i++) {
				if (guesses.get(guesses.size() - 1) == word.charAt(i)) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Method to generate the "_ _ a _" string for the .jsp
	public String generateGuessString() {
		String outputString = "";
		for (int i = 0; i < word.length(); i++) {
			boolean correctLetter = false;
			for (Character c : guesses) {
				if (word.charAt(i) == c.charValue()) {
					outputString += word.charAt(i) + " ";
					correctLetter = true;
				}
			}
			
			if (correctLetter == false) {
				outputString += "_ ";
			}			
		}
		return outputString;
	}
	
	// Method for checking if the user has lost and also to get the correct image
	public int getWrongGuesses() {
		return wrongGuesses;
	}
	
	public boolean isGameWon() {
		return !this.generateGuessString().contains("_");
	}
	
	// For displaying "There are no _'s"
	public Character getLastGuess() {
		return guesses.get(guesses.size() - 1);
	}
	
	public static String randomWord() {
		try {
			if (wordlist == null) {
				wordlist = new ArrayList<String>();
				// read in word list
				ClassLoader classloader = Thread.currentThread().getContextClassLoader();
				InputStream is = classloader.getResourceAsStream("wordlist.txt");
				Scanner infile = new Scanner(is);
				while (infile.hasNextLine()) {
					wordlist.add(infile.nextLine());
				}
				infile.close();
			}
			int t = generator.nextInt(wordlist.size());
			return wordlist.get(t);

		} catch (Exception e) {
			System.out.println("Error randomWord: reading wordlist. " + e.getMessage());
			System.exit(0);
			return null; // to keep compiler happy
		}
	}

	static Random generator = new Random();
}
