/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{
	ArrayList<Token> streamoftokens= new ArrayList<Token>();
	private ListIterator<Token> itr;
	public static boolean isLastTokenRemoved;
	public static int PreviousTokenindex;
	public static int iterPosMoved;
	
	public void setTokenstream(ArrayList<Token> listoftokens)
	{
		for(Token t: listoftokens)
		{
			streamoftokens.add(t);
			
		}
		itr=this.streamoftokens.listIterator();
		isLastTokenRemoved=false;
		PreviousTokenindex=-1;
		
	}
	public ArrayList<Token> getTokenstream()
	{
		
		return streamoftokens;
	}
	
	
	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		
		if(itr.hasNext())	return true;
		else				return false;
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		
		if(hasNext())
		{
			Token token = itr.next();
			isLastTokenRemoved=false;
			PreviousTokenindex=PreviousTokenindex+1;
			return token;
		}
		else 
			{
				PreviousTokenindex=PreviousTokenindex+1;
				return null;
			}
	}
	
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		try
		{
			if(getCurrent()!=null)
			{
				this.itr.remove();
				isLastTokenRemoved=true;
				PreviousTokenindex=PreviousTokenindex-1;
			}
		}catch(Exception e)
		{
			System.out.println("Exception thrown!"+e.getMessage());
		}
		//NO-OP
	}
	
	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		
		itr=this.streamoftokens.listIterator();
		isLastTokenRemoved=false;
		PreviousTokenindex=-1;
		
	}
	
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream)
	{
		if(stream!=null)
		{
			iterPosMoved=0;
			while(this.itr.hasNext())
			{
				this.itr.next();
				iterPosMoved=iterPosMoved+1;
			}
		
			stream.reset();
			while(stream.hasNext())
			{
				Token t=stream.next();
				this.itr.add(t);
				iterPosMoved=iterPosMoved+1;
			}
			while(iterPosMoved>0)
			{
				this.itr.previous();
				iterPosMoved=iterPosMoved-1;
			}
		}
		
	}
	
	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		if(isLastTokenRemoved) return null;
		else if(PreviousTokenindex>=this.streamoftokens.size()) return null;
		int x= itr.previousIndex();
		if(x!=-1)  return this.streamoftokens.get(x);
        else    return null;
	}
	
	/**To get Next term values which can be used in Token Filters like
	 *  Dates and Capitalization
	 */
	 public String getNextTokenValue(){
		 int x=itr.nextIndex();
		 if(x!=-1)
			 return this.streamoftokens.get(x).getTermText();
		 else return null;
	 }
	 
	 /**To move to previous term which can be used in Token Filters like
		 *  Dates and Capitalization
		 */
		 public Token previous()
		 {
			 if(itr.hasPrevious()) 
			{
				 Token tkn=this.itr.previous();
				isLastTokenRemoved=false;
				PreviousTokenindex=PreviousTokenindex-1;
				this.previous();
				this.next();
				return tkn;
			}
			 else return null;
			
		 }
	
}
