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
		
	      str=str.trim();
	      str = str.replaceAll("\"", ""); //To Remove Double Quotes
	      String[] result = str.split("\\s+"); //To Remove Space and Split with space
	      
	      int len=result.length;
	      ArrayList<Token> al = new ArrayList<Token>();
	      Token[] token=new Token[len];
	      for (int x=0; x<result.length; x++)
	      {
	    	 result[x]=result[x].replaceAll(","," ").trim();
	    	 token[x]=new Token();
	         token[x].setTermText(result[x]);
	         token[x].setTermBuffer(result[x].toCharArray());//Add Other Info If needed after words
	         al.add(token[x]);
	      }
	      
	      /*ArrayList<Token> al2 = new ArrayList<Token>();
	      Token[] tokensDemo=new Token[6];
	      for(int i=0;i<6;i++)
	      {
	    	  tokensDemo[i]=new Token();
	    	  int j =i*10;
	    	  tokensDemo[i].setTermText(String.valueOf(j));
	    	  al2.add(tokensDemo[i]);
	      }

TokenStream ts2=new TokenStream();
	      ts2.setTokenstream(al2);
	      for(int i=0;i<6;i++)
	      {
	    	  boolean x=ts2.hasNext();
	    	  System.out.println(x);
	      }
	      System.out.println("--Now tokens---"); 
	      for(int i=0;i<2;i++)
	      {
	    	 Token tk=ts2.next();
	    	  System.out.println(tk.getTermText());
	      }
	      System.out.println("Hi");
	      for(int i=0;i<2;i++)
	      {
	    	 Token tk=ts2.next();
	    	  System.out.println(tk.getTermText());
	      }
    	  
	      System.out.println("--Now resetting--");
	      ts2.reset();
	      
	      System.out.println("--Now token forwarded  once---"); 
	      Token tk=ts2.next();
    	  System.out.println(tk.getTermText());
	      
    	  System.out.println("--Now geting current token---");
	      for(int i=0;i<6;i++)
	      {
	    	  Token tk3=ts2.getCurrent();
	    	  System.out.println(tk3.getTermText());
	      }
	      
	      ts2.next();
	      System.out.println("--Now removing the same token---");
	      ts2.remove();
	      
	     System.out.println("After Removing Current Token text :"+ts2.getCurrent().getTermText()); 
	      
	      System.out.println("--Now resetting--");
	      ts2.reset();
	      
	      System.out.println("--Now tokens---"); 
	      while(ts2.hasNext())
	      {
	    	 Token tk4=ts2.next();
	    	  System.out.println(tk4.getTermText());
	      }
	      ts2.reset();
	      ts2.next();
	      System.out.println("from new method next token value:"+ts2.getNextTokenValue());
	      //System.out.println("from new method next token value:"+ts2.getNextTokenValue());
	      System.out.println("from new method next token value:"+ts2.getCurrent().getTermText());
	      System.out.println("--Now tokens---"); 
	      
	    	 Token tk4=ts2.next();
	    	  System.out.println(tk4.getTermText());
	    	  System.out.println("from new method next token value:"+ts2.getCurrent().getTermText());
	    	System.out.println("from new method next token value:"+ts2.getNextTokenValue());
	      */
	      
	      TokenStream ts=new TokenStream();
	      ts.setTokenstream(al);
	      return ts;
	}
}
