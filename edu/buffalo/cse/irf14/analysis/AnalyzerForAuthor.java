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
	ts=tfforauth.getStream();
	/*if(IsTokenRemoved==false)
	{
		TokenFilterCapitalization tfc=(TokenFilterCapitalization)tff.getFilterByType(TokenFilterType.CAPITALIZATION, ts);
		 tfforauth=tfc.capitalizationProcessing(ts);
	ts=tfforauth.getStream();
	}*/
	System.out.println("TOKEN OUT:"+ts.getCurrent().getTermText());
	TokenFilter.AnalyzerType=2;
	return ts;
		
	}
}
