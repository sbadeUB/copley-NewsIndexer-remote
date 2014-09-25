package edu.buffalo.cse.irf14.analysis;

public class AnalyzerForAuthor {

	public AnalyzerForAuthor(TokenStream ts)
	{
	
	}
	public TokenStream ProcessTokenonFilterTypes(TokenStream ts)
	{
		TokenFilter tfforauth= null;
	TokenFilterFactory tff=TokenFilterFactory.getInstance();
	tfforauth=tff.getFilterByType(TokenFilterType.ACCENT, ts);
	ts=tfforauth.getStream();
	return ts;
		
	}
}
