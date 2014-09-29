package edu.buffalo.cse.irf14.analysis;

import java.util.Arrays;



/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterStopWords extends TokenFilter implements Analyzer
{
	static boolean endFlag=true;
	public TokenFilterStopWords(TokenStream tokenStream)
	{
		super(tokenStream);
		TokenFilter.filterType=TokenFilterType.STOPWORD;
	}
public TokenFilter stopWordProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String str=null;
try{
		str=ts.getCurrent().getTermText();
		
		String[] stopwords={"a","able","about","across","after","all","almost","also","am",
				"among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could",
				"dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he",
				"her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like",
				"likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or",
				"other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their",
				"them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when",
				"where","which","while","who","whom","why","will","with","would","yet","you","your"};
		
		String[] divideWords=str.split(" ");
		str="";
		for(String str2:divideWords)
		{
			if(Arrays.asList(stopwords).contains(str2))
			{
				//DO nothing
			}
			else
			{
				//Add to string 'str'
				str=str+str2;
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
			if(ts.getCurrent()!=null)
			{
			ts.remove();
			TokenFilter.IsTokenRemoved=true;
			}
		}
}catch(Exception e)
{
	System.out.println("Exception thrown in stopwords invalid file!"+e.getMessage());
}
	
	tfs =new TokenFilterStopWords(ts);
	return tfs;
}
	
}