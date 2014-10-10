package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	public enum ScoringModel {TFIDF, OKAPI};
	String indexdir="";
	/**
	 * Default (and only public) consructor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		
		
		indexdir=indexDir;   //captal n smll
		
		
		//TODO: IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		
		//TODO: IMPLEMENT THIS METHOD
	  
		//String usergiven=userQuery.substring(1, userQuery.length()-1);
	//	String strippedstring="";
	//ArrayList<String> returneddocids=new ArrayList<String>();
		//String indexDir = args[1];
		//IndexReader rdr=new IndexReader(indexDir, IndexType.TERM);
	String[] splitintospaces=userQuery.split(" ");
	boolean started=false;
	IndexType indextype;
	ArrayList<String> docids=new ArrayList<String>();
	ArrayList<ArrayList<String>> chainarraylist=new ArrayList<ArrayList<String>>();
	String operator="";
	String[] priorityoperator= new String[4];
	boolean operatorset=false;
	boolean priorityoperatorset=false;
	boolean bracketstrt=false;
    boolean getresult=false;
    boolean notset=false;
    int count=0;
    int ends=0;
    int starts=0;
    ArrayList<ArrayList<String>> priorityarraylist=new ArrayList<ArrayList<String>>();
	for(String s:splitintospaces )
	{
		if(s.startsWith("{"))
		{
			started=true;
			s=s.substring(1);
		}
		if(s.endsWith("}"))
		{
			s=s.substring(0,s.length()-1);
			started=false;
		}
			if(s.contains("["))
			{
				while((s.contains("["))) ///check loop interchange
				{
				s=s.substring(s.indexOf("[")+1);
				bracketstrt=true;
				priorityoperatorset=false;
				starts++;
				}
				count++;
			}
			if(s.contains("]"))
			{
			while((s.contains("]"))) ///check loop interchange
			{
				s=s.substring(0, s.length()-1);
				bracketstrt=false;
				getresult=true;
				ends++;
				starts--;
			/*	if(operatorset)
					getdocsforoneoperator(operator, chainarraylist.get(0),chainarraylist.get(1));*/
			}
			}
			if(s.contains("AND")||s.contains("OR")||s.contains("NOT"))
			{
				if(bracketstrt || starts>0)
				{
					priorityoperator[count]=s;
					priorityoperatorset=true;
					continue;
				}
				else
				{
				operator=s;
				operatorset=true;
				continue;
				}
			}
			if(s.contains("<"))
			{
				s=s.substring(s.indexOf("<")+1,s.indexOf(">"));
				notset=true;
				if(bracketstrt||getresult)
					priorityoperator[count]="ANDNOT";
				else
				operator="ANDNOT";
			}
			String[] indexandtermsplit=s.split(":");
			if(indexandtermsplit[0].equalsIgnoreCase("Term"))
			{
				indextype=IndexType.TERM;
			}
			else if(indexandtermsplit[0].equalsIgnoreCase("Author"))
			{
				indextype=IndexType.AUTHOR;
			}
			else if(indexandtermsplit[0].equalsIgnoreCase("Place"))
			{
				indextype=IndexType.PLACE;
			}
			else
			{
				indextype=IndexType.CATEGORY;
			}
			docids=queryOR(indextype, indexandtermsplit[1]);
			if(bracketstrt|| getresult)
			{
				priorityarraylist.add(docids);
			}
			else
			{
			chainarraylist.add(docids);
			}
			if(getresult||priorityoperatorset)
			{
				while(ends>0) ///ckeck whether using duplicate for count and ends ///// check for three terms
				{
				ArrayList<String> resolveddocids=getdocsforoneoperator(priorityoperator[count], priorityarraylist.get(count-1),priorityarraylist.get(count));
			priorityarraylist.remove(count-1);
			priorityarraylist.remove(count-1);
			priorityarraylist.add(resolveddocids);
			priorityoperatorset=false;
			
			if (!(starts>0))
			count--;
			ends--;
				}
			}
			if(count==0 && starts==0)
			{
			if(getresult) ////have to check only at the end it has to be added;all closed bracs are closed only
			{
			chainarraylist.add(priorityarraylist.get(0));
			priorityarraylist.remove(0);
			getresult=false;
			}
			}
			if(operatorset && !(bracketstrt))
			{
				ArrayList<String> resolveddocids=getdocsforoneoperator(operator, chainarraylist.get(0),chainarraylist.get(1));
				chainarraylist.remove(0);
				chainarraylist.remove(0);
				chainarraylist.add(resolveddocids);
			}
			if(!started)
				break;
				
	}
	
		
		/*if(usergiven.contains("["))
		{
		String tobedonefirst=usergiven.substring(usergiven.indexOf("[")+1, usergiven.indexOf("]"));
		returneddocids=getdocsforsingleoperator(tobedonefirst);
		strippedstring=usergiven.substring(usergiven.indexOf("]")+1);
		strippedstring=strippedstring.trim();
		String[] connectors={"AND","OR","NOT"};
		if(strippedstring.startsWith(connectors[0]))
		{
			strippedstring=strippedstring.substring(3);
			strippedstring=strippedstring.trim();
			returneddocids=getdocsforsingleoperator(strippedstring);
		}
			
		}*/
		//return chainarraylist;
	}
	public ArrayList<String> getdocsforoneoperator(String operator,ArrayList<String> first,ArrayList<String> second)
	{
		HashSet<String> combine=new HashSet<String>();
		if(operator.equalsIgnoreCase("OR"))
		{
		
		combine.addAll(first);
		combine.addAll(second);
		}
		if(operator.equalsIgnoreCase("AND"))
		{
		for(String s:first)
			if(second.contains(s))
			{
				combine.add(s);
			}
	
		}
		if(operator.equalsIgnoreCase("ANDNOT"))
		{
			for(String s:second)
			{
				if(first.contains(s))
				{
					first.remove(s);
				}
			}
			combine.addAll(first);
		}
		return new ArrayList<String>(combine);
	}
	public ArrayList<String> getdocsforsingleoperator(String stringinbrackets)
	{
		String[] partition;
		String[] terms=new String[2];
		int i;
		  ArrayList<String> docids=new ArrayList<String>();
	if(stringinbrackets.contains("OR"))
	{
	partition=stringinbrackets.split("OR");
	i=0;
	for(String s:partition)
	{
		String[] splittedterms=s.split(":");
		terms[i]=splittedterms[1].trim();
		i++;
	}
	docids=	queryOR(IndexType.TERM, terms);
	}
	else if(stringinbrackets.contains("AND"))
	{
	partition=stringinbrackets.split("AND");
	i=0;
	for(String s:partition)
	{
		String[] splittedterms=s.split(":");
		terms[i]=splittedterms[1].trim();
		i++;
	}
	//docids=	queryAND(IndexType.TERM, terms);
	}
	else
	{
		/*partition=stringinbrackets.split(":");
		
		for(String s:partition)
		{*/i=0;
			String[] splittedterms=stringinbrackets.split(":");
			terms[i]=splittedterms[1].trim();
			i++;
		//}
		docids=	queryOR(IndexType.TERM, terms);
	}
	
	String mme="gcvhjkj";
	return docids;
	}
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
	@SuppressWarnings("null")
	public ArrayList<String> queryOR(IndexType type,String...terms) {
		String indexdir=this.indexdir;
		IndexReader rdr=new IndexReader(indexdir, type);
		File dictionaryFile=new File(rdr.indexType+ File.separator +"Dictionary");
		Integer[] termPositions=new Integer[terms.length];
		ArrayList<HashMap<String, Integer>> TermPostingslist=new ArrayList<HashMap<String, Integer>>();
		TreeMap<String, Integer> finalposting=new TreeMap<String, Integer>();
		HashMap<String, Integer> basemap=null;
	    ArrayList<String> finale=new ArrayList<String>();
		BufferedReader br;
		try
		{
				int l=0;
				br = new BufferedReader(new FileReader(dictionaryFile));
				for(String line; (line = br.readLine()) != null;) 
				{
					if(l==terms.length) break;
					String[] KeyIndexSplit=line.split(" ");
					if(KeyIndexSplit.length==2)
					{
						for(int i1=0;i1<terms.length;i1++)
						{
							if(terms[i1]!=null)
							{
							if(KeyIndexSplit[0].trim().equals(terms[i1]))
							{
								termPositions[i1]=Integer.parseInt(KeyIndexSplit[1].trim());
								l++;
								break;
							}	
							}
							else
							{
								l++;
							}
						}
					}
				}
				if(l==terms.length)
				{

					List<Integer> wordList = Arrays.asList(termPositions);
					if(wordList.size()>2)
					{
					Collections.sort(wordList);
					}
					File postingsFile=new File(rdr.indexType+ File.separator +"Postings");
					br.close();
					br = new BufferedReader(new FileReader(postingsFile));
					for(Integer j:wordList)
					{
						if(j!=null)
						{
						br = new BufferedReader(new FileReader(postingsFile));
						int i=0;
						while(i<j)
						{
							br.readLine();
							i=i+1;
						}
						String HashString=br.readLine();
						String[] TrmPstngSplit=HashString.split(":");
						TrmPstngSplit[0].trim();
						TrmPstngSplit[1].trim();
						TrmPstngSplit[1]=TrmPstngSplit[1].substring(0,TrmPstngSplit[1].length()-1);
						String[] PstngSplit=TrmPstngSplit[1].split("-");
						HashMap<String, Integer> Posting=new HashMap<String, Integer>();
						for(int i1=0;i1<PstngSplit.length;i1++)
						{
							PstngSplit[i1].trim();
							String[] DocFreqSplit=PstngSplit[i1].split(" ");
							if(type==IndexType.CATEGORY)
							{
								Posting.put(DocFreqSplit[0],0);
							}
							else
							{
							Posting.put(DocFreqSplit[0], Integer.parseInt(DocFreqSplit[1]));
						    }
						}
						TermPostingslist.add(Posting);
						br.close();
					}
					
					
					 basemap = TermPostingslist.get(0);
					int len = TermPostingslist.size();
					for(int i1=0;i1<len;i1++)
					{
						//basemap.keySet().retainAll(TermPostingslist.get(i1).keySet());
						//basemap.keySet().addAll(TermPostingslist.get(i1).keySet());
					/*	List<String> uniondocs=new ArrayList<String>();
						uniondocs=TermPostingslist*/
					//	union.putAll(TermPostingslist.get(i1).,Integer.parseInt("0"));
					Set<String> temp=null;
					temp=new HashSet<String>(TermPostingslist.get(i1).keySet());
					for(String s:temp)
					{
						finale.add(s);
					}
					}	
					HashSet<String> hs = new HashSet<String>();
					hs.addAll(finale);
					finale.clear();
					finale.addAll(hs);
					/*for(String s:finale)
					{
						for()
					}*/
				}
				}
				else
				{
					br.close();
					return null;
				}
				br.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Sorry,File Not Found!"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Sorry,an IO Error Occures!"+e.getMessage());
			e.printStackTrace();
		}
		
		return finale;
	}
}