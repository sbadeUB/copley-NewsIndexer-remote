/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterFactory;
import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
	}
	
	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		
		/*String[] xyz=d.getField(FieldNames.FILEID);
		//System.out.println(xyz[0]);
		d.getField(FieldNames.FILEID);
		d.getField(FieldNames.CATEGORY);
		d.getField(FieldNames.TITLE);
		d.getField(FieldNames.AUTHOR);
		d.getField(FieldNames.AUTHORORG);
		d.getField(FieldNames.PLACE);
		d.getField(FieldNames.NEWSDATE);*/
		/*String[] fileId=d.getField(FieldNames.FILEID);
		String[] category=d.getField(FieldNames.CATEGORY);
		String[] title=d.getField(FieldNames.TITLE);
		String[] author=d.getField(FieldNames.AUTHOR);
		String[] authororg=d.getField(FieldNames.AUTHORORG);
		String[] place=d.getField(FieldNames.PLACE);
		String[] newsDate=d.getField(FieldNames.NEWSDATE);*/
		String[] content=d.getField(FieldNames.CONTENT);
		
		Tokenizer tokenizer=new Tokenizer();
		
		try {
			TokenStream tokenStream=tokenizer.consume(content[0]);
			TokenFilterFactory tff=TokenFilterFactory.getInstance();
			TokenFilter tfs=tff.getFilterByType(TokenFilterType.SYMBOL, tokenStream);
			TokenStream ts2=tfs.getStream();
			ts2.reset();
			int i=1;
			while(ts2.hasNext())
			{
				System.out.println("Token "+i+"::"+ts2.next().getTermText());
				i=i+1;
			}
			ts2.reset();

			/*TokenFilter tfd=tff.getFilterByType(TokenFilterType.DATE, tfs.getStream());
			TokenStream ts3=tfd.getStream();
			ts3.reset();
			while(ts3.hasNext())
			{
				System.out.print(ts3.next().getTermText()+" ");
			}
			*/
			} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
	}
}
