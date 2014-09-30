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
	try
	{
		TokenFilterAccents tfa=(TokenFilterAccents)tff.getFilterByType(TokenFilterType.ACCENT, ts);
		tf=tfa.accentsProcessing(ts);
		if(tf!=null)			
		ts=tf.getStream();
		    if(IsTokenRemoved==false && ts.getCurrent()!=null)
	      	{
			TokenFilterDates tfd=(TokenFilterDates)tff.getFilterByType(TokenFilterType.DATE, ts);		
			tf=tfd.datesProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
	    	}
			if(IsTokenRemoved==false && ts.getCurrent()!=null)
			{
			TokenFilterNumbers tfn=(TokenFilterNumbers)tff.getFilterByType(TokenFilterType.NUMERIC, ts);
			tf=tfn.NumericProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
			}
			
			if(IsTokenRemoved==false && ts.getCurrent()!=null)
			{
			TokenFilterSymbol tfs=(TokenFilterSymbol)tff.getFilterByType(TokenFilterType.SYMBOL, ts);
			tf=tfs.symbolProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
			}
			if(IsTokenRemoved==false && ts.getCurrent()!=null)
			{
			TokenFilterCapitalization tfc=(TokenFilterCapitalization)tff.getFilterByType(TokenFilterType.CAPITALIZATION, ts);
			tf=tfc.capitalizationProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
			}
			if(IsTokenRemoved==false && ts.getCurrent()!=null)
			{
			TokenFilterSpecialChars tfsc=(TokenFilterSpecialChars)tff.getFilterByType(TokenFilterType.SPECIALCHARS, ts);
			tf=tfsc.specialCharsProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
			}
			if(IsTokenRemoved==false && ts.getCurrent()!=null)
			{
			 TokenFilterStopWords tfst=(TokenFilterStopWords)tff.getFilterByType(TokenFilterType.STOPWORD, ts);
			tf=tfst.stopWordProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
			}
			if(IsTokenRemoved==false && ts.getCurrent()!=null)
			{
			TokenFilterStemmer tfs=(TokenFilterStemmer)tff.getFilterByType(TokenFilterType.STEMMER, ts);
			tf=tfs.stemmerProcessing(ts);
			if(tf!=null)
			ts=tf.getStream();
			}
			TokenFilter.AnalyzerType=1;
	}catch(Exception e)
	{
		System.out.println("Exception thrown analyzer for Content!"+e.getMessage());
		e.printStackTrace();
	}
			return ts;	
	}
}
