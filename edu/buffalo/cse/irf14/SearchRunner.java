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
	HashMap<String, TreeMap<String, Integer>> docids=new HashMap<String, TreeMap<String, Integer>>();
	ArrayList<HashMap<String, TreeMap<String, Integer>>> chainarraylist=new ArrayList<HashMap<String, TreeMap<String, Integer>>>();
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
    ArrayList<HashMap<String, TreeMap<String, Integer>>> priorityarraylist=new ArrayList<HashMap<String, TreeMap<String, Integer>>>();
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
					HashMap<String, TreeMap<String, Integer>> resolveddocids=getdocsforoneoperator(priorityoperator[count], priorityarraylist.get(count-1),priorityarraylist.get(count));
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
				HashMap<String, TreeMap<String, Integer>> resolveddocids=getdocsforoneoperator(operator, chainarraylist.get(0),chainarraylist.get(1));
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
	public HashMap<String, TreeMap<String, Integer>> getdocsforoneoperator(String operator,HashMap<String, TreeMap<String, Integer>> first,HashMap<String, TreeMap<String, Integer>> second)
	{
		HashMap<String, TreeMap<String, Integer>> combine=new HashMap<String, TreeMap<String, Integer>>();
		if(operator.equalsIgnoreCase("OR"))
		{
		
/*		combine.addAll(first);
		combine.addAll(second);*/
			TreeMap<String, Integer> getterm=new TreeMap<String, Integer>();
			String term1="";
			String term2="";
			for(String s:first.keySet())
			{
				getterm=first.get(s);
				term1=getterm.firstKey();/// check case of multiple terms
				break;
				
			}
			for(String s:second.keySet())
			{
				getterm=second.get(s);
				term2=getterm.firstKey();
				break;
				
			}
			
			for(String s :first.keySet())
			{
				if(second.containsKey(s))
				{
					TreeMap<String, Integer> copy=new TreeMap<String, Integer>();
					TreeMap<String, Integer> copy2=new TreeMap<String, Integer>();
					TreeMap<String, Integer> combo=new TreeMap<String, Integer>();
					copy=first.get(s);
					copy2=second.get(s);
					for(String s1:copy.keySet())
					{
						combo.put(s1, copy.get(s1));
					}
					for(String s1:copy2.keySet())
					{
						combo.put(s1, copy2.get(s1));
					}
					
					combine.put(s, combo);	
				}
				else
				{
					TreeMap<String, Integer> copy=new TreeMap<String, Integer>();
					copy=first.get(s);
					copy.put(term2, 0);
					combine.put(s, copy);
				}
		}
			for(String s:second.keySet())
			{
				if(!(first.containsKey(s)))
				{
					TreeMap<String, Integer> copy=new TreeMap<String, Integer>();
					copy=second.get(s);
					copy.put(term1, 0);
					combine.put(s, copy);
				}
				
			}
				
		}
		/*if(operator.equalsIgnoreCase("AND"))
		{
		for(String s:first)
			if(second.contains(s))
			{
				combine.add(s);
			}
	
		}*/
		if(operator.equalsIgnoreCase("ANDNOT"))
		{
			for(String s:second.keySet())
			{
				if(first.keySet().contains(s))
				{
					first.remove(s);
				}
			}
		combine=first;
		}
		//return new ArrayList<String>(combine);
		if(operator.equalsIgnoreCase("AND"))
		{
			/*for(String s:first)
				if(second.contains(s))
				{
					combine.add(s);
				}
		
			}*/
		for(String s:first.keySet())
			if(second.keySet().contains(s))
			{
				TreeMap<String, Integer> copy=new TreeMap<String, Integer>();
				TreeMap<String, Integer> copy2=new TreeMap<String, Integer>();
				TreeMap<String, Integer> combo=new TreeMap<String, Integer>();
				/*copy=first.get(s);
				 * 
				copy.put(second.get(s).firstKey(),second.get(s).get(second.get(s).firstKey())); // check for adding  multiple items
			
				
				
				combine.put(s, copy);*/
				copy=first.get(s);
				copy2=second.get(s);
				for(String s1:copy.keySet())
				{
					combo.put(s1, copy.get(s1));
				}
				for(String s1:copy2.keySet())
				{
					combo.put(s1, copy2.get(s1));
				}
				
				combine.put(s, combo);
				//combine.get(s).put(copy.firstEntry());
			}
	
		}
	return combine;
	
	}
