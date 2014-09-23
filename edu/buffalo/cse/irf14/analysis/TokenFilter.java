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
	
	@SuppressWarnings("null")
	public boolean increment() throws TokenizerException
	{
		TokenFilterGetter tfg=null;
		TokenFilter tf=null;
		boolean bool=false;
		while(this.tokenStream.hasNext())
		{
			tf=tfg.ProcessTokenOnFilterTypes(this.tokenStream);
		}
		
		return bool;
	}
	
	public TokenStream getStream()
	{
		
		
		return this.tokenStream;
	}
	
}
