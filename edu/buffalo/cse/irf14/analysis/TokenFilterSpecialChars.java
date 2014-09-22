package edu.buffalo.cse.irf14.analysis;

/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterSpecialChars extends TokenFilter implements Analyzer
{
	public TokenFilterSpecialChars(TokenStream tokenStream)
	{
		super(tokenStream);
	}
public TokenFilter specialCharsProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String str=null;
	if(ts.hasNext())
	{
		str=ts.next().getTermText();
		String regex="[0-9]{1}\\/[0-9]{2}\\/[0-9]{2}";
		if(!str.matches(regex))
		{
			str=str.replaceAll("[^a-zA-Z0-9\\?\\!\\.\\-]+","");//Look it will remove spaces
		}
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
	}
	
	tfs =new TokenFilterSpecialChars(ts);
	return tfs;
}

	
}
