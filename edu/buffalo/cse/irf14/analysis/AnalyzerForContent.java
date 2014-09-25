/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * To get TokenFilters for Each given field from AnalyzerFactory
 * @author Bade
 *
 */
public class AnalyzerForContent extends TokenFilter implements Analyzer{
	
	/**
	 * Default constructor, creates an instance over the given
	 * TokenFilterGetter
	 * 
	 */
	
	public AnalyzerForContent(TokenStream ts) {
		super(ts);
	}
	
	public TokenStream ProcessTokenonFilterTypes(TokenStream ts)
	{
		TokenFilter tf=null;
		TokenFilterFactory tff=TokenFilterFactory.getInstance();
			tf=tff.getFilterByType(TokenFilterType.DATE, ts);
			ts=tf.getStream();
			if(IsTokenRemoved==false)
			tf=tff.getFilterByType(TokenFilterType.NUMERIC, ts);
			ts=tf.getStream();
			if(IsTokenRemoved==false)		
			tf=tff.getFilterByType(TokenFilterType.SYMBOL, ts);
			ts=tf.getStream();
			if(IsTokenRemoved==false)
			tf=tff.getFilterByType(TokenFilterType.STOPWORD, ts);
			ts=tf.getStream();
			System.out.println("Token OUT:"+ts.getCurrent().getTermText());
			return ts;
		
	}
	
	
}
