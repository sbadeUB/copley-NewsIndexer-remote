/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * To get TokenFilters for Each given field from AnalyzerFactory
 * @author Bade
 *
 */
public abstract class TokenFilterGetter implements Analyzer{
	
	/**
	 * Default constructor, creates an instance over the given
	 * TokenFilterGetter
	 * 
	 */
	
	public TokenFilterGetter() {
	}
	/*public TokenFilter ProcessTokenonFilterTypes(TokenStream ts)
	{
		TokenFilter tf= null;
		TokenFilterFactory tff=TokenFilterFactory.getInstance();
		if(ts.hasNext())
		{
			tf=tff.getFilterByType(TokenFilterType.SYMBOL, ts);
			tf=tff.getFilterByType(TokenFilterType.STOPWORD, ts);
		}
		return tf;
	}*/
	
	
}
