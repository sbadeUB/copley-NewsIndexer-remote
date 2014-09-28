package edu.buffalo.cse.irf14.analysis;

public class AnalyzerForPlace extends TokenFilter implements Analyzer {
	public AnalyzerForPlace(TokenStream ts)
	{
	super(ts);
	}
	public TokenStream ProcessTokenonFilterTypes(TokenStream ts)
	{
		TokenFilter tfforplace= null;
	TokenFilterFactory tff=TokenFilterFactory.getInstance();
	TokenFilterSymbol tffo=(TokenFilterSymbol)tff.getFilterByType(TokenFilterType.SYMBOL, ts);
	tfforplace=tffo.symbolProcessing(ts);
	ts=tfforplace.getStream();
	System.out.println("TOKEN OUT:"+ts.getCurrent().getTermText());
	TokenFilter.AnalyzerType=4;
	return ts;
	}

}
