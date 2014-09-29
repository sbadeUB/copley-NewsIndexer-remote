/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * The abstract class that you must extend when implementing your 
 * TokenFilter rule implementations.
 * Apart from the inherited Analyzer methods, we would use the 
 * inherited constructor (as defined here) to test your code.
 * @author nikhillo
 *
 */
public abstract class TokenFilter implements Analyzer {
	
	/**
	 * Default constructor, creates an instance over the given
	 * TokenStream
	 * @param stream : The given TokenStream instance
	 */
	public static int AnalyzerType=1;
	public static boolean IsTokenRemoved=false;
	public static TokenFilterType filterType;
	private TokenStream tokenStream=null;
	public TokenFilter(TokenStream tokenStream) {
		this.tokenStream=tokenStream;
		
	}
	
	public boolean increment() throws TokenizerException
	{
		TokenStream ts=this.getStream();
		while(ts.hasNext())
		{
			ts.next();
			IsTokenRemoved=false;
			switch(AnalyzerType)
			{
				case -1:
				{
					if(filterType.equals(TokenFilterType.SYMBOL))
					{
					TokenFilterSymbol tfs=(TokenFilterSymbol)this;
					tfs.symbolProcessing(ts);
					}
					if(filterType.equals(TokenFilterType.ACCENT))
					{
					TokenFilterAccents tfs=(TokenFilterAccents)this;
					tfs.accentsProcessing(ts);
					}
       					if(filterType.equals(TokenFilterType.CAPITALIZATION))
					{
					TokenFilterCapitalization tfs=(TokenFilterCapitalization)this;
					tfs.capitalizationProcessing(ts);
					}
					if(filterType.equals(TokenFilterType.DATE))
					{
					TokenFilterDates tfs=(TokenFilterDates)this;
					tfs.datesProcessing(ts);
					}
					if(filterType.equals(TokenFilterType.NUMERIC))
					{
					TokenFilterNumbers tfs=(TokenFilterNumbers)this;
					tfs.NumericProcessing(ts);
					}
					if(filterType.equals(TokenFilterType.SPECIALCHARS))
					{
					TokenFilterSpecialChars tfs=(TokenFilterSpecialChars)this;
					tfs.specialCharsProcessing(ts);
					}
					if(filterType.equals(TokenFilterType.STEMMER))
					{
					TokenFilterStemmer tfs=(TokenFilterStemmer)this;
					tfs.stemmerProcessing(ts);
					}
					if(filterType.equals(TokenFilterType.STOPWORD))
					{
					TokenFilterStopWords tfs=(TokenFilterStopWords)this;
					tfs.stopWordProcessing(ts);
					}
				
					break;
				}
				case 1:
				{
					AnalyzerForContent AFC=new AnalyzerForContent(ts);
					//System.out.println("Token IN:"+ts.getCurrent().getTermText());
					ts=AFC.ProcessTokenonFilterTypes(ts);
					break;
				}
				case 2:
				{
					AnalyzerForAuthor AFA=new AnalyzerForAuthor(ts);
					//System.out.println("Token IN:"+ts.getCurrent().getTermText());
					ts=AFA.ProcessTokenonFilterTypes(ts);
					break;
				}
				case 3:
				{
					AnalyzerForNewsDate AFA=new AnalyzerForNewsDate(ts);
					//System.out.println("Token IN:"+ts.getCurrent().getTermText());
					ts=AFA.ProcessTokenonFilterTypes(ts);
					break;
				}
				case 4:
				{
					AnalyzerForPlace AFP=new AnalyzerForPlace(ts);
					//System.out.println("Token IN:"+ts.getCurrent().getTermText());
					ts=AFP.ProcessTokenonFilterTypes(ts);
					break;
				}
				
					
				default:
				{
					break;
				}
				
			}
		}
		
		return false;
	}
	
	public TokenStream getStream()
	{
		
		
		return this.tokenStream;
	}
	
}
