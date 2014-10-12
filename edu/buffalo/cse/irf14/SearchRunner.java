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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.print.Doc;

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
	public SearchRunner(String indexDir, String corpusDir,char mode, PrintStream stream)
	{
		indexdir=indexDir;   
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model)
	{
		
	//Write code to get ParsedQuery	from QueryParser and then convert into string form using Query
	String parsedQuery=userQuery;
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
    @SuppressWarnings("unused")
	boolean notset=false;
    int count=0;
    int ends=0;
    int starts=0;
    TreeMap<String,Integer> termDocCounts= new TreeMap<String, Integer>();
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
				if(starts==1)
				bracketstrt=false;
				getresult=true;
				ends++;
				starts--;
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
			
			docids=getDOCIDs(indextype, indexandtermsplit[1]);
			termDocCounts.put(indexandtermsplit[1], docids.size());
			
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
	HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap=new HashMap<String, TreeMap<String, Integer>>();
	FinalDocResultHashmap=chainarraylist.get(0);
	
	TreeMap<String, Integer> TermTFCountPairMap=new TreeMap<String, Integer>();
	TermTFCountPairMap=RetrieveTermsInQuery(parsedQuery);
	ArrayList<String> TermsList=new ArrayList<String>();
	for(String str:TermTFCountPairMap.keySet())
	{
		TermsList.add(str);
	}
	
	
	TreeMap<String, Double> IDFMap=new TreeMap<String, Double>();
	IDFMap=calculateIDFScores(termDocCounts);

	TreeMap<String,Double> WtqMap=new TreeMap<String, Double>();
	WtqMap=calculateQueryTFIDFScore(TermTFCountPairMap,IDFMap);
	
	HashMap<String, TreeMap<String, Double>> WtdHashmap=new HashMap<String, TreeMap<String,Double>>();
	WtdHashmap=calculateDocTFIDFScores(FinalDocResultHashmap, IDFMap);
	
	@SuppressWarnings("unused")
	HashMap<String,Double> RelevanceScores=new HashMap<String, Double>();
	RelevanceScores=CalculateFinalDocTFIDFRelevanceScores(TermsList,WtdHashmap,WtqMap);
	
	
	
	}
	
	
	public HashMap<String,Double> CalculateFinalDocTFIDFRelevanceScores(ArrayList<String> TermsList,
			HashMap<String, TreeMap<String, Double>> WtdHashmap,TreeMap<String,Double> WtqMap)
	{
		HashMap<String,Double> RelevanceScores=new HashMap<String, Double>();
		String[] DocIDsList=WtdHashmap.keySet().toArray(new String[WtdHashmap.size()]);
		double[] Scores=new double[DocIDsList.length];
		double QueryLength=0.0;
		for(Double d:WtqMap.values())
		{
			QueryLength=QueryLength+(d*d);
		}
		QueryLength=Math.sqrt(QueryLength);
		for(Entry<String,TreeMap<String, Double>> EntryDoc:WtdHashmap.entrySet())
		{
			int d=-1;
			for(int i=0; i<DocIDsList.length; i++) 
			{
				if(DocIDsList[i] == EntryDoc.getKey())
				{
					d=i;
					break;
				}
			}
			TreeMap<String, Double> temp=new TreeMap<String, Double>();
			double DocumentLength=0.0;
			for(String S:TermsList)
			{
				S.trim();
				if(WtqMap.containsKey(S) && temp.containsKey(S))
				{
					Double Wtq=WtqMap.get(S); //STEP-1
					Double Wtd=temp.get(S);
					Scores[d]=Scores[d]+(Wtq*Wtd);//STEP-2
					DocumentLength=DocumentLength+(Wtd*Wtd);
				}
			}
			DocumentLength=Math.sqrt(DocumentLength);
			Scores[d]=(Scores[d]/(QueryLength*DocumentLength));
			RelevanceScores.put(EntryDoc.getKey(), Scores[d]);
		}
		return RelevanceScores;
	}
	
	public TreeMap<String,Integer> RetrieveTermsInQuery(String parsedQuery)
	{
		ArrayList<String> TermsList=new ArrayList<String>();
		/*
		 * To Remove Special Chars and other Category names and Form list with Raw Terms
		 */
		String[] splitSpace=parsedQuery.split(" ");
		for(String S:splitSpace)
		{
			if(S.contains(":"))
			{
				String[] splitColon=S.split(":");
				boolean setBraces=true;
				String str=splitColon[1];
				
				while(setBraces)
				{
					if(str.endsWith("]")||str.endsWith("}")||str.endsWith(">")||str.endsWith("\""))
					{
						str=str.substring(0, str.length()-1); //This will remove ] or } that are at the end 
					}
					else if(str.startsWith("\""))
					{
						str=str.substring(1, str.length());
					}
					else setBraces=false;
				}
				str=str.trim();
				TermsList.add(str);
			}
		}
		/*
		 * To remove Duplicates and Get Count of Each Term in Query
		 */
		TreeMap<String,Integer> TermTFCountPairMap=new TreeMap<String, Integer>();
		for(String S:TermsList)
		{
			int Count = Collections.frequency(TermsList, S);
			int FinalCount=Count;
			if(Count>1)
			{
				int first=TermsList.indexOf(S);
				for(int i=first+1;i<TermsList.size();i++)
				{
					if(TermsList.get(i).equals(S))
					{
						Count=Count-1;
						TermsList.remove(i);
					}
					if(Count==1) break;
				}
			 }
			TermTFCountPairMap.put(S, FinalCount);
		 }
		
		return TermTFCountPairMap;
	}
	

	public double TFVariants(int Type,TreeMap<String, Integer> TermndFreqMap,int CurrentTermFreq)
	{
		double TF=0.0;
		switch (Type) {
		case 1://Natural
		{
			TF=CurrentTermFreq;
			break;
		}
		case 2://Logarithmic
		{
			double y=(1+CurrentTermFreq);
			TF=Math.log10(y);
			break;
		}
		case 3://Augmented
		{
			Integer[] values=TermndFreqMap.values().toArray(new Integer[TermndFreqMap.size()]);
			Arrays.sort(values);
			int tfmax=values[values.length-1];
			double y=((0.5*CurrentTermFreq)/tfmax);
			TF=(0.5+y);
			break;
		}
		case 4://boolean
		{
			if(CurrentTermFreq>0) TF=1;
			else TF=0;
			break;
		}
		case 5://Log Ave
		{
			int tfsum=0;
			for(int i:TermndFreqMap.values())
				tfsum=tfsum+i;
			double x=1+(tfsum/TermndFreqMap.size());
			x=Math.log10(x);
			double y=(1+CurrentTermFreq);
			y=Math.log10(y);
			TF=(y/x);
			break;
		}
		default:
			break;
		}
		return TF;
	}
	
	public TreeMap<String,Double> calculateQueryTFIDFScore(TreeMap<String, Integer> TermTFCountPairMap,TreeMap<String, Double> IDFMap)
	{
		TreeMap<String,Double> WtqMap=new TreeMap<String, Double>();
		for(Entry<String, Integer> entryTermtfs:TermTFCountPairMap.entrySet())
		{
			String tempTerm=entryTermtfs.getKey();
			if(IDFMap.containsKey(tempTerm))
			{
				double idft=IDFMap.get(tempTerm);
				int temptf=entryTermtfs.getValue();
				double y=(1+temptf);
				double tft=Math.log10(y);
				double Wtq=(idft*tft);
				WtqMap.put(tempTerm, Wtq);
			}
		}
		return WtqMap;
	}
	
	public HashMap<String, TreeMap<String, Double>> calculateDocTFIDFScores(HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap,TreeMap<String, Double> IDFMap)
	{
		HashMap<String, TreeMap<String, Double>> WtdHashmap=new HashMap<String, TreeMap<String,Double>>();
		for(Entry<String, TreeMap<String, Integer>> entry:FinalDocResultHashmap.entrySet())
		{
			String DocID=entry.getKey().trim();
			TreeMap<String, Integer> temp=new TreeMap<String, Integer>();
			temp=entry.getValue();
			TreeMap<String,Double> newTemp=new TreeMap<String, Double>();
			for(Entry<String, Integer> entryTermtfs:temp.entrySet())
			{
				String tempTerm=entryTermtfs.getKey();
				if(IDFMap.containsKey(tempTerm))
				{
					double idft=IDFMap.get(tempTerm);
					int temptf=entryTermtfs.getValue();
					double y=(1+temptf);
					double tft=Math.log10(y);
					double Wtd=(idft*tft);
					newTemp.put(tempTerm, Wtd);
				}
			}
			WtdHashmap.put(DocID, newTemp);
		}
		
		return WtdHashmap;
	}
	
	
	@SuppressWarnings("unchecked")
	public TreeMap<String, Double> calculateIDFScores(TreeMap<String,Integer> termDocCounts)
	{
		TreeMap<String, Double> IDFMap=new TreeMap<String, Double>();
		TreeMap<String,Integer> mapInFile=new TreeMap<String, Integer>();
		FileInputStream fis=null;
		ObjectInputStream ois=null;
		int N=0;
		File documentdictionaryFile=new File(this.indexdir+ File.separator +"DocumentDictionary");
		try
		{
            fis=new FileInputStream(documentdictionaryFile);
            ois=new ObjectInputStream(fis);
			mapInFile=(TreeMap<String,Integer>)ois.readObject();
			N=mapInFile.size();
			ois.close();
		    fis.close();
		}
		catch(Exception e){	}	
		for(Entry<String, Integer> entry:termDocCounts.entrySet())
		{
			String Term=entry.getKey().trim();
			int dft=entry.getValue();
			double idft=(N/dft);
			idft=Math.log10(idft);
			IDFMap.put(Term, idft);
		}
		return IDFMap;
	}
	
	
	public HashMap<String, TreeMap<String, Integer>> getdocsforoneoperator(String operator,HashMap<String, TreeMap<String, Integer>> first,HashMap<String, TreeMap<String, Integer>> second)
	{
		HashMap<String, TreeMap<String, Integer>> combine=new HashMap<String, TreeMap<String, Integer>>();
		if(operator.equalsIgnoreCase("OR"))
		{
			TreeMap<String, Integer> getterm=new TreeMap<String, Integer>();
	          Set<String> term1=null;
			Set<String> term2=null;
			for(String s:first.keySet())
			{
				getterm=first.get(s);
				term1=getterm.keySet();/// check case of multiple terms
				break;
				
			}
			String[] term1array=term1.toArray(new String[term1.size()]);
			for(String s:second.keySet())
			{
				getterm=second.get(s);
				term2=getterm.keySet();
				break;
				
			}
			String[] term2array=term2.toArray(new String[term2.size()]);
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
					for(String s1:term2array)
					copy.put(s1, 0);
					combine.put(s, copy);
				}
		}
			for(String s:second.keySet())
			{
				if(!(first.containsKey(s)))
				{
					TreeMap<String, Integer> copy=new TreeMap<String, Integer>();
					copy=second.get(s);
					for(String s1:term1array)
					copy.put(s1, 0);
					combine.put(s, copy);
				}
				
			}
				
		}
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
		if(operator.equalsIgnoreCase("AND"))
		{
		for(String s:first.keySet())
			if(second.keySet().contains(s))
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
				//combine.get(s).put(copy.firstEntry());
			}
	
		}
	return combine;
	
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
	public HashMap<String,TreeMap<String, Integer>> getDOCIDs(IndexType type,String term) {
		String indexdir=this.indexdir;
		IndexReader rdr=new IndexReader(indexdir, type);
		File dictionaryFile=new File(rdr.indexType+ File.separator +"Dictionary");
		int termPosition=0;
		BufferedReader br;
		HashMap<String,TreeMap<String, Integer>> docidtermfreq= new HashMap<String,TreeMap<String, Integer>>();
		try
		{
				int l=0;
				br = new BufferedReader(new FileReader(dictionaryFile));
				for(String line; (line = br.readLine()) != null;) 
				{
					String[] KeyIndexSplit=line.split(" ");
					if(KeyIndexSplit.length==2)
					{
							if(KeyIndexSplit[0].trim().equals(term))
							{
								termPosition=Integer.parseInt(KeyIndexSplit[1].trim());
								l=l+1;
								break;
							}	
							}
				}
				File postingsFile=new File(rdr.indexType+ File.separator +"Postings");
					br.close();
					br = new BufferedReader(new FileReader(postingsFile));
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