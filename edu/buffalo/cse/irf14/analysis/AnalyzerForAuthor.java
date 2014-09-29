package edu.buffalo.cse.irf14.analysis;

public class AnalyzerForAuthor extends TokenFilter implements Analyzer {

	public AnalyzerForAuthor(TokenStream ts)
	{
	super(ts);
	}
	public TokenStream ProcessTokenonFilterTypes(TokenStream ts)
	{
		TokenFilter tfforauth= null;
	TokenFilterFactory tff=TokenFilterFactory.getInstance();
	TokenFilterAccents tfa=(TokenFilterAccents)tff.getFilterByType(TokenFilterType.ACCENT, ts);
	tfforauth=tfa.accentsProcessing(ts);
	if(tfforauth!=null)
	ts=tfforauth.getStream();
	TokenFilter.AnalyzerType=2;
	return ts;
		
	}
}
