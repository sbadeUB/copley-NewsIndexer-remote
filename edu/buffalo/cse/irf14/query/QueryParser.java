
/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.Arrays;
import java.util.Stack;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser
{
	static String finalQuery=null;
	static boolean isNotSet=false;
	static boolean isBooleanSet=false;
	static boolean isCategoryAngledNOTFound=false;
	static boolean isTermAngledNOTFound=false;
	static boolean isCategoryFound=false;
	static int NestedNumber=-1;
	static StringBuilder multiWord=new StringBuilder();
	static String[] Categories={"Author","Category","Place"};
	static Stack<Character> stack = new Stack<Character>();
	
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return 
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator)
	{
		Query query=new Query();
		String initUserQuery=userQuery;
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
        	String[] splitSpace=initUserQuery.split(" ");
    		if(splitSpace.length==1)
    		{
    			finalQuery=processOnSingleWord(splitSpace[0]);
    		}
    		else
    		{
    	        
    			multiWord.append('{');
    			for(int i=0;i<splitSpace.length;i++)
    			{
    				String str=splitSpace[i];
    				str.trim();
    				String[] splitColon=str.split(":");
    				if(Arrays.asList(Categories).contains(splitColon[0].trim()))
    				{
    					str=splitColon[1].trim();
    					i=processOnMultiwordParenthesis(splitSpace,splitColon[0].trim(),str,i);
    				}
    				
    				else if(!isCategoryFound)
    				{
    					i=processOnMultiwordParenthesis2(splitSpace,i,defaultOperator);
    				}
    				isCategoryFound=false;
    			}
    			//multiWord.trimToSize();
    			if(Character.isWhitespace(multiWord.charAt(multiWord.length() - 1)))
    				multiWord.deleteCharAt(multiWord.length() - 1);
    			multiWord.append("}");
    			finalQuery=multiWord.toString();
    		}
    		
    		System.out.println("Final String:"+finalQuery);
	    	return query;
        }
		
        else return null;
		
	}
	
	/**
	 * Method to parse a single word containing user Query
	 */
	public static String processOnSingleWord(String input)
	{
		input.trim();
		String[] splitSColon=input.split(":");
		input="";
		if(splitSColon.length==1) return input="{Term:"+splitSColon[0]+"}"; //--{Term:Hello}---//
		else if (splitSColon.length==2) return input="{"+splitSColon[0]+":"+splitSColon[1]+"}";//--{Author:rushdie}--//
		else return null;
	}
	
	public static int processOnMultiwordParenthesis(String[] splitSpace,String category,String str,int i)
	{
		if(str.contains("("))
		{
			stack.push('(');
			if(!isCategoryAngledNOTFound)
			multiWord.append("["+category+":"+str.substring(1)+" ");
			else multiWord.append("[<"+category+":"+str.substring(1)+" ");
			i=i+1;
			while(!splitSpace[i].contains(")"))
			{
				if(splitSpace[i].startsWith("("))
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
		else if(!str.contains("("))
		{
			if(!isCategoryAngledNOTFound)
				multiWord.append(category+":"+str+" ");
			else
				multiWord.append("<"+category+":"+str+" ");
		}
		isCategoryFound=true;
		return i;
	}
	
	public static int processOnMultiwordParenthesis2(String[] splitSpace,int i,String defaultOperator)
	{
	if(splitSpace[i].startsWith("("))
	{
		stack.push('(');
		if(!isTermAngledNOTFound)
			multiWord.append("["+"Term"+":"+splitSpace[i].substring(1)+" ");
		else
			multiWord.append("["+"<Term"+":"+splitSpace[i].substring(1)+" ");
		i=i+1;
		while(!splitSpace[i].contains(")"))
		{
			if(splitSpace[i].startsWith("("))
			{
				i=processOnMultiwordParenthesis2(splitSpace,i,defaultOperator);
				if(stack.isEmpty()) break;
			}
			else if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
			{
				multiWord.append(splitSpace[i]+" ");
			}
			else if(splitSpace[i].equals("NOT"))
			{
				multiWord.append("AND ");
				isTermAngledNOTFound=true;
			}
			else
			{
				if(splitSpace[i].startsWith("\""))
				{
					if(!isTermAngledNOTFound)
						multiWord.append("TERM:"+splitSpace[i]+" ");
					else
						multiWord.append("<TERM:"+splitSpace[i]+" ");
					i=i+1;
					while(!splitSpace[i].endsWith("\""))
					{
						multiWord.append(splitSpace[i]+" ");
						i=i+1;
					}
					if(splitSpace[i].endsWith("\"") && isTermAngledNOTFound)
						multiWord.append(splitSpace[i]+">"+" ");
					else if(!isTermAngledNOTFound) 
						multiWord.append(splitSpace[i]+" ");
					isTermAngledNOTFound=false;
				}
				else
				{
					if(isTermAngledNOTFound) multiWord.append("TERM:"+splitSpace[i]+">"+" ");
					else multiWord.append("TERM:"+splitSpace[i]+" ");
					isTermAngledNOTFound=false;
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
				if(isTermAngledNOTFound)
					multiWord.append("<TERM:"+splitSpace[i].substring(0, splitSpace[i].length()-countParenthesis)+">");
				else
					multiWord.append("TERM:"+splitSpace[i].substring(0, splitSpace[i].length()-countParenthesis));
				
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
				isTermAngledNOTFound=false;
			}
		}
	}					
	
	else if(!splitSpace[i].startsWith("("))
	{
		if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
		{
			isBooleanSet=true;
			String[] stringCol=splitSpace[i+1].split(":");
			if(Arrays.asList(Categories).contains(stringCol[0].trim())) multiWord.append(splitSpace[i].trim()+" ");
			else
			multiWord.append(splitSpace[i].trim()+" ");
		}
		
		else if(splitSpace[i].equals("NOT"))
		{
			isNotSet=true;
			isTermAngledNOTFound=true;
			multiWord.append("AND"+" ");
		}
		else if(splitSpace[i].startsWith("\""))
		{
			if(!isTermAngledNOTFound)
			multiWord.append("TERM:"+splitSpace[i]+" ");
			else
				multiWord.append("<TERM:"+splitSpace[i]+" ");
			i=i+1;
			while(!splitSpace[i].endsWith("\""))
			{
				multiWord.append(splitSpace[i]+" ");
				i=i+1;
			}
			if(splitSpace[i].endsWith("\"") && isTermAngledNOTFound)
				multiWord.append(splitSpace[i]+">"+" ");
			else if(!isTermAngledNOTFound) 
				multiWord.append(splitSpace[i]+" ");
			isTermAngledNOTFound=false;
		}
		else
		{
			if(isBooleanSet)
			{
				multiWord.append("TERM:"+splitSpace[i]+" ");
				isBooleanSet=false;
			}
			else if(isNotSet)
			{
				multiWord.append("<TERM:"+splitSpace[i]+">"+" ");
				isNotSet=false;
			}
			else if(i==0)
			{
				multiWord.append("Term:"+splitSpace[i]+" ");
			}
			else
			{
				multiWord.append(defaultOperator+" "+"Term:"+splitSpace[i]+" ");
			}
			isTermAngledNOTFound=false;
		}
	  }
	return i;
	}
}