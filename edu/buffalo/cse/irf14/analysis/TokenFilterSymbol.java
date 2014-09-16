/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterSymbol implements Analyzer
{
	public TokenFilterSymbol()
	{
		
	}
public TokenFilter symbolProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	ArrayList<Token> tremoval= new ArrayList<Token>();
	for(Token t:ts.streamoftokens)
	{
		String str=t.getTermText();
		str.replaceAll("[^\\w]+"," ");
	
	t.setTermText(str);
	tremoval.add(t);
	}
	TokenStream tss=new TokenStream();
	tss.setTokenstream(tremoval);
	for(Token t :tss.getTokenstream())
		System.out.println(t.getTermText());
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
