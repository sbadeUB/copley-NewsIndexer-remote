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
	
	TokenFilterNumbers tffn=(TokenFilterNumbers)tff.getFilterByType(TokenFilterType.NUMERIC, ts);
	tfforplace=tffn.NumericProcessing(ts);
	if(tfforplace!=null)
	ts=tfforplace.getStream();
	
	
	TokenFilterSymbol tffo=(TokenFilterSymbol)tff.getFilterByType(TokenFilterType.SYMBOL, ts);
	tfforplace=tffo.symbolProcessing(ts);
	if(tfforplace!=null)
	ts=tfforplace.getStream();
	
	TokenFilterSpecialChars tffsc=(TokenFilterSpecialChars)tff.getFilterByType(TokenFilterType.SPECIALCHARS, ts);
	tfforplace=tffsc.specialCharsProcessing(ts);
	if(tfforplace!=null)
	ts=tfforplace.getStream();
	TokenFilter.AnalyzerType=4;
	return ts;
	}

}
