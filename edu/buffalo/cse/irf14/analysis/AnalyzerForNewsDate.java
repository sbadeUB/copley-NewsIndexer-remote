package edu.buffalo.cse.irf14.analysis;

public class AnalyzerForNewsDate extends TokenFilter implements Analyzer {
	public AnalyzerForNewsDate(TokenStream ts)
	{
	super(ts);
	
	}
	public TokenStream ProcessTokenonFilterTypes(TokenStream ts)
	{
		TokenFilter tf= null;
	TokenFilterFactory tff=TokenFilterFactory.getInstance();
	TokenFilterDates tffo=(TokenFilterDates)tff.getFilterByType(TokenFilterType.DATE, ts);
	tf=tffo.datesProcessing(ts);
	if(tf!=null)
	ts=tf.getStream();
	TokenFilter.AnalyzerType=3;
	return ts;
	}

}
