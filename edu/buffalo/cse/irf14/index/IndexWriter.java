/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	
	public String indexDir=null;
	public static ArrayList<String> TermList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> TermPostingslist = new ArrayList<HashMap<String,Integer>>();
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		this.indexDir=indexDir;
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
			AnalyzerFactory af=AnalyzerFactory.getInstance();
			Analyzer a=af.getAnalyzerForField(FieldNames.CONTENT, tokenStream);
			while(a.increment())
			{
			
			}
			tokenStream=a.getStream();
			tokenStream.reset();
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
				}
				else
				{
					TermList.add(entry.getKey());
					temp.put(d.getField(FieldNames.FILEID)[0], entry.getValue());
					System.out.println(entry.getKey() + " : " + entry.getValue());
					TermPostingslist.add(temp);
				}
	        }
			
			System.out.println("HI");
			for(String m:TermList)
			{
				System.out.println("term:"+m);
			}
			
			writePostingsToFile(indexDir+"//PostingsDemo.txt",TermPostingslist);
			writeTermsToFile(indexDir+"//TermsDemo.txt",TermList);
			
		}catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	/**
	 * Method to write Postings index to File if a limit in HashMap or ArrayList is Reached.
	 * @throws FileHandlingException : In case any error occurs
	 */
	public boolean writePostingsToFile(String InputFileName,ArrayList<HashMap<String, Integer>> PostingsList)
	{
		boolean status=false;
		try{
	        File InputFile=new File(InputFileName);
	        FileOutputStream fos=new FileOutputStream(InputFile);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);
	        System.out.println("Trying to write Posting to file!");
	            oos.writeObject(PostingsList);
	        System.out.println("Postings written to file!");
	            oos.flush();
	            oos.close();
	            fos.close();
	            status=true;
	        }catch(Exception e){
	        	System.out.println("Error while writing Postings to file in Index Writer:"+e.getMessage());
	        }
		
		return status;
	}
	
	/**
	 * Method to write Terms index to File if a limit in HashMap or ArrayList is Reached.
	 * @throws FileHandlingException : In case any error occurs
	 */
	public boolean writeTermsToFile(String InputFileName,ArrayList<String> TermsList)
	{
		boolean status=false;
		try{
	        File InputFile=new File(InputFileName);
	        FileOutputStream fos=new FileOutputStream(InputFile);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);
	        System.out.println("Trying to write Terms to file!");
	            oos.writeObject(TermsList);
	        System.out.println("Terms written to file!");
	            oos.flush();
	            oos.close();
	            fos.close();
	            status=true;
	        }catch(Exception e){
	        	System.out.println("Error while writing Terms to file in Index Writer:"+e.getMessage());
	        }
		return status;
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
