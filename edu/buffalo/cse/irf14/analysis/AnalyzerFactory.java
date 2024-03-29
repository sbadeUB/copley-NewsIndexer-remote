/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * This factory class is responsible for instantiating "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {
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
	public static AnalyzerFactory getInstance() {
		AnalyzerFactory af=new AnalyzerFactory();
		return af;
	}
	
	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance
	 * for a given {@link FieldNames} field
	 * Note again that the singleton factory instance allows you to reuse
	 * {@link TokenFilter} instances if need be
	 * @param name: The {@link FieldNames} for which the {@link Analyzer}
	 * is requested
	 * @param TokenStream : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable {@link FieldNames}
	 * null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream)
	{
		Analyzer af=null;
		switch(name)
		{
			case AUTHOR:
			{
				int i=2;
				AnalyzerForAuthor afa=new AnalyzerForAuthor(stream);
				TokenFilter.AnalyzerType=i;
				af=(Analyzer)afa;
					break;
					
			}
			
			case PLACE:
			{
				int i=4;
				AnalyzerForPlace afc=new AnalyzerForPlace(stream);
				TokenFilter.AnalyzerType=i;
				af=(Analyzer)afc;
				break;
			}
			
			case CATEGORY:
			{
					break;
			}
			
			case CONTENT:
			{
				int i=1;
			AnalyzerForContent afc=new AnalyzerForContent(stream);
			TokenFilter.AnalyzerType=i;
			af=(Analyzer)afc;
					break;
			}
			
			case TITLE:
			{
				int i=1;
				AnalyzerForContent afa=new AnalyzerForContent(stream);
				TokenFilter.AnalyzerType=i;
				af=(Analyzer)afa;
				break;
			}
			case NEWSDATE:
			{
				int i=3;
				AnalyzerForNewsDate afd=new AnalyzerForNewsDate(stream);
				TokenFilter.AnalyzerType=i;
				af=(Analyzer)afd;
				break;
			}
			
		
			default:
			{
				break;
			}
	}
		return af;
}
}
