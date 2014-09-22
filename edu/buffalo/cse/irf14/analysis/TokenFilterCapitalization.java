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
	}
public TokenFilter capitalizationProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String str=null;
	if(ts.hasNext())
	{
		str=ts.next().getTermText();
		String camel="[A-Z]([a-z$&+,:;=?@#|'<>.-^*()%!])+";//check next token
		if(Pattern.matches(camel, str))
		{
		  System.out.println("Camel Cased:"+str);
		  if(ts.hasNext() && !(str.endsWith(".") || str.endsWith("!")|| str.endsWith("?")))
		  {
			  String str2=ts.getNextTokenValue();                
			  if(Pattern.matches(camel, str2))	
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