/*	public ArrayList<String> getdocsforsingleoperator(String stringinbrackets)
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
		partition=stringinbrackets.split(":");
		
		for(String s:partition)
		{i=0;
			String[] splittedterms=stringinbrackets.split(":");
			terms[i]=splittedterms[1].trim();
			i++;
		//}
		docids=	queryOR(IndexType.TERM, terms);
	}
	
	String mme="gcvhjkj";
	return docids;
	}*/
	
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
	public HashMap<String,TreeMap<String, Integer>> queryOR(IndexType type,String term) {
		String indexdir=this.indexdir;
		IndexReader rdr=new IndexReader(indexdir, type);
		File dictionaryFile=new File(rdr.indexType+ File.separator +"Dictionary");
		//Integer[] termPositions=new Integer[terms.length];
		int termPosition=0;
		ArrayList<HashMap<String, Integer>> TermPostingslist=new ArrayList<HashMap<String, Integer>>();
		TreeMap<String, Integer> finalposting=new TreeMap<String, Integer>();
		HashMap<String, Integer> basemap=null;
	    ArrayList<String> finale=new ArrayList<String>();
		BufferedReader br;
		HashMap<String,TreeMap<String, Integer>> docidtermfreq= new HashMap<String,TreeMap<String, Integer>>();
		try
		{
				int l=0;
				br = new BufferedReader(new FileReader(dictionaryFile));
				for(String line; (line = br.readLine()) != null;) 
				{
					//if(l==1) break;
					String[] KeyIndexSplit=line.split(" ");
					if(KeyIndexSplit.length==2)
					{
						/*for(int i1=0;i1<terms.length;i1++)
						{
							if(terms[i1]!=null)
							{*/
							if(KeyIndexSplit[0].trim().equals(term))
							{
								termPosition=Integer.parseInt(KeyIndexSplit[1].trim());
								l++;
								break;
							}	
							}
							/*else
							{
								l++;
							}
						}
					}*/
				}
				/*if(l==terms.length)
				{*/

					/*List<Integer> wordList = Arrays.asList(termPositions);
					if(wordList.size()>2)
					{
					Collections.sort(wordList);
					}*/
					File postingsFile=new File(rdr.indexType+ File.separator +"Postings");
					br.close();
					br = new BufferedReader(new FileReader(postingsFile));
					/*for(Integer j:wordList)
					{
						if(j!=null)
						{*/
						//br = new BufferedReader(new FileReader(postingsFile));
						int i=0;
						while(i<termPosition)
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
						
						for(int i1=0;i1<PstngSplit.length;i1++)
						{
							TreeMap<String, Integer> Posting=new TreeMap<String, Integer>();
							PstngSplit[i1].trim();
							String[] DocFreqSplit=PstngSplit[i1].split(" ");
							if(type==IndexType.CATEGORY)
							{
								Posting.put(DocFreqSplit[0],0);
							}
							else
							{
							Posting.put(term, Integer.parseInt(DocFreqSplit[1]));
						    }
							docidtermfreq.put(DocFreqSplit[0], Posting);
						}
						
						
				//	}
					
					
				/*	 basemap = TermPostingslist.get(0);
					int len = TermPostingslist.size();
					for(int i1=0;i1<len;i1++)
					{
						//basemap.keySet().retainAll(TermPostingslist.get(i1).keySet());
						//basemap.keySet().addAll(TermPostingslist.get(i1).keySet());
						List<String> uniondocs=new ArrayList<String>();
						uniondocs=TermPostingslist
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
					for(String s:finale)
					{
						for()
					}
				}
				}
				else
				{
					br.close();
					return null;
				}*/
				br.close();
						
		} 
		catch (FileNotFoundException e) {
			System.out.println("Sorry,File Not Found!"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Sorry,an IO Error Occures!"+e.getMessage());
			e.printStackTrace();
		}
		
		return docidtermfreq;
	}
}