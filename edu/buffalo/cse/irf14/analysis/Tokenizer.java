/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;



/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
	//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenStream tokenStream=new TokenStream();
	      str=str.trim();
		String[] result = str.split("\\s+");
		int len=result.length;
		ArrayList<Token> al = new ArrayList<Token>();
	     
	     
		Token[] token=new Token[len];
	     for (int x=0; x<result.length; x++)
	     {
	    	 result[x].replaceAll(","," ").trim();
	    	 token[x]=new Token();
	         token[x].setTermText(result[x]);
	         token[x].setTermBuffer(result[x].toCharArray());//Add Other Info If needed after words
	         al.add(token[x]);
	      }
	    TokenStream ts=new TokenStream();
	     ts.setTokenstream(al);
	    
	    
	    TokenStream tkStream=new TokenStream();
	    tkStream.streamoftokens=ts.getTokenstream();
	     for(Token t:tkStream.streamoftokens)
	    	System.out.println(t.getTermText());
		
		return ts;
	}
}
