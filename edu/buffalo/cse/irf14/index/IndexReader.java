/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;


/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	public String indexDir=null;
	public String indexType=null;
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		
		File dir=new File(indexDir);
		this.indexDir=dir.getAbsolutePath();
		if(type==IndexType.TERM)
			this.indexType=dir.getAbsolutePath() + File.separator +"TERM";
		if(type==IndexType.AUTHOR)
			this.indexType=dir.getAbsolutePath() + File.separator +"AUTHOR";
		if(type==IndexType.PLACE)
			this.indexType=dir.getAbsolutePath() + File.separator +"PLACE";
		if(type==IndexType.CATEGORY)
			this.indexType=dir.getAbsolutePath() + File.separator +"CATEGORY";
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms()
	{
		
		File dictionaryFile=new File(indexType+ File.separator +"Dictionary");
		int totalKeyTerms=0;
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(dictionaryFile));
			
			for(@SuppressWarnings("unused")
			String line; (line = br.readLine()) != null;) 
			{
				totalKeyTerms=totalKeyTerms+1;
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Sorry,File Not Found!"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Sorry,an IO Error Occures!"+e.getMessage());
			e.printStackTrace();
		}
		
		return totalKeyTerms;
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		File dictionaryFile=new File(indexDir+ File.separator +"DocumentDictionary");
		int totalValueTerms=0;
		try{
            
            FileInputStream fis=new FileInputStream(dictionaryFile);
            ObjectInputStream ois=new ObjectInputStream(fis);

            @SuppressWarnings("unchecked")
			TreeMap<String,Integer> mapInFile=(TreeMap<String,Integer>)ois.readObject();
            totalValueTerms=mapInFile.size();
            ois.close();
            fis.close();
            //print All data in MAP
           
        }catch(Exception e){}		
		return totalValueTerms;
		
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term)
	{
		File dictionaryFile=new File(indexType+ File.separator +"Dictionary");
		int TermPosition=-1;
		HashMap<String, Integer> Posting=new HashMap<String, Integer>();
		Map<String,Integer> finalPosting = new TreeMap<String, Integer>();
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(dictionaryFile));
			
			for(String line; (line = br.readLine()) != null;) 
			{
				String[] KeyIndexSplit=line.split(" ");
				if(KeyIndexSplit.length==2)
				{
					if(KeyIndexSplit[0].equals(term))
					{
						TermPosition=Integer.parseInt(KeyIndexSplit[1].trim());
						break;
					}
				}
			}
			if(TermPosition!=-1)
			{
				File postingsFile=new File(indexType+ File.separator +"Postings");
				br.close();
				br = new BufferedReader(new FileReader(postingsFile));
				for(int i=0;i<TermPosition;i++) 
				{
					br.readLine();
				}
				String HashString=br.readLine();
				String[] TrmPstngSplit=HashString.split(":");
				TrmPstngSplit[0].trim();
				TrmPstngSplit[1].trim();
				TrmPstngSplit[1]=TrmPstngSplit[1].substring(0,TrmPstngSplit[1].length()-1);
				String[] PstngSplit=TrmPstngSplit[1].split("-");
				
				for(int i=0;i<PstngSplit.length;i++)
				{
					PstngSplit[i].trim();
					String[] DocFreqSplit=PstngSplit[i].split(" ");
					Posting.put(DocFreqSplit[0], Integer.parseInt(DocFreqSplit[1]));
				}
			}
			else
			{
				br.close();
				return null;
			}
			
			File documentdictionaryFile=new File(indexDir+ File.separator +"DocumentDictionary");
			try{
	            
	            FileInputStream fis=new FileInputStream(documentdictionaryFile);
	            ObjectInputStream ois=new ObjectInputStream(fis);

	            @SuppressWarnings("unchecked")
				TreeMap<String,Integer> mapInFile=(TreeMap<String,Integer>)ois.readObject();
	           // totalValueTerms=mapInFile.size();
	            for(Map.Entry<String,Integer> entry : mapInFile.entrySet())
				{
					for(Map.Entry<String,Integer> entry2 : Posting.entrySet())
					{
						String w=entry.getKey();
						int x=entry.getValue();
						int y=Integer.parseInt(entry2.getKey());
						int z=entry2.getValue();
						if(x==y)
						{
							finalPosting.put(w, z);
							break;
						}
					}
				}
	            
	            
	            ois.close();
	            fis.close();
	        }catch(Exception e){}	
			
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Sorry,File Not Found!"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Sorry,an IO Error Occures!"+e.getMessage());
			e.printStackTrace();
		}
		
		return finalPosting;
	}
	
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) 
	{
		if(k>=1)
		{
			File postingsFile=new File(indexType+ File.separator +"Postings");
			Map<String, Integer> CountPosting=new HashMap<String, Integer>();
			List<String> TopStrings=new ArrayList<String>();
			BufferedReader br;
			try
			{
				br = new BufferedReader(new FileReader(postingsFile));
				
				for(String line; (line = br.readLine()) != null;) 
				{
					String[] TrmPstngSplit=line.split(":");
					TrmPstngSplit[0].trim();
					TrmPstngSplit[1].trim();
					TrmPstngSplit[1]=TrmPstngSplit[1].substring(0,TrmPstngSplit[1].length()-1);
					String[] PstngSplit=TrmPstngSplit[1].split("-");
					int count=0;
					for(int i=0;i<PstngSplit.length;i++)
					{
						PstngSplit[i].trim();
						String[] DocFreqSplit=PstngSplit[i].split(" ");
						count=count+Integer.parseInt(DocFreqSplit[1]);
					}
					CountPosting.put(TrmPstngSplit[0],count);
				}
				 Map<String, Integer> sortedMapAsc = sortByComparator(CountPosting);
				 int i=0;
				 for(Map.Entry<String,Integer> entry : sortedMapAsc.entrySet())
				{
						TopStrings.add(entry.getKey());
						i++;
						if(i==k) break;
				}
				 
				 File dictionaryFile=new File(indexType+ File.separator +"Dictionary");
				 br.close();
				 br = new BufferedReader(new FileReader(dictionaryFile));
				 int l=0;
				 for(String line; (line = br.readLine()) != null;) 
				{
					 	if(l==k) break;
						String[] KeyIndexSplit=line.split(" ");
						if(KeyIndexSplit.length==2)
						{
							for(int i1=0;i1<TopStrings.size();i1++)
							{
								if(KeyIndexSplit[1].trim().equals(TopStrings.get(i1)))
								{
									TopStrings.remove(i1);
									TopStrings.add(i1, KeyIndexSplit[0]);
									l++;
									break;
								}
								
							}
						}
					}
				br.close();
			} catch (FileNotFoundException e) {
				System.out.println("Sorry,File Not Found!"+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Sorry,an IO Error Occures!"+e.getMessage());
				e.printStackTrace();
			}
		
			return TopStrings;
		}
		else return null;
		
	}
	
	/**
	 * Method to return SortedMap with Given UnsortedMap
	 * @param unsortMap
	 * @return sortmap
	 * Reference: http://www.mkyong.com/java/how-to-sort-a-map-in-java/
	 */
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {
		 
		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
		});
 
		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		File dictionaryFile=new File(indexType+ File.separator +"Dictionary");
		Integer[] termPositions=new Integer[terms.length];
		ArrayList<HashMap<String, Integer>> TermPostingslist=new ArrayList<HashMap<String, Integer>>();
		TreeMap<String, Integer> finalposting=new TreeMap<String, Integer>();
		HashMap<String, Integer> basemap=null;
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
							if(KeyIndexSplit[0].trim().equals(terms[i1]))
							{
								termPositions[i1]=Integer.parseInt(KeyIndexSplit[1].trim());
								l++;
								break;
							}				
						}
					}
				}
				if(l==terms.length)
				{
					List<Integer> wordList = Arrays.asList(termPositions);
					Collections.sort(wordList);
					File postingsFile=new File(indexType+ File.separator +"Postings");
					br.close();
					br = new BufferedReader(new FileReader(postingsFile));
					
					for(Integer j:wordList)
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
							Posting.put(DocFreqSplit[0], Integer.parseInt(DocFreqSplit[1]));
						}
						TermPostingslist.add(Posting);
						br.close();
					}
					
					
					 basemap = TermPostingslist.get(0);
					int len = TermPostingslist.size();
					String key;
					int value;
					for(int i1=1;i1<len;i1++)
					{
						basemap.keySet().retainAll(TermPostingslist.get(i1).keySet());
						for (Entry<String, Integer> entry : TermPostingslist.get(i1).entrySet())
						{
							key = entry.getKey();
							value = entry.getValue();
							if (basemap.containsKey(key))
							{
								basemap.put(key, basemap.get(key) + value);
							}
						}
					}
					
					if(!basemap.isEmpty())
					{
						File documentdictionaryFile=new File(indexDir+ File.separator +"DocumentDictionary");
						try{
				            
				            FileInputStream fis=new FileInputStream(documentdictionaryFile);
				            ObjectInputStream ois=new ObjectInputStream(fis);

				            @SuppressWarnings("unchecked")
							TreeMap<String,Integer> mapInFile=(TreeMap<String,Integer>)ois.readObject();
				           // totalValueTerms=mapInFile.size();
				            for(Map.Entry<String,Integer> entry : mapInFile.entrySet())
							{
								for(Map.Entry<String,Integer> entry2 : basemap.entrySet())
								{
									String w=entry.getKey();
									int x=entry.getValue();
									int y=Integer.parseInt(entry2.getKey());
									int z=entry2.getValue();
									if(x==y)
									{
										finalposting.put(w, z);
										break;
									}
								}
							}
				            ois.close();
				            fis.close();
				            //print All data in MAP
				           
				        }catch(Exception e){}	
						
					
					}
					else
					{
						return null;
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
		
		return finalposting;
	}
}
