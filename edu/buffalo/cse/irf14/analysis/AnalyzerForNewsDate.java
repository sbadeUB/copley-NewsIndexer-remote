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
	ts=tf.getStream();
	System.out.println("TOKEN OUT:"+ts.getCurrent().getTermText());
	TokenFilter.AnalyzerType=3;
	return ts;
	}

}
