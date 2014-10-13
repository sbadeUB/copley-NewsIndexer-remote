
/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Bade
 * Static parser that converts raw text to Query objects
 */
public class QueryParser
{
	String[] Categories={"Author","Category","Place"};
	public static StringBuilder multiword=new StringBuilder();
	Stack<Character> generalOperatorStack=new Stack<Character>();
	boolean endReached=true;
	
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return 
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator)
	{
		@SuppressWarnings("unused")
		String[] Categories={"Author","Category","Place"};
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
        	ArrayList<String> TermsList=new ArrayList<String>();
    		String[] splitSpace=userQuery.split(" ");
    		for(int j=1;j<splitSpace.length;j++)
    		{
    			String S=splitSpace[j];
    			if(S.contains(":"))
    			{
    				String[] splitColon=S.split(":");
    				boolean setBraces=true;
    				String category=null;
    				if(splitColon[0].contains("Author")) category="Author";
    				else if(splitColon[0].contains("Category")) category="Category";
    				else if(splitColon[0].contains("Place")) category="Place";
    				multiword.append(splitColon[0]+":");
    				String str=splitColon[1];
    				String ConsumeString=null;
    				while(setBraces)
    				{
    					while(str.startsWith("("))
    					{
    						multiword.append("(");
    						str=str.substring(1,str.length());
    					}
    					//String ConsumeString=null;
    					if(!str.startsWith("\"") && (str.endsWith("]")||str.endsWith("}")||str.endsWith(">")||str.endsWith("\"")))
    					{
    						str=str.substring(0, str.length()-1); //This will remove ] or } that are at the end 
    					}
    					else if(str.startsWith("\""))
    					{
    						str=str.substring(1, str.length());
    						j=j+1;
    						String s=splitSpace[j];
    						while(!s.endsWith("\""))
    						{
    							s.trim();
    							str=str+" "+s;
    							j=j+1;
    							s=splitSpace[j];
    						}
    						str=str+" "+s;
    					}
    					else setBraces=false;
    				}
    				str=str.trim();
    				TermsList.add(str);
    			}
    		}
        	String initParsedQuery=userQuery;
        	//Do some processing
    		Query query=new Query(initParsedQuery,defaultOperator);
	    	return query;
        }
		
        else return null;
		
	}
	
	public int HandleTerms(String[] splitSpace,String cat,String str,int j,String ConsumeString)
	{
		int countOpenBracks=0;
		while(str.startsWith("("))
		{
			multiword.append("(");
			generalOperatorStack.push('(');
			countOpenBracks=countOpenBracks+1;
			str=str.substring(1,str.length());
		}
		multiword.append("* ");
		ConsumeString=ConsumeString+" "+str;
		generalOperatorStack.push('*'); //Indicates a word is to be appended here
		j=j+1;
		while(!splitSpace[j].endsWith(")"))
		{
			if(splitSpace[j].startsWith("("))
			{
				str=splitSpace[j].trim();
				j=HandleTerms(splitSpace,cat,str,j,ConsumeString);
				if(generalOperatorStack.isEmpty()) break;
			}
			else if(splitSpace[j].equals("AND") || splitSpace[j].equals("OR") || splitSpace[j].equals("NOT"))
			{
				multiword.append(splitSpace[j]+" ");
			}
			else
			{
				if(splitSpace[j].contains("\""))
				{
					multiword.append("\"");
					multiword.append("* ");
					ConsumeString=ConsumeString+" "+str;
					generalOperatorStack.push('\"');
					generalOperatorStack.push('*');
					j=j+1;
					while(!splitSpace[j].contains("\""))
					{
						splitSpace[j]=splitSpace[j].trim();
						ConsumeString=ConsumeString+" "+splitSpace[j];
						multiword.append("* ");
						j=j+1;
					}
					if(splitSpace[j].contains("\""))
					{
						str=splitSpace[j];
						multiword.append("*");
						generalOperatorStack.push('*');
						while((str.endsWith(")")||str.endsWith("}")||str.endsWith("\"")))
    					{
							String x=str.substring(str.length(), str.length());
							str=str.substring(0, str.length()-1);
							if(x=="\"") multiword.append("\"");
							else multiword.append("x");
							generalOperatorStack.push(x.charAt(0));
    						 //This will remove ] or } or " that are at the end 
    					}
						ConsumeString=ConsumeString+" "+str;
					}
					
				}
				else
				{
					multiword.append("* ");
					generalOperatorStack.push('*');
					ConsumeString=ConsumeString+" "+splitSpace[j];
				}
			}
			
		}
		
		/*
		 * if(splitSpace[i].startsWith("("))
				{
					str=splitSpace[i].trim();
					i=processOnMultiwordParenthesis(splitSpace,category,str,i);
					if(stack.isEmpty()) break;
				}
				else if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
				{
					multiWord.append(splitSpace[i]+" ");
				}
				else if(splitSpace[i].equals("NOT"))
				{
					multiWord.append("AND"+" ");
					isCategoryAngledNOTFound=true;
				}
				else
				{
					if(splitSpace[i].contains("\""))
					{
						multiWord.append("<"+category+":"+splitSpace[i]+" ");
						i=i+1;
						while(!splitSpace[i].contains("\""))
						{
							multiWord.append(splitSpace[i]+" ");
							i=i+1;
						}
						if(splitSpace[i].contains("\"") && isCategoryAngledNOTFound)
							multiWord.append(splitSpace[i]+">"+" ");
						else if(!isCategoryAngledNOTFound) 
							multiWord.append(splitSpace[i]+" ");
						isCategoryAngledNOTFound=false;
					}
					else
					{
						if(isCategoryAngledNOTFound) multiWord.append("<"+category+":"+splitSpace[i]+">"+" ");
						else multiWord.append(category+":"+splitSpace[i]+" ");
						isCategoryAngledNOTFound=false;
					}
				}
				i=i+1;
			}
			if(!stack.isEmpty())
			{
				if(splitSpace[i].equals(")") )
				{
					stack.pop();
					if(Character.isWhitespace(multiWord.charAt(multiWord.length() - 1)))
					multiWord.deleteCharAt(multiWord.length() - 1);
					multiWord.append("] ");
				}
				else if(splitSpace[i].contains(")"))
				{
					char[] array=splitSpace[i].toCharArray();
					int countParenthesis=0;
					for(int i1=0;i1<array.length;i1++)
					{
						if(array[i1]==')')
						{
							countParenthesis=countParenthesis+1;
						}
					}
					if(isCategoryAngledNOTFound)
						multiWord.append("<"+category+":"+splitSpace[i].substring(0, splitSpace[i].length()-countParenthesis)+">");
					else
						multiWord.append(category+":"+splitSpace[i].substring(0, splitSpace[i].length()-countParenthesis));
					
					if(countParenthesis==1)
					{
						multiWord.append("] ");
						stack.pop();
					}
					else
					{
						for(int i1=1;i1<=countParenthesis-1;i1++)
						{
							multiWord.append("]");
							stack.pop();
						}
						multiWord.append("] ");
						stack.pop();
					}
					isCategoryAngledNOTFound=false;
				}
			}
		}	
		 */
		
		
		
		
		
		
		
		
		
		if(!str.startsWith("\"") && (str.endsWith("]")||str.endsWith("}")||str.endsWith(">")||str.endsWith("\"")))
		{
			str=str.substring(0, str.length()-1); //This will remove ] or } that are at the end 
		}
		else if(str.startsWith("\""))
		{
			str=str.substring(1, str.length());
			j=j+1;
			String s=splitSpace[j];
			while(!s.endsWith("\""))
			{
				s.trim();
				str=str+" "+s;
				j=j+1;
				s=splitSpace[j];
			}
			str=str+" "+s;
		}
		
		return j;
	}
	
}