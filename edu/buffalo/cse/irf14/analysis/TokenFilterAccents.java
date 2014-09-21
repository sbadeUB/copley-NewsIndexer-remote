package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterAccents extends TokenFilter implements Analyzer
{
	public TokenFilterAccents(TokenStream tokenStream)
	{
		super(tokenStream);
	}
public TokenFilter accentsProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	ArrayList<Token> tremoval= new ArrayList<Token>();
	String[] strArray=null;
	for(Token t:ts.streamoftokens)
	{
		String str=t.getTermText();
		if(str.endsWith(".")||str.endsWith("!")||str.endsWith("?"))
		{
			str=str.substring(0, str.length()-1); //This will remove . or ! or ? that are at the end only
		} 
		
		str = str.replace("n\'t", " not"); //Common Contractions and expansions for each
		str = str.replace("\'ve", " have");
		str = str.replace("\'d", " would");
		str = str.replace("\'ll", " will");
		str = str.replace("\'m", " am");
		str = str.replace("\'am", "dam");
		str = str.replace("\'re", " are");
		str = str.replace("y\'", "you ");
		str = str.replace("Y\'", "You ");
		str = str.replace("\'em", "them");
		
		str = str.replace("\'s", "");     //This will remove apostrophes with only 's or s' or '
		str = str.replace("s\'", "");
		str = str.replace("\'", " ");
		
		
		
		
		if(str.matches("^[[a-zA-Z]+[-][a-zA-Z]+]+$"))//To return week-day to week day
		{
			strArray=str.split("-");
			str="";
			for(int i=0;i<strArray.length;i++)
			str=str+" "+strArray[i];
			str=str.trim();
		}
		else if(str.matches("^[[a-zA-Z]+[-][0-9]+]+$") || str.matches("^[[0-9]+[-][0-9]+]+$") || str.matches("^[[0-9]+[-][a-zA-Z]+]+$"))
		{          }                                 //To retain B-52 or 23-52 or 52-B
			
		
		str=str.trim();
		
	t.setTermText(str);
	tremoval.add(t);
	}
	TokenStream tss=new TokenStream();
	tss.setTokenstream(tremoval);
	tfs =new TokenFilterSymbol(tss);
	return tfs;
}

	public TokenStream getStream()
	{
		
		//writing code
	return null;
	}
	public  boolean increment()
	{
	return false;
	}
	
}
