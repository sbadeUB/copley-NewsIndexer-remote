package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.ParserException;

import javax.print.Doc;

import sun.security.util.Length;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.NoFixedFacet;
import com.sun.xml.internal.ws.wsdl.parser.ParserUtil;

import edu.buffalo.cse.irf14.document.Parser;
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
	public String indexdir;
	public String corpusDir;
	public char mode;
		public PrintStream stream;
	/**
	 * Default (and only public) consructor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir,char mode, PrintStream stream)
	{
		this.indexdir=indexDir;
				this.corpusDir=corpusDir;
				this.mode=mode;
				this.stream=stream;
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	 TreeMap<String,Integer> termDocCounts= new TreeMap<String, Integer>();
	public void query(String userQuery, ScoringModel model) throws ParserException
	{
		long querystarts = System.currentTimeMillis();
		System.out.println("In search Runner!");
			Query query=QueryParser.parse(userQuery, "OR");
			String parsedQuery=query.toString();
			parsedQuery=query.getStringWithOutSpaces();
			HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap=  new HashMap<String, TreeMap<String,Integer>>();
			FinalDocResultHashmap=gethashmap(parsedQuery);
	TreeMap<String, Integer> TermTFCountPairMap=new TreeMap<String, Integer>();
	TermTFCountPairMap=RetrieveTermsInQuery(parsedQuery);
	ArrayList<String> TermsList=new ArrayList<String>();
	for(String str:TermTFCountPairMap.keySet())
	{
		TermsList.add(str);
	}
	TreeMap<String, Double> IDFMap=new TreeMap<String, Double>();
	IDFMap=calculateIDFScores(termDocCounts);
//	TreeMap<String,Double> WtqMap=new TreeMap<String, Double>();
//	WtqMap=calculateQueryTFIDFScore(TermTFCountPairMap,IDFMap);
//	
//	HashMap<String, TreeMap<String, Double>> WtdHashmap=new HashMap<String, TreeMap<String,Double>>();
//	WtdHashmap=calculateDocTFIDFScores(FinalDocResultHashmap, IDFMap);
//	
//	@SuppressWarnings("unused")
//	HashMap<String,Double> RelevanceScores=new HashMap<String, Double>();
//	RelevanceScores=CalculateFinalDocTFIDFRelevanceScores(TermsList,WtdHashmap,WtqMap);
//	HashMap<String,Double> sortedmap=getsortedmapofOKAPIvalues(RelevanceScores);
	
	/*for(String s:RelevanceScores.keySet())
		System.out.println(s+" "+RelevanceScores.get(s));*/
	HashMap<String,Double> RelevanceScoresOKAPI=new HashMap<String, Double>();
	RelevanceScoresOKAPI=calculateOKAPI(IDFMap,FinalDocResultHashmap);
	List<Entry<String,Double>> sortedmapwithfileids=getsortedmapwithcomparator(RelevanceScoresOKAPI);
	for(Entry<String,Double> s:sortedmapwithfileids)
	{
		System.out.println(s);
	}
	/*HashMap<String,Double> sortedmap=getsortedmapofOKAPIvalues(RelevanceScoresOKAPI);
	for(String s:sortedmap.keySet())
		System.out.println(s+" "+sortedmap.get(s));*/
	/*File corpDirectory = new File(corpusDir);
	Document d=null;
	String[] files=corpDirectory.list();
	long queryends = System.currentTimeMillis();
	System.out.println((queryends-querystarts)/1000+" secs");
	for(String s:sortedmap.keySet())
	{
		d = Parser.parse(corpDirectory.getAbsolutePath() + File.separator +s);
		System.out.println(d.getField(FieldNames.TITLE)[0]);
		String[] con=d.getField(FieldNames.CONTENT);
		String str=con[0];
		String[] contentarray=str.split("\r");
		System.out.println(contentarray[0]);
		if(!contentarray[1].isEmpty())
		System.out.println(contentarray[1]);
		if(!contentarray[2].isEmpty())
		System.out.println(contentarray[2]);
	}*/
	
	}
	public List<Entry<String,Double>> getsortedmapwithcomparator(HashMap<String,Double> RelevanceScoresOKAPI)
	{
		HashMap<String,Double> mapwithfileids= new HashMap<String, Double>();
		File documentdictionaryFile=new File(indexdir+ File.separator +"DocumentDictionary");
        FileInputStream fis;
		try {
			fis = new FileInputStream(documentdictionaryFile);
        ObjectInputStream ois=new ObjectInputStream(fis);
		@SuppressWarnings("unchecked")
		TreeMap<Integer,String> mapInFile=(TreeMap<Integer,String>)ois.readObject();
        for(String s:RelevanceScoresOKAPI.keySet())
        {
        	if(mapInFile.containsKey(Integer.parseInt(s)))
        	{
        		mapwithfileids.put(mapInFile.get(Integer.parseInt(s)),RelevanceScoresOKAPI.get(s));
        	}
        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Entry<String,Double>> sortedEntries = new ArrayList<Entry<String,Double>>(mapwithfileids.entrySet());
		Collections.sort(sortedEntries,new Comparator<Entry<String,Double>>() {
            public int compare(Entry<String,Double> e1, Entry<String,Double> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        }
		);
		return sortedEntries;
	}

	@SuppressWarnings("resource")
	public HashMap<String,Double> getsortedmapofOKAPIvalues(HashMap<String,Double> RelevanceScoresOKAPI)
	{
		List<String> keys= new ArrayList<String>(RelevanceScoresOKAPI.keySet());
		List<Double> values= new ArrayList<Double>(RelevanceScoresOKAPI.values());
		Collections.sort(keys);
		Collections.sort(values);
		HashMap<String, Double> sortedmap=new HashMap<String, Double>();
		if(values.size()>10)
		{
		for(int i=values.size();i>0;i--)//change to i>values.size()-10,now will return all
		{
			double k=values.get(i-1);
			for(String s:RelevanceScoresOKAPI.keySet())
			{
			if(RelevanceScoresOKAPI.get(s)==k)
			{
				/*while(!sortedmap.keySet().contains(s))
				{*/
				sortedmap.put(s, k);
			//	i--;
				RelevanceScoresOKAPI.remove(s);
				//String xyz="p";
				//String q=xyz;
				//}
				break;
			}
			}
		}
		
           
		}
		else
		{
			for(int i=values.size();i>0;i--)
			{
				double k=values.get(i-1);
				for(String s:RelevanceScoresOKAPI.keySet())
				{
				if(RelevanceScoresOKAPI.get(s)==k)
				{
					/*while(!sortedmap.keySet().contains(s))
					{*/
					sortedmap.put(s, k);
				//	i--;
					RelevanceScoresOKAPI.remove(s);
					//String xyz="p";
					//String q=xyz;
					//}
					break;
				}
				}
			}
		}
		HashMap<String,Double> sortedmapwithfileids= new HashMap<String, Double>();
		File documentdictionaryFile=new File(indexdir+ File.separator +"DocumentDictionary");
		
        
        FileInputStream fis;
		try {
			fis = new FileInputStream(documentdictionaryFile);
		
        ObjectInputStream ois=new ObjectInputStream(fis);

		@SuppressWarnings("unchecked")
		TreeMap<Integer,String> mapInFile=(TreeMap<Integer,String>)ois.readObject();
        for(String s:sortedmap.keySet())
        {
        	if(mapInFile.containsKey(Integer.parseInt(s)))
        	{
        		sortedmapwithfileids.put(mapInFile.get(Integer.parseInt(s)),sortedmap.get(s));
        	}
        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sortedmapwithfileids;
	}
	public  HashMap<String, TreeMap<String,Integer>>gethashmap(String parsedQuery)
	{
		String[] splitintospaces=parsedQuery.split(" ");
		boolean quoteset=false;
		//String[] splitintospaces=userQuery.split(" ");
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
	    ArrayList<HashMap<String, TreeMap<String, Integer>>> priorityarraylist=new ArrayList<HashMap<String, TreeMap<String, Integer>>>();
		for(String s:splitintospaces )
		{
			String sm=s;
			if(quoteset)
			{
				quoteset=false;
				continue;
			}
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
					getresult=false;
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
				if(s.contains("\""))
				{
					String st=s.replace("\"","");
					int index=0;
				int len=splitintospaces.length;
				for(int i=0;i<len;i++)
				{
					if(splitintospaces[i].equals(sm))
					{
						index=i;
						break;
					}
						
				}
				String quote=splitintospaces[index+1];
				if(quote.endsWith("}"))
				{
					started=false;
					quote=quote.substring(0, quote.length()-1);
					//s=st+" "+quote;
				}
				if(quote.contains("]"))
				{
					while((quote.contains("]"))) ///check loop interchange
					{
						quote=quote.substring(0, quote.length()-1);
						if(starts==1)
						bracketstrt=false;
						getresult=true;
						ends++;
						starts--;
					}
				}
				if(!(quote.endsWith("}")||quote.endsWith("]")))
				{
				quote=quote.substring(0, quote.length()-1);
				s=st+" "+quote;
				quoteset=true;
				}
				
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
					System.out.println("added to prio "+ indexandtermsplit[1]);
				}
				else
				{
				chainarraylist.add(docids);
				System.out.println("added to chain "+indexandtermsplit[1]);
				}
				if(getresult||priorityoperatorset)
				{
					while(count>=0 && priorityarraylist.size()>1 && starts<=count) ///ckeck whether using duplicate for count and ends ///// check for three terms//check for large ones term
					{
						HashMap<String, TreeMap<String, Integer>> resolveddocids=getDocsForOneOperator(priorityoperator[count], priorityarraylist.get(count-1),priorityarraylist.get(count));
						priorityarraylist.remove(count-1);
						priorityarraylist.remove(count-1);
						priorityarraylist.add(resolveddocids);
						priorityoperatorset=false;
				        System.out.println("operator "+ priorityoperator[count]+" applied");
						/*if (!(starts>0))
							count--;*/
				        if(ends>0 ||!(starts>0))
							count--;
				        
						if(ends>0)
						ends--;
						
						/*if(ends==0)
							break;*/
					} 
					if(starts>count)
			        	count++;
				}
				if(count==0 && starts==0)
				{
				if(getresult) ////have to check only at the end it has to be added;all closed bracs are closed only
				{
				chainarraylist.add(priorityarraylist.get(0));
				System.out.println("prio resolved and added to chain");
				priorityarraylist.remove(0);
				getresult=false;
				}
				}
				if(operatorset && !(bracketstrt))
				{
					HashMap<String, TreeMap<String, Integer>> resolveddocids=getDocsForOneOperator(operator, chainarraylist.get(0),chainarraylist.get(1));
					chainarraylist.remove(0);
					chainarraylist.remove(0);
					chainarraylist.add(resolveddocids);
					System.out.println("chain done with operator "+operator);
				}
				if(!started)
					break;
			}
		HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap=new HashMap<String, TreeMap<String, Integer>>();
		FinalDocResultHashmap=chainarraylist.get(0);
		
		return FinalDocResultHashmap;
	}
	
	@SuppressWarnings("resource")
	public  HashMap<String, Double> calculateOKAPI(TreeMap<String, Double> iDFMap,HashMap<String, TreeMap<String, Integer>> finalDocResultHashmap)
	{
        double k1=1.5;
        double b=0.75;
        File dictionaryFile=new File(this.indexdir+ File.separator +"DocumentDictionary2");
        BufferedReader br;
       int totaldocs=0;
      int  totalterms=0;
      HashMap<String, Double> result=new HashMap<String, Double>();
      HashMap<String,Double> normalizeOKAPIMAP= new HashMap<String, Double>();
      HashMap<String, Integer> docidlength= new HashMap<String, Integer>();
		try {
			br = new BufferedReader(new FileReader (dictionaryFile));
			for(String line; (line = br.readLine()) != null;) 
			{
				String[] docidfileidcountsplit=line.split(" ");
				totaldocs=totaldocs+1;
				totalterms=totalterms+Integer.parseInt(docidfileidcountsplit[2].trim());
				docidlength.put(docidfileidcountsplit[0].trim(), Integer.parseInt(docidfileidcountsplit[2].trim()));
				
			}
			int docavaragelenth=totalterms/totaldocs;
			for(String s :finalDocResultHashmap.keySet())
			{
			//	int k=docidlength.get("5842");
				int doclength=docidlength.get(s);
				double idf=0.0;
				double cont=0.0;
				double finalvalue=0.0;	
				TreeMap<String, Integer> doctermfreq= new TreeMap<String, Integer>();
				doctermfreq=finalDocResultHashmap.get(s);
				for(String s1:doctermfreq.keySet())
				{
					if(iDFMap.containsKey(s1))
						idf=iDFMap.get(s1);
					int tf=doctermfreq.get(s1);
					double upper=(k1+1)*tf;
					double lower=((doclength/docavaragelenth)*b+(1-b))*k1+tf;
							cont=upper/lower;
							finalvalue=finalvalue+cont*idf;
				}
				result.put(s, finalvalue);
			}	
			/*double maxvalue=0.0;
			for(String s:result.keySet())
			{
				if(result.get(s)>maxvalue)
				{
					maxvalue=result.get(s);
				}
			}
			for(String s: result.keySet())
			{
				double factor = 1e5; // = 1 * 10^5 = 100000.
				double normalscore=result.get(s)/maxvalue;
				double rounded = Math.round(normalscore * factor) / factor;
				
				normalizeOKAPIMAP.put(s,rounded);
			}
				
			*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
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
			temp=EntryDoc.getValue();
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
		for(int j=0;j<splitSpace.length;j++)
 		{
		String S=splitSpace[j];
			if(S.contains(":"))
			{
				String[] splitColon=S.split(":");
				boolean setBraces=true;
				String str=splitColon[1];
				
				while(setBraces)
				{
					if(!str.startsWith("\"") && (str.endsWith("]")||str.endsWith("}")||str.endsWith(">")||str.endsWith("\"")))
					{
						str=str.substring(0, str.length()-1); //This will remove ] or } that are at the end 
					}
					else if(str.startsWith("\""))
					{
						str=str.substring(1, str.length());
						j=j+1;
											String s=splitSpace[j];
												while(!(s.endsWith("\"") || s.endsWith("]") || s.endsWith("}")))
												{
													System.out.println(s);
													s.trim();
													str=str+" "+s;
													j=j+1;
													s=splitSpace[j];
												}
												while(s.endsWith("\"") || s.endsWith("]") || s.endsWith("}"))
												{
													
													s=s.substring(0, s.length()-1);
												}
												str=str+" "+s;
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
		HashSet<String> termslistmap= new HashSet<String>(TermsList);
		for(String S:termslistmap)
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
		TreeMap<Integer,String> mapInFile=new TreeMap<Integer,String>();
		FileInputStream fis=null;
		ObjectInputStream ois=null;
		int N=0;
		File documentdictionaryFile=new File(this.indexdir+ File.separator +"DocumentDictionary");
		try
		{
            fis=new FileInputStream(documentdictionaryFile);
            ois=new ObjectInputStream(fis);
			mapInFile=(TreeMap<Integer,String>)ois.readObject();
			N=mapInFile.size();
			ois.close();
		    fis.close();
		}
		catch(Exception e){	}	
		for(Entry<String, Integer> entry:termDocCounts.entrySet())
		{
			String Term=entry.getKey().trim();
			int dft=entry.getValue();
			double idft=0;
			if(dft!=0)
			idft=(N/dft);
			idft=Math.log10(idft);
			IDFMap.put(Term, idft);
		}
		return IDFMap;
	}
	
	
	public HashMap<String, TreeMap<String, Integer>> getDocsForOneOperator(String operator,HashMap<String, TreeMap<String, Integer>> first,HashMap<String, TreeMap<String, Integer>> second)
	{
			HashMap<String, TreeMap<String, Integer>> combine=new HashMap<String, TreeMap<String, Integer>>();
			if(operator.equalsIgnoreCase("OR"))
			{
				if(first.size()!=0 && second.size()!=0)
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
				/*String xyz="p";
				String p=xyz;*/
				return combine;
					
			}
				else if(first.size()==0)
				{
					return second;
				}
				else
				{
					return first;
				}
			}
			if(operator.equalsIgnoreCase("ANDNOT"))
			{
				if(first.size()==0 || second.size()==0)
					return first;
				for(String s:second.keySet())
				{
					if(first.keySet().contains(s))
					{
						first.remove(s);
					}
				}
			combine=first;
			return combine;
			}
			if(operator.equalsIgnoreCase("AND"))
			{
				if(first.size()!=0 && second.size()!=0)
				{
			for(String s:first.keySet())
			{
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
		
			}
			else if (first.size()==0)
			{
				return first;
			}
			else
			{
				return second;
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
		BufferedReader br;
		int noofqueries;
		 ArrayList<String> arrayOut=new ArrayList<String>();
		try {
			br=new BufferedReader(new FileReader(queryFile));
			try {
				for(String line;(line=br.readLine())!=null;)
				{
					if(line.contains("="))
					{
						String[] splittedline=line.split("=");
						noofqueries=Integer.parseInt(splittedline[1]);
						String str="numresults "+noofqueries;
						arrayOut.add(str);
						continue;			
					}
					String[] fullquer=line.split(":");
					String fullquery="";
					for(int i=1;i<fullquer.length;i++)
					{
						 fullquery=fullquery+fullquer[i]+":";
					}
					String querystring=fullquery.substring(1,fullquery.length()-2);
					Query query=QueryParser.parse(querystring, "OR");
					String parsedQuery=query.toString();
					HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap=  new HashMap<String, TreeMap<String,Integer>>();
					FinalDocResultHashmap=gethashmap(parsedQuery);
					TreeMap<String, Integer> TermTFCountPairMap=new TreeMap<String, Integer>();
					TermTFCountPairMap=RetrieveTermsInQuery(parsedQuery);
					ArrayList<String> TermsList=new ArrayList<String>();
					for(String str:TermTFCountPairMap.keySet())
					{
						TermsList.add(str);
					}
					TreeMap<String, Double> IDFMap=new TreeMap<String, Double>();
					IDFMap=calculateIDFScores(termDocCounts);
					HashMap<String,Double> RelevanceScoresOKAPI=new HashMap<String, Double>();
					RelevanceScoresOKAPI=calculateOKAPI(IDFMap,FinalDocResultHashmap);
					HashMap<String,Double> sortedmap=getsortedmapofOKAPIvalues(RelevanceScoresOKAPI);//also return the file ids
					
					            String str="";
					            
					            for(Map.Entry<String,Double> entry : sortedmap.entrySet())
								{
									str=str+entry.getKey()+" "+entry.getValue()+",";
								}
					            str=fullquer[0]+":"+"{"+str.substring(0, str.length()-1)+ "}";
					            arrayOut.add(str);
				}
				File dir=new File(this.indexdir);
				writeQueriesToFile(dir.getAbsolutePath() + File.separator +"queryrslt",arrayOut);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public boolean writeQueriesToFile(String InputFileName,ArrayList<String> arrayOut)
	{
		boolean status=false;
		/* ArrayList<String> arrayOut=new ArrayList<String>();
		try{
	            String str="";
	            for(Map.Entry<String,Double> entry : sortedmap.entrySet())
				{
					str=str+entry.getKey()+" "+entry.getValue()+",";
				}
	            str="{"+str.substring(0, str.length()-1)+ "}";
	            arrayOut.add(str);
	        }catch(Exception e){}*/
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
		int termPosition=-1;
		BufferedReader br;
		HashMap<String,TreeMap<String, Integer>> docidtermfreq= new HashMap<String,TreeMap<String, Integer>>();
		try
		{
				int l=-1;
				br = new BufferedReader(new FileReader(dictionaryFile));
				for(String line; (line = br.readLine()) != null;) 
				{
					String[] KeyIndexSplit=line.split(" ");
					if(KeyIndexSplit.length==2)
					{
							if(KeyIndexSplit[0].trim().equalsIgnoreCase(term))
							{
								termPosition=Integer.parseInt(KeyIndexSplit[1].trim());
								l=l+1;
								break;
							}	
							}
				}
				if(termPosition==-1)
				{
					br.close();
					HashMap<String,TreeMap<String, Integer>> noterms= new HashMap<String,TreeMap<String, Integer>>();
					return noterms;
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
								Posting.put(term,1);
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