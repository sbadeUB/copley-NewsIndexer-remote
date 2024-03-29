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
		TokenFilter.filterType=TokenFilterType.SPECIALCHARS;
	}
public TokenFilter specialCharsProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String str=null;

		str=ts.getCurrent().getTermText();
		String regex="[0-9]{1}\\/[0-9]{2}\\/[0-9]{2}";
		if(!str.matches(regex))
		{
			if(str.matches("^[[a-zA-Z]+[-][a-zA-Z]+]+$"))
			{
				str=str.replaceAll("[^a-zA-Z0-9\\?\\!\\.]+","");			
			}
			else
			{
				String[] sdc=str.split(" ");
				str="";
				for(int i=0;i<sdc.length;i++)
				{
					sdc[i]=sdc[i].replaceAll("[^a-zA-Z0-9\\?\\!\\.\\-]+","");//Look it will remove spaces
					str=str+sdc[i]+" ";
				}
				str=str.trim();
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
