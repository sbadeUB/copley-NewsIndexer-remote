package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterNumbers extends TokenFilter implements Analyzer
{
	public TokenFilterNumbers(TokenStream tokenStream)
	{
		super(tokenStream);
		TokenFilter.filterType=TokenFilterType.NUMERIC;
	}
public TokenFilter NumericProcessing(TokenStream ts)
{
	
	TokenFilter tfs=null;
	String str=null;
		str=ts.getCurrent().getTermText();
		
	if(!str.matches("^[a-zA-Z.]*$") && !str.matches("^(2[0-3]|1[0-9]|0[0-9]):[0-5][0-9]:[0-5][0-9]([A-Za-z.]*)$") && !str.matches("^-?\\d{8}(.)?$") && !str.matches("^\\d{8}-\\d{8}(.)?$")) 
	{
		str=str.replaceAll("[0-9]","");
		str=str.replace(".", "");
		str=str.replace(",", "");
	}
		
		str=str.trim();
		
		if(!str.isEmpty())
		{
			ts.getCurrent().setTermText(str);
			ts.getCurrent().setTermBuffer(str.toCharArray());
		}
		else
		{
			ts.remove();
			TokenFilter.IsTokenRemoved=true;
		}
	
		tfs =new TokenFilterNumbers(ts);
		return tfs;
	
}

	
}
