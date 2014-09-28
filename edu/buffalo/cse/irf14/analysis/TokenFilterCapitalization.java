package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;

/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterCapitalization extends TokenFilter implements Analyzer
{
	static boolean endFlag=true;
	public TokenFilterCapitalization(TokenStream tokenStream)
	{
		super(tokenStream);
		TokenFilter.filterType=TokenFilterType.CAPITALIZATION;
	}
public TokenFilter capitalizationProcessing(TokenStream ts)
{
	//TokenFilter.AnalyzerType=3;//for testing
	TokenFilter tfs=null;
	String str=null;

		str=ts.getCurrent().getTermText();
		String camel="[A-Z]([a-z$&+,:;=?@#|'<>.-^*()%!])+";//check next token
		String camel2="[a-z]([A-Z])";
		if(Pattern.matches(camel, str))// add this |Pattern.matches(camel2, str)
		{
		  System.out.println("Camel Cased:"+str);
		  if(ts.hasNext() && !(str.endsWith(".") || str.endsWith("!")|| str.endsWith("?")))
		  {
			  String str2=ts.getNextTokenValue();          
			 // boolean cam=Pattern.matches(camel2, str2);//for testing
			  if(Pattern.matches(camel, str2)|Pattern.matches(camel2, str2))	
			  {
				  str=str+" "+str2;
				  ts.next();
				  ts.remove();
			  }
		  }
		  if(endFlag==true) str=str.toLowerCase();
		  else
		  {
			  //--retain--//
		  }
		}
		
		
		else
		{
			String capital="[A-Z]+";
			if(Pattern.matches(capital, str))
			{
				System.out.println("ALL Capital Cased:"+str); //do nothing
			}
			else
			{
				
				str=str.toLowerCase();
			}
		}
		
		if(str.endsWith(".") || str.endsWith("!")|| str.endsWith("?"))
		{
			endFlag= true;
		}
		else
		{
			endFlag=false;
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
	
	tfs =new TokenFilterCapitalization(ts);
	return tfs;
}

	
}