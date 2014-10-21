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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
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
	public void query(String userQuery, ScoringModel model)
	{
		long querystarts = System.currentTimeMillis();
		Query query=QueryParser.parse(userQuery, "OR");
		if(query!=null)
		{
		String parsedQuery=query.toString();
		HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap=  new HashMap<String, TreeMap<String,Integer>>();
		FinalDocResultHashmap=hashmapwithstak(parsedQuery);
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
		HashMap<String, TreeMap<String, Double>> WtdHashmap=new HashMap<String, TreeMap<String,Double>>();
		HashMap<String,Double> RelevanceScores=new HashMap<String, Double>();
		
		switch(model)
		{
		case TFIDF:
		{
			WtqMap=calculateQueryTFIDFScore(TermTFCountPairMap,IDFMap);
			WtdHashmap=calculateDocTFIDFScores(FinalDocResultHashmap, IDFMap);
			RelevanceScores=CalculateFinalDocTFIDFRelevanceScores(TermsList,WtdHashmap,WtqMap);
			break;
		}
			
		case OKAPI:
		{
			RelevanceScores=calculateOKAPI(IDFMap,FinalDocResultHashmap);
			break;
		}
			
		default:break;
		}
		List<Entry<String,Double>> sortedmapwithfileids=getsortedmapwithcomparator(RelevanceScores);
		long queryends = System.currentTimeMillis();
		stream.println("Query: "+userQuery);
		stream.println("Query Time: "+(queryends-querystarts)+" in ms");
		File corpDirectory = new File(corpusDir);
		Document d=null;
		int i=1;
		for(Entry<String,Double> entry:sortedmapwithfileids)
		{
			stream.println("Result Rank: "+i);
			try {
				d = Parser.parse(corpDirectory.getAbsolutePath() + File.separator +entry.getKey());
			} catch (ParserException e) {
				e.printStackTrace();
			}
			stream.println("Result Title: "+d.getField(FieldNames.TITLE)[0]);
			String[] con=d.getField(FieldNames.CONTENT);
			String str=con[0];
			String[] contentarray=str.split("\r");
			stream.println("Result Snippet: "+contentarray[0]);
			if(contentarray.length>3)
			{
				stream.println(contentarray[1]);
				stream.println(contentarray[2]);
			}
			stream.println("Result relevancy: "+entry.getValue()+"\n");
			i=i+1;
		}
	}
	
	}
	@SuppressWarnings("resource")
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		List<Entry<String,Double>> sortedEntries = new ArrayList<Entry<String,Double>>(mapwithfileids.entrySet());
		Collections.sort(sortedEntries,new Comparator<Entry<String,Double>>() {
            public int compare(Entry<String,Double> e1, Entry<String,Double> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        }
		);
		if(sortedEntries.size()>10)
		sortedEntries=sortedEntries.subList(0, 10);
		return sortedEntries;
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
			double maxvalue=0.0;
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return normalizeOKAPIMAP;
	}

	@SuppressWarnings("resource")
	public HashMap<String,Double> CalculateFinalDocTFIDFRelevanceScores(ArrayList<String> TermsList,HashMap<String, TreeMap<String, Double>> WtdHashmap,TreeMap<String,Double> WtqMap)
	{
		 File dictionaryFile=new File(this.indexdir+ File.separator +"DocumentDictionary2");
	        BufferedReader br;
	      int  totalterms=0;
	      HashMap<String, Integer> docidlength= new HashMap<String, Integer>();
	      try
	      {
				br = new BufferedReader(new FileReader (dictionaryFile));
				for(String line; (line = br.readLine()) != null;) 
				{
					String[] docidfileidcountsplit=line.split(" ");
					totalterms=totalterms+Integer.parseInt(docidfileidcountsplit[2].trim());
					docidlength.put(docidfileidcountsplit[0].trim(), Integer.parseInt(docidfileidcountsplit[2].trim()));		
				}
	      }
	      catch(Exception e)
	      {
	    	  System.out.println(e.getMessage());
	      }
	      
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
			String docid=DocIDsList[d];
			int doculength=0;
			for(String s: docidlength.keySet())
			{
				if(s.equals(docid))
				{
					doculength=docidlength.get(s);
				}
			}
			Scores[d]=Scores[d]/doculength;
			RelevanceScores.put(EntryDoc.getKey(), Scores[d]);
		}
		HashMap<String,Double> tempRelevanceScores= new HashMap<String, Double>();
		double highest = Scores[0];
	    for (int index = 1; index < Scores.length; index ++) {
	        if (Scores[index] > highest) {
	            highest = Scores [index];
	        }
	        for(String s:RelevanceScores.keySet())
	        {
	        	double score=RelevanceScores.get(s);
	        	score=score/highest;
	        	double factor=1e5;
	        	double rounded = Math.round(score * factor) / factor;
	        	tempRelevanceScores.put(s, rounded);
	        }
	    }
		return tempRelevanceScores;
	}
	/*
	 * Maps should have keys in descending order from largest to smallest
	 */
	public double calculateNDCGMeasure(TreeMap<String, Double> ResultantDocRelvsD1, TreeMap<String, Double> ActualDocRelvsD1)
	{
		double DCG=0.0;
		double iDCG=0.0;
		int i=1;
		int j=1;
		for(Entry<String,Double> entry:ActualDocRelvsD1.entrySet())
		{
			double r=entry.getValue();
			if(j==1) iDCG=iDCG+r;
			else iDCG=iDCG+(r/Math.log10(j));
			j=j+1;
		}
		for(Entry<String,Double> entry:ResultantDocRelvsD1.entrySet())
		{
			double r=entry.getValue();
			if(i==1) DCG=DCG+r;
			else DCG=DCG+(r/Math.log10(i));
			i=i+1;
		}
		double NDCG=(DCG/iDCG);
		return NDCG;
	}
	
	public double calculateFMeasure(ArrayList<String> ResultantDocsD1,ArrayList<String> ActualDocsD)
	{
		ArrayList<String> finalDocs=IntersectDocs(ResultantDocsD1,ActualDocsD);
		 int P = (finalDocs.size()/ResultantDocsD1.size());
		 int R = (finalDocs.size()/ActualDocsD.size());
		 double FBy2Measure = (1.25 * P * R) / (0.25 * P + R);
		 return FBy2Measure;
	}
	public ArrayList<String> IntersectDocs(ArrayList<String> ResultantDocsD1,ArrayList<String> ActualDocsD)
	{
		ArrayList<String> finalDocs=new ArrayList<String>();
		HashSet <String> resultantset = new HashSet <String>();
		for (String s : ResultantDocsD1)
		{
            if(ActualDocsD.contains(s))
            {
            	resultantset.add(s);
            }
        }
		finalDocs.addAll(resultantset);
		return finalDocs;
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
	
	@SuppressWarnings("unchecked")
	public HashMap<String, TreeMap<String, Integer>> hashmapwithstak(String parsedQuery)
	{
		
		Stack<String> operators= new Stack<String>();
		Stack<String> terms= new Stack<String>();
		//Stack secondstack= new Stack();
		String[] splitintospaces=parsedQuery.split(" ");
		boolean quoteset=false;
		int index=0;
		int nextindex=0;
		@SuppressWarnings("rawtypes")
		Stack thirdstack=new Stack();
		try
		{
		for(String s: splitintospaces)
		{
			if(quoteset)
			{
				if(index<nextindex)
				{
					index++;
					continue;
				}
				quoteset=false;
			}
			if(s.equals("}"))
			{
				int i=operators.size();
			    while(!operators.get(i-1).equals("{"))
			    {
			    	String operator=operators.get(i-1);
			    	terms.push(operator);
			    	operators.pop();
			    	i--;
			    }
			    operators.pop();
			}
			else if(s.equals("]"))
			{
				int i=operators.size();
			    while(!operators.get(i-1).equals("["))
			    {
			    	String operator=operators.get(i-1);
			    	terms.push(operator);
			    	operators.pop();
			    	i--;
			    }
			    operators.pop();
			}
			else if(s.equals("[")||s.equals("AND")||s.equals("OR")||s.equals("{"))
			{
				if((s.equals("OR")&& operators.elementAt(operators.size()-1).equals("AND"))||(s.equals("OR")&& operators.elementAt(operators.size()-1).equals("AND")))
				{
					operators.pop();
					terms.push("AND");
				}
				operators.push(s);
			}
			else
			{
				if(s.contains("\""))
				{
					String sm=s;
					s=s.replace("\"","");
				//	int index=0;
				int len=splitintospaces.length;
				for(int i=0;i<len;i++)
				{
					if(splitintospaces[i].equals(sm))
					{
						index=i;
						break;
					}		
				}
				//int nextindex=0;
				for(int i=index+1;i<splitintospaces.length;i++)
				{
					if(splitintospaces[i].contains("\""))
					{
						nextindex=i;
						break;
					}
				}
				String quote="";
				for(int i=index+1;i<=nextindex;i++)
				{
					quote=quote+" "+splitintospaces[i].substring(0,splitintospaces[i].length());	
				}
				s=s+" "+quote.substring(1,quote.length()-1);
				terms.add(s);
				quoteset=true;
				}		
				else
				{
				terms.push(s);
				}
			}
       	}
		if(terms.size()==1)
		{
			 String s=terms.pop();
			 String indextermsplit[]=s.split(":");
			 IndexType indextype;
				if(indextermsplit[0].equalsIgnoreCase("Term"))
				{
					indextype=IndexType.TERM;
				}
				else if(indextermsplit[0].equalsIgnoreCase("Author"))
				{
					indextype=IndexType.AUTHOR;
				}
				else if(indextermsplit[0].equalsIgnoreCase("Place"))
				{
					indextype=IndexType.PLACE;
				}
				else
				{
					indextype=IndexType.CATEGORY;
				}
				HashMap<String, TreeMap<String, Integer>> onetermresult=new HashMap<String, TreeMap<String,Integer>>();
				onetermresult=getDOCIDs(indextype,indextermsplit[1]);
				termDocCounts.put(indextermsplit[1],onetermresult.size());
			 return getDOCIDs(indextype,indextermsplit[1]);
		}
		for(int i=0;i<terms.size();i++)
		{
			String term=terms.get(i);
			if(!(term.equals("OR")||term.equals("AND")))
			{
				thirdstack.push(terms.get(i));
			}
			else
			{
				String operator= terms.get(i).toString();
				String firstpoppeddatatype=thirdstack.elementAt(thirdstack.size()-1).getClass().getName();
				HashMap<String, TreeMap<String, Integer>> first =new HashMap<String, TreeMap<String,Integer>>();
				HashMap<String, TreeMap<String, Integer>> second =new HashMap<String, TreeMap<String,Integer>>();
				if(firstpoppeddatatype!="java.lang.String")
				{
                    first=(HashMap<String, TreeMap<String, Integer>>) thirdstack.pop();
				}
				else
				{
					String s=(String) thirdstack.pop();
					if(s.contains("<"))
					{
						s=s.substring(1,s.length()-1);
						operator="ANDNOT";
					}
					String[] indextermsplitfirst=s.split(":");
					
					IndexType indextype;
					if(indextermsplitfirst[0].equalsIgnoreCase("Term"))
					{
						indextype=IndexType.TERM;
					}
					else if(indextermsplitfirst[0].equalsIgnoreCase("Author"))
					{
						indextype=IndexType.AUTHOR;
					}
					else if(indextermsplitfirst[0].equalsIgnoreCase("Place"))
					{
						indextype=IndexType.PLACE;
					}
					else
					{
						indextype=IndexType.CATEGORY;
					}
					 first= getDOCIDs(indextype,indextermsplitfirst[1]);
					termDocCounts.put(indextermsplitfirst[1], first.size());
				}
				String secondpoppeddatatype=thirdstack.elementAt(thirdstack.size()-1).getClass().getName();
				if(secondpoppeddatatype!="java.lang.String")
				{
                           second=(HashMap<String, TreeMap<String, Integer>>) thirdstack.pop();
				}
				else
				{
					String s=(String) thirdstack.pop();
					if(s.contains("<"))
					{
						s=s.substring(1,s.length()-1);
						operator="ANDNOT";
					}
					String[] indextermsplitfirst=s.split(":");
					IndexType indextype;
					if(indextermsplitfirst[0].equalsIgnoreCase("Term"))
					{
						indextype=IndexType.TERM;
					}
					else if(indextermsplitfirst[0].equalsIgnoreCase("Author"))
					{
						indextype=IndexType.AUTHOR;
					}
					else if(indextermsplitfirst[0].equalsIgnoreCase("Place"))
					{
						indextype=IndexType.PLACE;
					}
					else
					{
						indextype=IndexType.CATEGORY;
					}
					 second= getDOCIDs(indextype,indextermsplitfirst[1]);
					termDocCounts.put(indextermsplitfirst[1], second.size());
				}
				HashMap<String, TreeMap<String, Integer>> result= getDocsForOneOperator(operator, second, first);
				thirdstack.push(result);
			}
				
		}
		}
		catch(Exception e)
		{
			System.out.println("error in stack implementation"+e.getMessage());
		}
		return (HashMap<String, TreeMap<String, Integer>>) thirdstack.get(0);
	}
	
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
		BufferedReader br;
		@SuppressWarnings("unused")
		int noofqueries=0;
		int numresults=0;
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
					if(query!=null)
					{
						String parsedQuery=query.toString();
						HashMap<String, TreeMap<String, Integer>> FinalDocResultHashmap=  new HashMap<String, TreeMap<String,Integer>>();
						FinalDocResultHashmap=hashmapwithstak(parsedQuery);
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
						List<Entry<String,Double>> sortedList=getsortedmapwithcomparator(RelevanceScoresOKAPI);//also return the file ids
						if(FinalDocResultHashmap.size()!=0)
						{
							numresults=numresults+1;
						}
						String str="";
						            
						 for(Entry<String,Double> entry : sortedList)
						{
							 str=str+entry.getKey()+"#"+entry.getValue()+", ";
						}
					    str=fullquer[0]+":"+"{"+str.substring(0, str.length()-2)+ "}";
					    arrayOut.add(str);
					}
				}
				writeQueriesToFile(arrayOut,numresults);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void writeQueriesToFile(ArrayList<String> arrayOut,int num)
	{
		try
		{
			stream.println("numResults="+num);
			for(String s:arrayOut)
				stream.println(s);
		}
	
		catch(Exception e)
		{
			System.out.println("Sorry,Some error occurred while writing back to PrintStream!"+e.getMessage());
		}
		
	}
	/**
	 * General cleanup method
	 */
	public void close() {
		stream.flush();
		stream.close();
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
					else if(KeyIndexSplit.length==3)
					{
						String search=KeyIndexSplit[0].trim()+" "+KeyIndexSplit[1].trim();
						if(search.equalsIgnoreCase(term))
						{
							termPosition=Integer.parseInt(KeyIndexSplit[2].trim());
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