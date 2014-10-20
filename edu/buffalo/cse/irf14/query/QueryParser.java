
/**
 * 
 */
package edu.buffalo.cse.irf14.query;


//import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;
import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author Bade
 * Static parser that converts raw text to Query objects
 */
public class QueryParser
{
	public static StringBuilder multiword=new StringBuilder();
	static Stack<String> generalOperandStack=new Stack<String>();
	static Stack<Character> generalOperatorStack=new Stack<Character>();
	static Stack<String> outputOperandStack=new Stack<String>();
	static int countOpenParenthesis=0;
	static String ConsumeString="";
	private static boolean isEndReached=false;
	private static boolean isError=false;
	public static int countOperators=0;
	public static TreeMap<Integer,Integer[]> quotesInfo=new TreeMap<Integer, Integer[]>();
	
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return 
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator)
	{
		Stack<Character> stack = new Stack<Character>();
		boolean isBalanced=true;
		
		/** 
		 * Checking for balancing of Parenthesis
		 */
        for (int i = 0; i < userQuery.length(); i++) 
        {
            if(userQuery.charAt(i) == '(')   stack.push('(');
            else if(userQuery.charAt(i) == ')')
            {
                if (stack.isEmpty()) isBalanced=false;
                else stack.pop();
            }
        }
        
        if(!stack.isEmpty()) isBalanced=false;
        
        if(isBalanced)
        {
        	HandleSequentialTerms(userQuery,defaultOperator);
        	userQuery=multiword.toString();
        	userQuery.trim();
        	System.out.println(userQuery);
        	
        	multiword=new StringBuilder();//re-Initialize to null
    		String[] splitSpace=userQuery.split(" ");
			String category=null;
			boolean isCategorySet=false;
			boolean isSet=false;
    		for(int j=0;j<splitSpace.length;j++)
    		{
    			String S=splitSpace[j];
    			if(S.contains(":"))
        		{
        			String[] splitColon=S.split(":");
    				if(splitColon[0].contains("Author")) category="Author";
    				else if(splitColon[0].contains("Category")) category="Category";
    				else if(splitColon[0].contains("Place")) category="Place";
    				isCategorySet=true;
        			multiword.append(splitColon[0]+":");
        			S=splitColon[1];
        		}
    			if(S.startsWith("("))
    			{
    				j=loadExpressionToStack(splitSpace,category,S,j);
    				S=ConsumeString.trim();
    				ConsumeString="";
    			}
    			else if(S.equals("AND") || S.equals("OR") || S.equals("NOT") || S.equals("(") || S.equals(")"))
        		{
        			multiword.append(S+" ");
        			countOperators=countOperators+1;
        			isSet=true;
        		}
        		else//either single word or with multiple words having no operators in between
        		{
        			int n=j;
        			int m=0;
        			String QuoteString="";
        			if(S.contains("\""))
        			{
        				multiword.append("\"");
        				multiword.append("* ");
        				QuoteString=QuoteString+S.substring(1);
        				generalOperandStack.push(S.substring(1));
        				j=j+1;
        				m=m+1;
        				S=splitSpace[j];
        				while(!S.contains("\""))
        				{
        					QuoteString=QuoteString+" "+S;
        					j=j+1;
        					m=m+1;
        					multiword.append("* ");
        					generalOperandStack.push(S);
            				S=splitSpace[j];
        				}
        				
        				multiword.append("*");
        				multiword.append("\"");
        				m=m+1;
        				while((S.endsWith(")")||S.endsWith("\"")))
    					{
							char x=S.charAt(S.length()-1);
							S=S.substring(0, S.length()-1);
							if(!(x=='\"')) 								
							{
								multiword.append(x);
								generalOperatorStack.pop();
							}
							else
							{
								
							}
    					}
    					generalOperandStack.push(S);
    					multiword.append(" ");
        				QuoteString=QuoteString+" "+S;
        				Integer[] x={m,countOperators};
						quotesInfo.put(n, x);
        				if(j<=splitSpace.length) 
        				{
        					S=QuoteString;
        					S.trim();
        				}
        				else
        				{
        					break;
        				}
        			}
        			else
        			{
        				int indcloses=0;
        				while(S.endsWith(")"))
        				{
        					indcloses=indcloses+1;
        					S=S.substring(0,S.length()-1);
        					generalOperatorStack.pop();
        				}
        				multiword.append("*");
        				for(int i=1;i<=indcloses;i++)
        					multiword.append(")");
        				multiword.append(" ");
        				generalOperandStack.push(S);
        			}
        		 }
    			if(!isSet)
    			{
    				if(j<splitSpace.length)
    				{
    					String St=splitSpace[j];
    					if(St.equals("AND") || St.equals("OR") || St.equals("NOT") || St.equals("(") || St.equals(")"))
    					{
    						multiword.append(St+" ");
    						countOperators=countOperators+1;
    						isSet=true;
    					}
    				}
    			}
    			if(!isCategorySet) category="Term";
    			PassTermsToTokenFilters(S,category);
    			category=null;
    			isCategorySet=false;
    			isSet=false;
    		}
    		System.out.println(multiword.toString());
    		String initParsedQuery=getFinalParsedQuery();
    		if(isError) initParsedQuery=userQuery;
    		Query query=new Query(initParsedQuery,defaultOperator);
    		DisposeParameters();
	    	return query;
        }
        else return null;
	}
	
	public static void DisposeParameters()
	{
		multiword.delete(0, multiword.length());
		generalOperandStack=new Stack<String>();
		generalOperatorStack=new Stack<Character>();
		outputOperandStack=new Stack<String>();
		countOpenParenthesis=0;
		ConsumeString="";
		isEndReached=false;
		isError=false;
		countOperators=0;
		quotesInfo=new TreeMap<Integer, Integer[]>();
	}
	
	public static String getFinalParsedQuery()
	{
		Stack<String> tempStack=new Stack<String>();
		String finalQuery=null;
		if(generalOperandStack.size()==outputOperandStack.size())
		{
			String finalWord=multiword.toString();
			finalWord.trim();
			multiword=new StringBuilder();
			String[] splitSpace=finalWord.split(" ");
			
			for(int i=splitSpace.length-1;i>-1;i--)
			{
				String in=splitSpace[i];
				if(in.contains("*"))
				{
					String out=outputOperandStack.pop();
					in=in.replaceAll("\\*", out);
				}
				tempStack.push(in);
			 }
			while(!tempStack.isEmpty())
			{
				String temp=tempStack.pop();
				multiword.append(temp+" ");
			}
			System.out.println(multiword);
			finalQuery=multiword.toString();
			finalQuery.trim();
		}
		else
		{
			isError=true;
			System.out.println("Sorry, some problem while applying tokenfilters to QUERY");
		}
		
		return finalQuery;
	}
	
	public static boolean PassTermsToTokenFilters(String s,String category)
	{
		
		try
		{
			Tokenizer tokenizer=new Tokenizer();
			AnalyzerFactory af=AnalyzerFactory.getInstance();
			TokenStream stream = tokenizer.consume(s);
			if(category.equals("Place"))
			{
				Analyzer analyzerForPlace=af.getAnalyzerForField(FieldNames.PLACE, stream);
				while(analyzerForPlace.increment())
				{
					
				}
				stream=analyzerForPlace.getStream();
				stream.reset();
				
			}
			else if(category.equals("Author"))
			{
				Analyzer analyzerForAuthor=af.getAnalyzerForField(FieldNames.AUTHOR, stream);
				while(analyzerForAuthor.increment())
				{
					
				}
				stream=analyzerForAuthor.getStream();
				stream.reset();
			}
			else if(category.equals("Term"))
			{
				Analyzer a=af.getAnalyzerForField(FieldNames.CONTENT, stream);
				while(a.increment())
				{
				
				}
				stream=a.getStream();
				stream.reset();
			}
			int k=-1;
			boolean isQuote=false;
			while(stream.hasNext())
			{
				Token token=stream.next();
				String temp=token.getTermText();
				k=k+1;
				System.out.println(temp);
				temp.trim();
				String[] splitSpace=temp.split(" ");
				if(splitSpace.length>1)
				{
					for(Entry<Integer, Integer[]> entry:quotesInfo.entrySet())
					{
						int key=entry.getKey()-entry.getValue()[1];
						if(k==key)
						{
							for(int i=0;i<splitSpace.length;i++)
							outputOperandStack.push(splitSpace[i]);
							isQuote=true;
							break;
						}
					}
					if(!isQuote)
					{
						String quoteString="\""+temp+"\"";
						outputOperandStack.push(quoteString);
					}
				}
				else
				{
					outputOperandStack.push(temp);
				}
				
			}
			stream.reset();
			
		}
		catch(Exception e)
		{
			System.out.println("Exception Caught!!"+e.getMessage());
		}
		return true;
	}
	
	public static int loadExpressionToStack(String[] splitSpace,String cat,String str,int j)
	{
		
		while(str.startsWith("("))
		{
			multiword.append("(");
			countOpenParenthesis=countOpenParenthesis+1;
			generalOperatorStack.push('(');
			str=str.substring(1,str.length());
		}
		if(str.contains("\""))
		{
			int m=0;
			int n=j;
			multiword.append("\"");
			multiword.append("* ");
			ConsumeString=ConsumeString+" "+str.substring(1);
			generalOperandStack.push(str.substring(1));
			m=m+1;
			j=j+1;
			str=splitSpace[j];
			while(!(str.endsWith("\"") || str.endsWith(")")))
			{
				ConsumeString=ConsumeString+" "+str;
				multiword.append("* ");
				generalOperandStack.push(str);
				j=j+1;
				m=m+1;
				str=splitSpace[j];
			}
			if(str.contains("\""))
			{
				m=m+1;
				str=splitSpace[j];
				multiword.append("*");
				multiword.append("\"");
				while((str.endsWith(")")||str.endsWith("}")||str.endsWith("\"")))
				{
					char x=str.charAt(str.length()-1);
					str=str.substring(0, str.length()-1);
					if(!(x=='\"')) 
					{
						countOpenParenthesis=countOpenParenthesis-1;
						multiword.append(x);
						generalOperatorStack.pop();
					} 
					else
					{
						//Do nothing
					}
				}
				ConsumeString=ConsumeString+" "+str;
				generalOperandStack.push(str);
				multiword.append(" ");
				if(j==splitSpace.length-1) 
				{
					isEndReached=true;
				}
			}
			Integer[] x={m,countOperators};
			quotesInfo.put(n, x);
			
		}
		else
		{
			multiword.append("* ");
			ConsumeString=ConsumeString+" "+str;
			generalOperandStack.push(str);
		}
		if(!isEndReached)
		{
			j=j+1;
			while(!splitSpace[j].endsWith(")") && (!generalOperatorStack.isEmpty()))
			{
				if(splitSpace[j].startsWith("("))
				{
					str=splitSpace[j].trim();
					j=loadExpressionToStack(splitSpace,cat,str,j);
					
					if(generalOperatorStack.isEmpty())
					{
						break;
					}
				}
				else if(splitSpace[j].equals("AND") || splitSpace[j].equals("OR") || splitSpace[j].equals("NOT"))
				{
					multiword.append(splitSpace[j]+" ");
					countOperators=countOperators+1;
				}
				else
				{
					if(splitSpace[j].contains("\""))
					{
						int n=j;
						int m=0;
						multiword.append("\"");
						multiword.append("* ");
						ConsumeString=ConsumeString+" "+splitSpace[j].substring(1);
						generalOperandStack.push(splitSpace[j].substring(1));
						j=j+1;
						m=m+1;
						while(!splitSpace[j].contains("\""))
						{
							m=m+1;
							splitSpace[j]=splitSpace[j].trim();
							ConsumeString=ConsumeString+" "+splitSpace[j];
							multiword.append("* ");
							generalOperandStack.push(splitSpace[j]);
							j=j+1;
						}
						if(splitSpace[j].contains("\""))
						{
							m=m+1;
							str=splitSpace[j];
							multiword.append("*");
							multiword.append("\"");
							while((str.endsWith(")")||str.endsWith("\"")))
	    					{
								char x=str.charAt(str.length()-1);
								str=str.substring(0, str.length()-1);
								if(!(x=='\"')) 								
								{
									countOpenParenthesis=countOpenParenthesis-1;
									multiword.append(x);
									generalOperatorStack.pop();
								}
								else
								{
									
								}
	    					 }
							ConsumeString=ConsumeString+" "+str;
							generalOperandStack.push(str);
							multiword.append(" ");
							if(j==splitSpace.length-1) 
							{
								isEndReached=true;
								Integer[] x={m,countOperators};
								quotesInfo.put(n, x);
								break;
							}
						}
						Integer[] x={m,countOperators};
						quotesInfo.put(n, x);
					}
					else
					{
						multiword.append("* ");
						ConsumeString=ConsumeString+" "+splitSpace[j];
						generalOperandStack.push(splitSpace[j]);
					}
				}
				j=j+1;
			}
		}
		if(!generalOperatorStack.isEmpty())
		{
			multiword.append("*");
			while(splitSpace[j].endsWith(")"))
			{
				splitSpace[j]=splitSpace[j].substring(0, splitSpace[j].length()-1); 
				multiword.append(")");
				generalOperatorStack.pop();
				countOpenParenthesis=countOpenParenthesis-1;
			}
			ConsumeString=ConsumeString+" "+splitSpace[j];
			generalOperandStack.push(splitSpace[j]);
			multiword.append(" ");
		}
		else if(!splitSpace[j].endsWith(")") && !generalOperatorStack.isEmpty())
		{
			j=j-1;
		}
		
		return j;
	}
	
	public static void HandleSequentialTerms(String userQuery,String defaultOperator)
	{
		String[] splitSpace=userQuery.split(" ");
    	for(int k=0;k<splitSpace.length;k++)
    	{
    		String S=splitSpace[k];
    		if(S.contains(":"))
    		{
    			String[] splitColon=S.split(":");
    			multiword.append(splitColon[0]+":");
    			S=splitColon[1];
    		}
    		if(S.contains("("))
    		{
    			int l=0;
    			while(S.startsWith("("))
				{
					multiword.append("(");
					S=S.substring(1,S.length());
					l=l+1;
				}
    			multiword.append(S+" ");
    			k=k+1;
    			S=splitSpace[k];
    			int countopens=l;
    			while(!S.endsWith(")") || countopens>=l)
    			{
    				int countcloses=0;
    				while(S.startsWith("("))
    				{
    					countopens=countopens+1;
    					multiword.append("(");
    					S=S.substring(1,S.length());
    				}
    				while(S.endsWith(")"))
    				{
    					countopens=countopens-1;
    					countcloses=countcloses+1;
    					S=S.substring(0,S.length()-1);
    				}
    				multiword.append(S);
    				for(int i=1;i<=countcloses;i++)
    					multiword.append(")");
    				multiword.append(" ");
    				if(countopens==0) break;
    				else if(k<splitSpace.length)
    				{
    					k=k+1;
    					S=splitSpace[k];
    				}
    				else break;
    			}
    			if(S.endsWith(")") && countopens<=l)
    			{
    				multiword.append(S+" ");
    			}
    			
    		}
    		else if(S.equals("AND") || S.equals("OR") || S.equals("NOT") || S.equals("(") || S.equals(")"))
    		{
    			multiword.append(S+" ");
    		}
    		else//either single word or with multiple words having no operators in between
    		{
    			String QuoteString="";
    			if(S.contains("\""))
    			{
    				QuoteString=QuoteString+S;
    				k=k+1;
    				S=splitSpace[k];
    				while(!S.contains("\""))
    				{
    					QuoteString=QuoteString+" "+S;
    					k=k+1;
        				S=splitSpace[k];
    				}
    				QuoteString=QuoteString+" "+S;
    				if(k<=splitSpace.length) 
    				{
    					S=QuoteString;
    					S.trim();
    				}
    				else
    				{
    					break;
    				}
    			}
    		
    			boolean TargetSet=false;
    			while(k<(splitSpace.length-1))
    			{
    				String x=splitSpace[k+1];
    				 if(!(x.equals("AND") || x.equals("OR") || x.equals("NOT")))
    				 {
    					 if(TargetSet==false)
         				{
         					multiword.append("("+S);
         					TargetSet=true;
         				}
         				else
         				{
         					multiword.append(" "+defaultOperator+" "+S);
         				}
         				k=k+1;
         				S=splitSpace[k];
    				 }
    				 else
    				 {
    					 break;
    				 }
    			}
    			if(TargetSet) multiword.append(" "+defaultOperator+" "+S+")"+" ");
    			else multiword.append(S+" ");
    			TargetSet=false;
    		}
    		
    	}
		
	}
	
}