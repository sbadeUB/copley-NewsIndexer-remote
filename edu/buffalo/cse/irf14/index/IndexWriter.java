/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
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
	public static ArrayList<String> CategoryList=new ArrayList<String>();
	public static TreeMap<Integer,String> DocumentIDsList=new TreeMap<Integer,String>();
	public static ArrayList<TreeMap<String, Integer>> CategoryPostingslist=new ArrayList<TreeMap<String,Integer>>();
	public static ArrayList<String> PlaceList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> PlacePostingslist = new ArrayList<HashMap<String,Integer>>();
	public static ArrayList<String> AuthorList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> AuthorPostingslist = new ArrayList<HashMap<String,Integer>>();
	public static ArrayList<String> TermList=new ArrayList<String>();
	public static ArrayList<HashMap<String, Integer>> TermPostingslist = new ArrayList<HashMap<String,Integer>>();
	public static ArrayList<Integer> DocumentLengthList=new ArrayList<Integer>();
	
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
		System.out.println(fileid[0]);
		int docLen=0;
		//--Doc ID conversion--//
		DocCount++;
		DocumentIDsList.put(DocCount,fileid[0]);
		int DocID=DocCount;
		TokenStream streamForPlace=null;
		TokenStream streamForTitle=null;
		TokenStream streamForAuthor=null;
		TokenStream streamForContent=null;
		TokenStream streamForDate=null;
		String cat=null;
		if(category!=null)
		{
			cat=category[0];
			//DocumentIDsList.put(fileid[0], DocCount);
			int catset=0;
			if(CategoryList.size()!=0)
			{
				for(int i=0;i<CategoryList.size();i++)
				{
					TreeMap<String, Integer> temp = new TreeMap<String, Integer>();
					String k=CategoryList.get(i).toString().trim();
					if(cat.equals(k))
					{
						temp=CategoryPostingslist.get(i);
						temp.put(String.valueOf(DocID), 1);
						catset=1;
						break;
					}
					
				}
				if(catset==0)
				{
					CategoryList.add(cat);
					TreeMap<String, Integer> temp = new TreeMap<String, Integer>();
					temp.put(String.valueOf(DocID), 1);
					CategoryPostingslist.add(temp);
				}
			}
			else
			{
				CategoryList.add(cat);
				TreeMap<String, Integer> temp = new TreeMap<String, Integer>();
				temp.put(String.valueOf(DocID), 1);
				CategoryPostingslist.add(temp);
			}
			
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
				while(streamForPlace.hasNext())
				{
					streamForPlace.next();
					docLen=docLen+1;
				}
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
				while(streamForTitle.hasNext())
				{
					streamForTitle.next();
					docLen=docLen+1;
				}
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
				while(streamForAuthor.hasNext())
				{
					streamForAuthor.next();
					docLen=docLen+1;
				}
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
				while(streamForContent.hasNext())
				{
					streamForContent.next();
					docLen=docLen+1;
				}
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
				while(streamForDate.hasNext())
				{
					streamForDate.next();
					docLen=docLen+1;
				}
				streamForDate.reset();
			}
		//----------------HashMap Writing Starts here----------------------------//
			if(streamForPlace!=null)
			 MakeHashmaps(streamForPlace, DocID, 2);
			 if(streamForAuthor!=null)
			 MakeHashmaps(streamForAuthor, DocID, 1);
			 if(streamForTitle!=null)
			 MakeHashmaps(streamForTitle, DocID, 3);
			 if(streamForContent!=null)
			 MakeHashmaps(streamForContent, DocID, 3);
			 if(streamForDate!=null)
			 MakeHashmaps(streamForDate, DocID, 3);
		
			// if(docLen!=0)
				DocumentLengthList.add(docLen);
			
		}catch (TokenizerException e) {
			e.printStackTrace();
			System.out.println("tokenizer exception thrown"+e.getMessage());
		DocumentLengthList.add(0);
			}
	}
	
	@SuppressWarnings("unused")
	public boolean writeToDisk(int DocID,String IndexType)
	{
		ArrayList<String> arrayOut=new ArrayList<String>();
		
			//int n=DocID/mod;
			if(IndexType=="TERM")
			{
				for(int i=0;i<TermPostingslist.size();i++)
				{
				HashMap<String, Integer> t = new HashMap<String, Integer>();
				t=TermPostingslist.get(i);
				String str=i+":";
				for(Map.Entry<String,Integer> entry : t.entrySet())
				{
					str=str+entry.getKey()+" "+entry.getValue()+"-";
				}
				arrayOut.add(str);
				}
		
				int c=0;
				for(String m:TermList)
				{
				TermList.set(c,TermList.get(c)+" "+c);
				c=c+1;
				}
			}
			else if(IndexType=="AUTHOR")
			{
				for(int i=0;i<AuthorPostingslist.size();i++)
				{
				HashMap<String, Integer> t = new HashMap<String, Integer>();
				t=AuthorPostingslist.get(i);
				String str=i+":";
				for(Map.Entry<String,Integer> entry : t.entrySet())
				{
					str=str+entry.getKey()+" "+entry.getValue()+"-";
				}
				arrayOut.add(str);
				}
		
				int c=0;
				for(String m:AuthorList)
				{
				AuthorList.set(c,AuthorList.get(c)+" "+c);
				c=c+1;
				}
			}
			
			else if(IndexType=="PLACE")
			{
				for(int i=0;i<PlacePostingslist.size();i++)
				{
				HashMap<String, Integer> t = new HashMap<String, Integer>();
				t=PlacePostingslist.get(i);
				String str=i+":";
				for(Map.Entry<String,Integer> entry : t.entrySet())
				{
					str=str+entry.getKey()+" "+entry.getValue()+"-";
				}
				arrayOut.add(str);
				}
		
				int c=0;
				for(String m:PlaceList)
				{
				PlaceList.set(c,PlaceList.get(c)+" "+c);
				c=c+1;
				}
			}
	
		File indexDir = new File(this.indexDir+ File.separator+ IndexType);
	
		if (!indexDir.exists())
		{
			if (indexDir.mkdir())
			{

			} else
			{
				
			}
		}
		writePostingsToFile(indexDir.getAbsolutePath() + File.separator +"Postings",arrayOut);
		 writeTermsDictToFile(indexDir.getAbsolutePath() + File.separator +"Dictionary",IndexType);
		 if(IndexType=="TERM") 
		 {
			System.out.println("done");
			TermList.clear();
			TermPostingslist.clear();
		 }
		 else if(IndexType=="AUTHOR") 
		 {
		 AuthorPostingslist.clear();
		 AuthorList.clear();
		 }
		 else if(IndexType=="PLACE") 
		 {
		 PlacePostingslist.clear();
		 PlaceList.clear();
		 }
		
		
		return true;
	
	}
	
	public int containsWithIgnoreCase(ArrayList<String> Arraylist,String str){
	    for(int i=0;i<Arraylist.size();i++)
	    {
	        if(str.equalsIgnoreCase(Arraylist.get(i)))
	            return i;
	    }
	    return -1;
	}
	
	public boolean MakeHashmaps(TokenStream ts,int DocID,int FieldType)
	{
		
		Map<String,Integer> tm = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
		for(Token t:ts.getTokenstream())
		{
			String nextElement=t.getTermText();
			nextElement.trim();
			if(nextElement.contains(" "))
			{
				String[] splitSpace=nextElement.split(" ");
				for(String s:splitSpace)
				{
					s.trim();
					if(tm.size()>0 && tm.containsKey(s))
					{
						int val = 0;
						if(tm.get(s)!= null)
						{
							val = (Integer) tm.get(s);
							val = val+1;
						}
						tm.put(s, val);
					}
					else
					{
						tm.put(s, 1);
					}
				}
			}
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
				int Pos=containsWithIgnoreCase(AuthorList, entry.getKey());
				if(Pos != -1)
				{
					temp=AuthorPostingslist.get(Pos);
					temp.put(String.valueOf(DocID), entry.getValue());
				}
				else
				{
					AuthorList.add(entry.getKey());
					temp.put(String.valueOf(DocID), entry.getValue());
					AuthorPostingslist.add(temp);
				}
	        }
		}
		else if(FieldType==2)		
		{
			for(Map.Entry<String,Integer> entry : tm.entrySet()) 
			{
				HashMap<String, Integer> temp = new HashMap<String, Integer>();
				int Pos=containsWithIgnoreCase(PlaceList, entry.getKey());
				if(Pos != -1)
				{
					temp=PlacePostingslist.get(Pos);
					temp.put(String.valueOf(DocID), entry.getValue());
				}
				else
				{
					PlaceList.add(entry.getKey());
					temp.put(String.valueOf(DocID), entry.getValue());
					PlacePostingslist.add(temp);
				}
	        }
		}
		else if(FieldType==3)		
		{
			for(Map.Entry<String,Integer> entry : tm.entrySet()) 
			{
				HashMap<String, Integer> temp = new HashMap<String, Integer>();
				int Pos=containsWithIgnoreCase(TermList, entry.getKey());
				if(Pos != -1)
				{
					temp=TermPostingslist.get(Pos);
					temp.put(String.valueOf(DocID), entry.getValue());
				}
				else
				{
					TermList.add(entry.getKey());
					temp.put(String.valueOf(DocID), entry.getValue());
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
	public boolean writeTermsDictToFile(String InputFileName,String IndexType)
	{
		boolean status=false;
		PrintWriter out=null;
		try {
			
            out = new PrintWriter( new FileWriter( InputFileName ) );
            if(IndexType=="TERM")
            {
            for(String s:TermList)
            out.println(s);
            }
            else if(IndexType=="AUTHOR")
            {
            for(String s:AuthorList)
            out.println(s);
            }
            else if(IndexType=="PLACE")
            {
            for(String s:PlaceList)
            out.println(s);
            }
            else if(IndexType=="CATEGORY")
            {
            for(String s:CategoryList)
            out.println(s);
            }
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
		int i=0;
		try {
            out = new PrintWriter( new FileWriter( InputFileName ) );
            for(Map.Entry<Integer,String> entry : DocumentIDsList.entrySet())
            {
            	Integer n=DocumentLengthList.get(i);
            	out.println(entry.getKey()+" "+entry.getValue()+" "+n.intValue());
            	i=i+1;
            }
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
	@SuppressWarnings("unused")
	public void close() throws IndexerException {
		

		writeToDisk(DocCount,"TERM");
		writeToDisk(DocCount,"AUTHOR");
		writeToDisk(DocCount,"PLACE");
		
		ArrayList<String> arrayOut=new ArrayList<String>();
		for(int i=0;i<CategoryPostingslist.size();i++)
		{
		TreeMap<String, Integer> t = new TreeMap<String, Integer>();
		t=CategoryPostingslist.get(i);
		String str=i+":";
		for(Map.Entry<String,Integer> entry : t.entrySet())
		{
			str=str+entry.getKey()+" "+"-";
		}
		arrayOut.add(str);
		}
		
		int c=0;
		for(String m:CategoryList)
		{
		CategoryList.set(c,CategoryList.get(c)+" "+c);
		c=c+1;
		}
		
		File indexDir = new File(this.indexDir+ File.separator+ "CATEGORY");
		
		if (!indexDir.exists())
		{
			if (indexDir.mkdir())
			{
				
			} else
			{
			}
		}
		writePostingsToFile(indexDir.getAbsolutePath() + File.separator +"Postings",arrayOut);
		writeTermsDictToFile(indexDir.getAbsolutePath() + File.separator +"Dictionary","CATEGORY");
		CategoryList.clear();
		CategoryPostingslist.clear();
		
		File dir=new File(this.indexDir);
		writeDocDictToFile2(dir.getAbsolutePath() + File.separator +"DocumentDictionary");
		writeDocDictToFile(dir.getAbsolutePath() + File.separator +"DocumentDictionary2");
	}
}
