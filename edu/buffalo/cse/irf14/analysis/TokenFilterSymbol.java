/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;




/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterSymbol extends TokenFilter implements Analyzer
{
	public TokenFilterSymbol(TokenStream tokenStream)
	{
		super(tokenStream);
		TokenFilter.filterType=TokenFilterType.SYMBOL;
	}
public TokenFilter symbolProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String[] strArray=null;
	boolean setPunctuation=true;
	String str=ts.getCurrent().getTermText();
		while(setPunctuation)
		{
			if(str.endsWith(".")||str.endsWith("!")||str.endsWith("?"))
			{
				str=str.substring(0, str.length()-1); //This will remove . or ! or ? that are at the end only
			}
			else setPunctuation=false;
		}
		
		str = str.replace("\'s$", "");     //This will remove apostrophes with only 's or s' or '
		str = str.replace("s\'$", "s");
		
		
		str = str.replace("won\'t", " will not");
		str = str.replace("shan\'t", " shall not");
		str = str.replace("n\'t", " not"); //Common Contractions and expansions for each
		str = str.replace("\'ve", " have");
		str = str.replace("\'d", " would");
		str = str.replace("\'ll", " will");
		str = str.replace("\'m", " am");
		str = str.replace("\'am", "dam");
		str = str.replace("\'re", " are");
		str = str.replace("^y\'", "you ");
		str = str.replace("^Y\'", "You ");
		if(str.equals("\'em"))
		{
			str="them";
		}
		str = str.replace("\'", "");
		
		
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
		
	if(!str.isEmpty())
	{
		ts.getCurrent().setTermText(str);
		ts.getCurrent().setTermBuffer(str.toCharArray());
	}
	else
	{
		ts.remove();
		TokenFilter.IsTokenRemoved=true;
		/*try {
			tfs =new TokenFilterSymbol(ts);
			boolean ac=tfs.increment();
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			System.out.println("Some errors occured");
			e.printStackTrace();
		}*/
	}
	
	
	
	/*for(Token t :tss.getTokenstream())
		System.out.println(t.getTermText());*/
	//ts.reset();
	tfs =new TokenFilterSymbol(ts);
	
	return tfs;
}

}
