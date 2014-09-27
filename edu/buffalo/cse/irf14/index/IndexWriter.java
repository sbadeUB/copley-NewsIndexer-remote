/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
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
	

	public static ArrayList<String> TermList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> TermPostingslist = new ArrayList<HashMap<String,Integer>>();
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
		
		
		String[] content=d.getField(FieldNames.CONTENT);
		Tokenizer tokenizer=new Tokenizer();
		
		try {
			TokenStream tokenStream=tokenizer.consume(content[0]);
			int i=0;
			AnalyzerFactory af=AnalyzerFactory.getInstance();
			Analyzer a=af.getAnalyzerForField(FieldNames.CONTENT, tokenStream);
			while(a.increment())
			{
			
			}
			tokenStream=a.getStream();
			tokenStream.reset();
			
		/*	String[] totalwords= new String[500];
			int k=0;
			
			for(Token t:tokenStream.getTokenstream())
			{
				totalwords[k]=t.getTermText();
				k++;
			}
			Set<String> uniquewords = new HashSet<String>(Arrays.asList(totalwords));
			//if(uniquewords.equals(null))
				uniquewords.remove(null);
		for(String s:uniquewords)
		{
			int count=0;
			for(Token t:tokenStream.getTokenstream())
			{
				if(s.equals(t.getTermText()))
				{
                       count++; 
			     }
			}
				temp.put(d.getField(FieldNames.FILEID)[0], count);
				retlist.add(temp);
				System.out.println("term "+s+" count"+count);
				System.out.println("list count"+retlist.size());
		}*/
			Map<String,Integer> tm = new TreeMap<String,Integer>();
			for(Token t:tokenStream.getTokenstream())
			{
				String nextElement=t.getTermText();
				if(tm.size()>0 && tm.containsKey(nextElement))
				{
                 int val = 0;
                 if(tm.get(nextElement)!= null)
                 {
                  val = (Integer) tm.get(nextElement);
                  val = val+1;
                 }
                tm.put(nextElement, val);
				}
				else
				{
					tm.put(nextElement, 1);
				}
			}
			for(Map.Entry<String,Integer> entry : tm.entrySet()) 
			{
				HashMap<String, Integer> temp = new HashMap<String, Integer>();
				if(TermList.contains(entry.getKey()))
				{
					int index=TermList.indexOf(entry.getKey());
					temp=TermPostingslist.get(index);
					temp.put(d.getField(FieldNames.FILEID)[0], entry.getValue());
					TermPostingslist.add(index, temp);
				}
				else
				{
					TermList.add(entry.getKey());
					temp.put(d.getField(FieldNames.FILEID)[0], entry.getValue());
					System.out.println(entry.getKey() + " : " + entry.getValue());
					TermPostingslist.add(temp);
				}
	        }
			
			
			
			
			
			
			
			//System.out.println("final tokens :"+i);
			/*TokenFilterFactory tff=TokenFilterFactory.getInstance();
			TokenFilter tfs=tff.getFilterByType(TokenFilterType.SYMBOL, tokenStream);
			TokenFilter tfs=tff.getFilterByType(TokenFilterType.SYMBOL, tokenStream);
			TokenStream ts2=tfs.getStream();
			ts2.reset();
			
			TokenFilter tfd=tff.getFilterByType(TokenFilterType.DATE, tokenStream);
			TokenStream ts3=tfd.getStream();
			ts3.reset();
			
			while(ts3.hasNext())
			{
				System.out.println(ts3.next().getTermText()+" ");
			}
			ts3.reset();
			
			TokenFilter tfn=tff.getFilterByType(TokenFilterType.NUMERIC, tfd.getStream());
			TokenStream ts4=tfn.getStream();
			ts4.reset();
			while(ts4.hasNext())
			{
				System.out.println(ts4.next().getTermText()+" ");
			}
			
			TokenFilter tfd=tff.getFilterByType(TokenFilterType.STEMMER, tokenStream);
			TokenStream ts3=tfd.getStream();
			ts3.reset();
			while(ts3.hasNext())
			{
				System.out.println(ts3.next().getTermText()+" ");
			}*/
			
			
		}catch (TokenizerException e) {
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
