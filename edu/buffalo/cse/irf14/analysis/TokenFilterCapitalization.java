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
	TokenFilter tfs=null;
	String str=null;
		try
		{
			str=ts.getCurrent().getTermText().trim();
			int j=0;
			/*String capital="[A-Z]+";
			if(Pattern.matches(capital, str))
			{
                  j=str.length()-1;
			}*/
			
			char[] charArray=str.toCharArray();
			//String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
			for(int i=0;i<str.length();i++)
			{
				if(Character.isLowerCase(charArray[i]))
				{
					break;
				}
				else j=j+1;
			}
			
			if(j!=str.length())
			{
				String camel="[A-Z]([a-z$&+,:;=?@#|'<>.-^*()%!])+";//check next token
				String camel2="[a-z]([A-Z])";
				if(Pattern.matches(camel, str))// add this |Pattern.matches(camel2, str)
				{
					
					  if(ts.hasNext() && !(str.endsWith(".") || str.endsWith("!")|| str.endsWith("?")))
					  {
						  String str2=ts.getNextTokenValue();
						  boolean setPunctuation=true;
						  if(Pattern.matches(camel, str2)|Pattern.matches(camel2, str2))	
						  {
							  str2=str2.trim();
							  	while(setPunctuation)
								{
									if(str2.endsWith(".")||str2.endsWith("!")||str2.endsWith("?"))
									{
										str2=str2.substring(0,str2.length()-1); //This will remove . or ! or ? that are at the end only
									}
									else setPunctuation=false;
								}
							  if(str2.endsWith("'s")) str2=str2.substring(0,str2.length()-2);
							  str2 = str2.replace("s\'$", "s");
							  str2 = str2.replace("\'$", "");
							  
							  str=str+" "+str2;
							  ts.getCurrent().setTermText(str);
							  ts.getCurrent().setTermBuffer(str.toCharArray());
							  ts.next();
							  ts.remove();
							  ts.previous();
							 // IsTokenRemoved=true;
							
						  }
						 
				  }
				  if(endFlag==true) str=str.toLowerCase();
				    
				}
				 else
			       {
				  //--retain--//
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
	
	if(!str.isEmpty() && ts.getCurrent()!=null)
	{
		ts.getCurrent().setTermText(str);
		ts.getCurrent().setTermBuffer(str.toCharArray());
	}
	else
	{
		ts.remove();
	}
		}
		catch(Exception e)
		{
			System.out.println("Thrown in TokenFilter Capitals!"+e.getMessage());
			e.printStackTrace();
		}
	
	tfs =new TokenFilterCapitalization(ts);
	return tfs;
}

	
}