/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;




/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	TokenStream tstream;
	String delim;
	public Tokenizer() {
		this.delim=" ";
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		this.delim=delim;
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
	      ArrayList<Token> al = new ArrayList<Token>();
		if(this.delim=="and")
		{
			str.trim();
			 str = str.replaceAll("\"", ""); //To Remove Double Quotes
			 String[] result = str.split(this.delim);
			 int len=result.length;

		      Token[] token=new Token[len];
		      for (int x=0; x<result.length; x++)
		      {
		    	 token[x]=new Token();
		         token[x].setTermText(result[x]);
		         token[x].setTermBuffer(result[x].toCharArray());//Add Other Info If needed after words
		         al.add(token[x]);
		      }
			 
		}
		else if(this.delim==",")
		{
			str.trim();
			 str = str.replaceAll("\"", ""); //To Remove Double Quotes
			 String[] result = str.split(this.delim);
			 int len=result.length;

		      Token[] token=new Token[len];
		      for (int x=0; x<result.length; x++)
		      {
		    	 token[x]=new Token();
		         token[x].setTermText(result[x]);
		         token[x].setTermBuffer(result[x].toCharArray());//Add Other Info If needed after words
		         al.add(token[x]);
		      }
		}
		else
		{
	      str=str.trim();
	      str = str.replaceAll("\"", ""); //To Remove Double Quotes
	      String[] result = str.split("\\s+"); //To Remove Space and Split with space
	      
	      int len=result.length;

	      Token[] token=new Token[len];
	      for (int x=0; x<result.length; x++)
	      {
	    	 result[x]=result[x].replaceAll(","," ").trim();
	    	 token[x]=new Token();
	         token[x].setTermText(result[x]);
	         token[x].setTermBuffer(result[x].toCharArray());//Add Other Info If needed after words
	         token[x].merge(al);
	        
	      }
		}	
	      
	      
	      
	      TokenStream ts=new TokenStream();
	      ts.setTokenstream(al);
	      return ts;
	}
}
