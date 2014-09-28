/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
	public static int DocCount=0;
	public static TreeMap<String,Integer> DocumentIDsList=new TreeMap<String,Integer>();
	public static TreeMap<String,Integer[]> CategoryList=new TreeMap<String,Integer[]>();
	public static ArrayList<String> PlaceList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> PlacePostingslist = new ArrayList<HashMap<String,Integer>>();
	public static ArrayList<String> AuthorList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> AuthorPostingslist = new ArrayList<HashMap<String,Integer>>();
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
		String[] title=d.getField(FieldNames.TITLE);
		String[] author=d.getField(FieldNames.AUTHOR);
		String[] authororg=d.getField(FieldNames.AUTHORORG);
		String[] place=d.getField(FieldNames.PLACE);
		String[] newsdate=d.getField(FieldNames.NEWSDATE);
		String[] fileid=d.getField(FieldNames.FILEID);
		String[] category=d.getField(FieldNames.CATEGORY);
		
		//--Doc ID conversion--//
		DocCount++;
		DocumentIDsList.put(fileid[0], DocCount);
		int DocID=DocumentIDsList.get(fileid[0]);
		File dir = null;
		String[] IndexCategories={"TERM","AUTHOR","PLACE","CATEGORY"};
		System.out.println(fileid[0]);
		TokenStream streamForPlace=null;
		TokenStream streamForTitle=null;
		TokenStream streamForAuthor=null;
		TokenStream streamForContent=null;
		TokenStream streamForDate=null;
		if(category!=null)
		{
		String cat=category[0];
		}
		String finalauthor=null;
		if(author!=null)
		{
			if(authororg!=null)
				finalauthor=author[0]+"and"+authororg[0];
			else finalauthor=author[0];
		}
		
		Tokenizer tokenizer=new Tokenizer();
		AnalyzerFactory af=AnalyzerFactory.getInstance();
		
		try {
			if(place!=null)
			{
				Tokenizer tokenizerforplace=new Tokenizer(",");
				streamForPlace=tokenizerforplace.consume(place[0]);
				Analyzer analyzerForPlace=af.getAnalyzerForField(FieldNames.PLACE, streamForPlace);
				while(analyzerForPlace.increment())
				{
					
				}
				streamForPlace=analyzerForPlace.getStream();
				streamForPlace.reset();
			}
			
			if(title!=null && (!title[0].trim().isEmpty()))
			{
				streamForTitle=tokenizer.consume(title[0]);
				Analyzer analyzerForTitle=af.getAnalyzerForField(FieldNames.TITLE, streamForTitle);
				while(analyzerForTitle.increment())
				{
					
				}
				streamForTitle=analyzerForTitle.getStream();
				streamForTitle.reset();
			}
			
			if(finalauthor!=null)
			{
				Tokenizer tokenizerdelimiter=new Tokenizer("and");
				streamForAuthor=tokenizerdelimiter.consume(finalauthor);
				Analyzer analyzerForAuthor=af.getAnalyzerForField(FieldNames.AUTHOR, streamForAuthor);
				while(analyzerForAuthor.increment())
				{
					
				}
				streamForAuthor=analyzerForAuthor.getStream();
				streamForAuthor.reset();
			}
			
			if(content!=null)
			{
				streamForContent=tokenizer.consume(content[0]);
				Analyzer a=af.getAnalyzerForField(FieldNames.CONTENT, streamForContent);
				while(a.increment())
				{
				
				}
				streamForContent=a.getStream();
				streamForContent.reset();
			}
			
			if(newsdate!=null)
			{
				streamForDate=tokenizer.consume(newsdate[0]);
				Analyzer a=af.getAnalyzerForField(FieldNames.NEWSDATE, streamForDate);
				while(a.increment())
				{
				
				}
				streamForDate=a.getStream();
				streamForDate.reset();
			}
		//----------------HashMap Writing Starts here----------------------------//
			if(streamForPlace!=null)
			 MakeHashmaps(streamForPlace, DocID, 1);
			 System.out.println("place hashmaps done");
			 if(streamForAuthor!=null)
			 MakeHashmaps(streamForAuthor, DocID, 2);
			 System.out.println("author hashmaps done");
			 if(streamForTitle!=null)
			 MakeHashmaps(streamForTitle, DocID, 3);
			 System.out.println("title hashmaps done");
			 if(streamForContent!=null)
			 MakeHashmaps(streamForContent, DocID, 3);
			 System.out.println("content hashmaps done");
			 if(streamForDate!=null)
			 MakeHashmaps(streamForDate, DocID, 3);
			 System.out.println("date hashmaps done");
			
			System.out.println("Success!");
		
			
			///----After Some Limit-----//
			if(DocID%1000==0)
			{
				ArrayList<String> arrayOut=new ArrayList<String>();
				for(int i=0;i<TermPostingslist.size();i++)
				{
					HashMap<String, Integer> t = new HashMap<String, Integer>();
					t=TermPostingslist.get(i);
					String str=i+":";
					for(Map.Entry<String,Integer> entry : t.entrySet())
					{
						System.out.println(TermList.get(i)+":"+i+entry.getKey() + " " + entry.getValue()+"-");
						str=str+entry.getKey()+" "+entry.getValue()+"-";
					}
					arrayOut.add(str);
				}
			
				int c=0;
				for(String m:TermList)
				{
					TermList.set(c,TermList.get(c)+" "+c);
					System.out.println("term:"+m);
					c=c+1;
				}
			
				File indexDir = new File(this.indexDir+ File.separator+ IndexCategories[0]);
				dir=new File(this.indexDir);
			
				if (!indexDir.exists())
				{
					if (indexDir.mkdir())
					{
						System.out.println("Directory is created!");
					} else
					{
						System.out.println("Failed to create directory!");
					}
				}
				writePostingsToFile(indexDir.getAbsolutePath() + File.separator +"Postings",arrayOut);
				writeTermsDictToFile(indexDir.getAbsolutePath() + File.separator +"Dictionary");
				writeDocDictToFile2(dir.getAbsolutePath() + File.separator +"DocumentDictionary");
			}
			
			
			
			
			
		}catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	public boolean MakeHashmaps(TokenStream ts,int DocID,int FieldType)
	{
		
		Map<String,Integer> tm = new TreeMap<String,Integer>();
		for(Token t:ts.getTokenstream())
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
		if(FieldType==1)		
		{
			for(Map.Entry<String,Integer> entry : tm.entrySet()) 
			{
				HashMap<String, Integer> temp = new HashMap<String, Integer>();
				if(AuthorList.contains(entry.getKey()))
				{
					int index=AuthorList.indexOf(entry.getKey());
					temp=AuthorPostingslist.get(index);
					temp.put(String.valueOf(DocID), entry.getValue());
				}
				else
				{
					AuthorList.add(entry.getKey());
					temp.put(String.valueOf(DocID), entry.getValue());
					//System.out.println(entry.getKey() + " : " + entry.getValue());
					AuthorPostingslist.add(temp);
				}
	        }
		}
		if(FieldType==2)		
		{
			for(Map.Entry<String,Integer> entry : tm.entrySet()) 
			{
				HashMap<String, Integer> temp = new HashMap<String, Integer>();
				if(PlaceList.contains(entry.getKey()))
				{
					int index=PlaceList.indexOf(entry.getKey());
					temp=PlacePostingslist.get(index);
					temp.put(String.valueOf(DocID), entry.getValue());
				}
				else
				{
					PlaceList.add(entry.getKey());
					temp.put(String.valueOf(DocID), entry.getValue());
					//System.out.println(entry.getKey() + " : " + entry.getValue());
					PlacePostingslist.add(temp);
				}
	        }
		}
		if(FieldType==3)		
		{
			for(Map.Entry<String,Integer> entry : tm.entrySet()) 
			{
				HashMap<String, Integer> temp = new HashMap<String, Integer>();
				if(TermList.contains(entry.getKey()))
				{
					int index=TermList.indexOf(entry.getKey());
					temp=TermPostingslist.get(index);
					temp.put(String.valueOf(DocID), entry.getValue());
				}
				else
				{
					TermList.add(entry.getKey());
					temp.put(String.valueOf(DocID), entry.getValue());
					//System.out.println(entry.getKey() + " : " + entry.getValue());
					TermPostingslist.add(temp);
				}
	        }
		}
		return true;
		
	}
	
	/**
	 * Method to write Postings index to File if a limit in HashMap or ArrayList is Reached.
	 * @throws FileHandlingException : In case any error occurs
	 */
	public boolean writePostingsToFile(String InputFileName,ArrayList<String> arrayOut)
	{
		boolean status=false;
		PrintWriter out=null;
		try {
            out = new PrintWriter( new FileWriter( InputFileName ) );
            for(String s:arrayOut)
            out.println(s);
        }
        catch ( IOException error ) {
        	System.out.println("Error while writing Postings to file2 in Index Writer:"+error.getMessage());
        }
		out.flush();
        out.close();
		return status;
	}
	
	/**
	 * Method to write TermsDictionary to File if a limit in HashMap or ArrayList is Reached.
	 * @throws FileHandlingException : In case any error occurs
	 */
	public boolean writeTermsDictToFile(String InputFileName)
	{
		boolean status=false;
		PrintWriter out=null;
		try {
			
            out = new PrintWriter( new FileWriter( InputFileName ) );
            for(String s:TermList)
            out.println(s);
        }
        catch ( IOException error ) {
        	System.out.println("Error while writing Terms to file2 in Index Writer:"+error.getMessage());
        }
		
        out.flush();
        out.close();
		return status;
	}
	
	/**
	 * Method to write Document Dictionary to File if a limit is Reached.
	 * @throws FileHandlingException : In case any error occurs
	 */
	public boolean writeDocDictToFile(String InputFileName)
	{
		boolean status=false;
		PrintWriter out=null;
		try {
            out = new PrintWriter( new FileWriter( InputFileName ) );
            for(Map.Entry<String,Integer> entry : DocumentIDsList.entrySet())
            out.println(entry.getKey()+" "+entry.getValue());
        }
        catch ( IOException error ) {

        	System.out.println("Error while writing Document Dictionary to file in Index Writer:"+error.getMessage());
        }
		
		out.flush();
        out.close();
		return status;
	}
	
	/**
	 * Method to write Document Dictionary to File if a limit is Reached.
	 * @throws FileHandlingException : In case any error occurs
	 */
	public boolean writeDocDictToFile2(String InputFileName)
	{
		boolean status=false;
		try{
	        File fileOne=new File(InputFileName);
	        FileOutputStream fos=new FileOutputStream(fileOne);
	            ObjectOutputStream oos=new ObjectOutputStream(fos);

	            oos.writeObject(DocumentIDsList);
	            oos.flush();
	            oos.close();
	            fos.close();
	        }catch(Exception e){}
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
