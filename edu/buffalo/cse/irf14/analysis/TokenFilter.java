/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * The abstract class that you must extend when implementing your 
 * TokenFilter rule implementations.
 * Apart from the inherited Analyzer methods, we would use the 
 * inherited constructor (as defined here) to test your code.
 * @author nikhillo
 *
 */
public abstract class TokenFilter implements Analyzer {
	
	/**
	 * Default constructor, creates an instance over the given
	 * TokenStream
	 * @param stream : The given TokenStream instance
	 */
	
	private TokenStream tokenStream=null;
	public TokenFilter(TokenStream tokenStream) {
		this.tokenStream=tokenStream;
	}
	
	public boolean increment() throws TokenizerException
	{
		boolean bool=false;
		
		return bool;
	}
	
	public TokenStream getStream()
	{
		
		
		return this.tokenStream;
	}
	
}
