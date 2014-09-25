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
	public static int AnalyzerType=1;
	public static boolean IsTokenRemoved=false;
	private TokenStream tokenStream=null;
	public TokenFilter(TokenStream tokenStream) {
		this.tokenStream=tokenStream;
		
	}
	
	public boolean increment() throws TokenizerException
	{
		boolean bool=true;
		TokenStream ts=this.getStream();
		while(ts.hasNext())
		{
			ts.next();
			IsTokenRemoved=false;
			switch(AnalyzerType)
			{
				case 1:
				{
					AnalyzerForContent AFC=new AnalyzerForContent(ts);
					System.out.println("Token IN:"+ts.getCurrent().getTermText());
					ts=AFC.ProcessTokenonFilterTypes(ts);
					
					break;
				}
				case 2:
				case 3:
				case 4:
				default:
				
			}
		}
		
		return false;
	}
	
	public TokenStream getStream()
	{
		
		
		return this.tokenStream;
	}
	
}
