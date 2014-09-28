/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenFilterFactory tff=new TokenFilterFactory();
		return tff;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenFilter.AnalyzerType=-1;//Single TokenFilter Instances
		TokenFilter tf=null;
		switch(type)
		{
			case SYMBOL:
			{
					TokenFilterSymbol tfs=new TokenFilterSymbol(stream);
					tf=(TokenFilterSymbol)tfs;
					break;
					
					
			}
			case ACCENT:
			{
					TokenFilterAccents tfa=new TokenFilterAccents(stream);
					tf=(TokenFilterAccents)tfa;
					break;
			}
			case SPECIALCHARS:
			{
					TokenFilterSpecialChars tfsp=new TokenFilterSpecialChars(stream);
					tf=(TokenFilterSpecialChars)tfsp;
					break;
			}
			
			case DATE:
			{
				TokenFilterDates tfd=new TokenFilterDates(stream);
				tf=(TokenFilterDates)tfd;
				break;
			}
			
			case NUMERIC:
			{
					TokenFilterNumbers tfn=new TokenFilterNumbers(stream);
					tf=(TokenFilterNumbers)tfn;
					break;
			}
			case CAPITALIZATION:
			{
				TokenFilterCapitalization tfc=new TokenFilterCapitalization(stream);
					tf=(TokenFilterCapitalization)tfc;
				break;
			}
			
			case STOPWORD:
			{
				TokenFilterStopWords tfst=new TokenFilterStopWords(stream);
				tf=(TokenFilterStopWords)tfst;
				break;
			}
			case STEMMER:
			{
				TokenFilterStemmer tfste=new TokenFilterStemmer(stream);
				tf=(TokenFilterStemmer)tfste;
				break;
			}
			
		default:
			break;
		}
		return tf;
	
	}
}
