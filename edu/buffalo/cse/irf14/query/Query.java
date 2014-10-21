package edu.buffalo.cse.irf14.query;

import java.util.Arrays;
import java.util.Stack;

/**
 * Class that represents a parsed query
 * @author Bade
 *
 */
public class Query 
{
	String initQueryString="";
	String defaultOperator="";
	String finalQuery="";
	String[] Categories={"Author","Category","Place"};
	StringBuilder multiWord=new StringBuilder();
	boolean isCategoryFound=false;
	boolean isCategoryAngledNOTFound=false;
	boolean isTermAngledNOTFound=false;
	boolean isNotSet=false;
	boolean isBooleanSet=false;
	static int countOpenParenthesis=0;
	int countClosingParenthesis=0;
	Stack<Character> stack = new Stack<Character>();
	boolean setCatSequence=false;
	boolean isEndReached=false;
	boolean isSet=false;
	//String ResultantUnSpacedQuery="";
	
	/**
	 * Constructor to invoke Query Object with given initQueryString and defaultOpearator
	 * (Generally call comes from QueryParser)
	 */
	public Query(String initQueryString,String defaultOpearator)
	{
		this.initQueryString=initQueryString;
		this.defaultOperator=defaultOpearator;
	}
	
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() 
	{
			String initUserQuery=this.initQueryString;
        	String[] splitSpace=initUserQuery.split(" ");
    		if(splitSpace.length==1)
    		{
    			finalQuery=processOnSingleWord(splitSpace[0]);
    		}
    		else
    		{
    			boolean isOverIndexed = false;
    			multiWord.append('{');
    			for(int i=0;i<splitSpace.length;i++)
    			{
    				if(isOverIndexed)
    				{
    					i=i-1;
    					isOverIndexed=false;
    				}
    				String str=splitSpace[i];
    				str.trim();
    				String[] splitColon=str.split(":");
    				if(Arrays.asList(Categories).contains(splitColon[0].trim()))
    				{
    					str=splitColon[1].trim();
    					i=processOnMultiwordParenthesis(splitSpace,splitColon[0].trim(),str,i);
    					while(setCatSequence)
    					{
    						i=i+1;
    						str=splitSpace[i].trim();
    						i=processOnMultiwordParenthesis(splitSpace,splitColon[0].trim(),str,i);
    					}
    					if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR") || splitSpace[i].equals("NOT"))
						{
							isOverIndexed=true;
						}
    				}
    				else if(!isCategoryFound)
    				{
    					
    					i=processOnMultiwordParenthesis2(splitSpace,i,defaultOperator);
    					if(i<splitSpace.length)
    					{
    						if(!isSet)
    						{
    							if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR") || splitSpace[i].equals("NOT"))
        						{
        							isOverIndexed=true;
        						}
    						}
    						else
    						{
    							isSet=false;
    						}
    					}
    				}
    				isCategoryFound=false;
    			}
    			if(Character.isWhitespace(multiWord.charAt(multiWord.length() - 1)))
    				multiWord.deleteCharAt(multiWord.length() - 1);
    			multiWord.append("}");
    			finalQuery=multiWord.toString();
    		}
    		finalQuery=addSpaces(finalQuery);
	    	return finalQuery;
	}
	
	/*public String getStringWithOutSpaces()
	{
		return this.ResultantUnSpacedQuery;
	}*/
	
	public String addSpaces(String finalQuery)
	{
		StringBuilder multiword2=new StringBuilder();
		String[] splitSpace=finalQuery.split(" ");
		boolean isSpaceSet=false;
		boolean isSpaceNotKept=false;
		boolean isEnd=false;
		for(int i=0;i<splitSpace.length;i++)
		{
			int ch=0;
			while(splitSpace[i].startsWith("{") || splitSpace[i].startsWith("["))
			{
				char c=splitSpace[i].charAt(0);
				if(isSpaceNotKept)
				{
					multiword2.append(" ");
					isSpaceNotKept=false;
				}
				multiword2.append(c+" ");
				splitSpace[i]=splitSpace[i].substring(1);
				isSpaceSet=true;
			}
			while(splitSpace[i].endsWith("}") || splitSpace[i].endsWith("]"))
			{
				char c=splitSpace[i].charAt(splitSpace[i].length()-1);
				if(c==']')	ch++;
				else isEnd=true;
				splitSpace[i]=splitSpace[i].substring(0,splitSpace[i].length()-1);
			}
			if(!isSpaceSet)
			multiword2.append(" "+splitSpace[i]);
			else
			{
				multiword2.append(splitSpace[i]);
				isSpaceNotKept=true;
			}
			for(int i1=1;i1<=ch;i1++)
				multiword2.append(" ]");
			if(isEnd)
			multiword2.append(" }");
			isSpaceSet=false;
		 }
		finalQuery=multiword2.toString();
		
		
		return finalQuery.trim();
	}
	
	/**
	 * Method to parse a single word containing initQueryString
	 */
	public String processOnSingleWord(String input)
	{
		input.trim();
		String[] splitSColon=input.split(":");
		input="";
		if(splitSColon.length==1) return input="{Term:"+splitSColon[0]+"}"; //--{Term:Hello}---//
		else if (splitSColon.length==2) return input="{"+splitSColon[0]+":"+splitSColon[1]+"}";//--{Author:rushdie}--//
		else return null;
	}
	
	/**
	 * Method to parse a multiple word containing initQueryString belonging to any of the listed Categories
	 */
	public int processOnMultiwordParenthesis(String[] splitSpace,String category,String str,int i)
	{
		
		if(str.contains("("))
		{
			while(str.startsWith("("))
			{
				stack.push('(');
				countOpenParenthesis=countOpenParenthesis+1;
				str=str.substring(1,str.length());
				multiWord.append("[");
			}
			
			if(str.contains("\""))
			{
				if(!isCategoryAngledNOTFound)
					multiWord.append(category+":"+str+" ");
				else
					multiWord.append("<"+category+":"+str+" ");
				i=i+1;
				str=splitSpace[i];
				while(!(str.endsWith("\"") || str.endsWith(")")))
				{
					multiWord.append(splitSpace[i]+" ");
					i=i+1;
					str=splitSpace[i];
				}
				int countClosepar=0;
				while((str.endsWith(")")||str.endsWith("\"")))
				{
					char x=str.charAt(str.length()-1);
					str=splitSpace[i].substring(0, str.length()-1);
					if(x=='\"' && isCategoryAngledNOTFound)
						multiWord.append(str+"\""+">");
					else if(x=='\"' && !isCategoryAngledNOTFound) 
						multiWord.append(str+"\"");
					else 
					{
						countOpenParenthesis=countOpenParenthesis-1;
						countClosepar=countClosepar+1;
						stack.pop();								
					}
				 }
				int i1=1;
				for(;i1<=countClosepar-1;i1++)
				{
					multiWord.append("]");
				}
				if(i1==countClosepar)
				multiWord.append("]");
				multiWord.append(" ");
				if(i==splitSpace.length-1) 
				{
					isEndReached=true;
				}
				isCategoryAngledNOTFound=false;
			}
			else
			{
				if(!isCategoryAngledNOTFound)
					multiWord.append(category+":"+str+" ");
				else
					multiWord.append("<"+category+":"+str+" ");
			}
			if(!isEndReached)
			{
				i=i+1;
				while((!splitSpace[i].contains(")")) && (!stack.isEmpty()))
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
							if(!isCategoryAngledNOTFound)
								multiWord.append(category+":"+splitSpace[i]+" ");
							else
								multiWord.append("<"+category+":"+splitSpace[i]+" ");
							i=i+1;
							while(!(splitSpace[i].endsWith("\"") || splitSpace[i].endsWith(")")))
							{
								multiWord.append(splitSpace[i]+" ");
								i=i+1;
							}
							int countClosepar=0;
							while((splitSpace[i].endsWith(")")||splitSpace[i].endsWith("\"")))
							{
								char x=splitSpace[i].charAt(splitSpace[i].length()-1);
								splitSpace[i]=splitSpace[i].substring(0, splitSpace[i].length()-1);
								if(x=='\"' && isCategoryAngledNOTFound)
									multiWord.append(splitSpace[i]+"\""+">");
								else if(x=='\"' && !isCategoryAngledNOTFound) 
									multiWord.append(splitSpace[i]+"\"");
								else 
								{
									countOpenParenthesis=countOpenParenthesis-1;
									countClosepar=countClosepar+1;
									stack.pop();								
								}
							 }
							int i1=1;
							for(;i1<=countClosepar-1;i1++)
							{
								multiWord.append("]");
							}
							if(i1==countClosepar)
							multiWord.append("]");
							multiWord.append(" ");
							if(i==splitSpace.length-1) 
							{
								isEndReached=true;
								break;
							}
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
			}
			if(!stack.isEmpty())
			{
				if(splitSpace[i].equals(")") )
				{
					countOpenParenthesis=countOpenParenthesis-1;
					countClosingParenthesis=countClosingParenthesis+1;
					stack.pop();
					if(Character.isWhitespace(multiWord.charAt(multiWord.length() - 1)))
					multiWord.deleteCharAt(multiWord.length() - 1);
					multiWord.append("] ");
					countClosingParenthesis=countClosingParenthesis-1;
				}
				else if(splitSpace[i].contains(")"))
				{
					char[] array=splitSpace[i].toCharArray();
					for(int i1=0;i1<array.length;i1++)
					{
						if(array[i1]==')')
						{
							countClosingParenthesis=countClosingParenthesis+1;
							countOpenParenthesis=countOpenParenthesis-1;
						}
					}
					if(isCategoryAngledNOTFound)
						multiWord.append("<"+category+":"+splitSpace[i].substring(0, splitSpace[i].length()-countClosingParenthesis)+">");
					else
						multiWord.append(category+":"+splitSpace[i].substring(0, splitSpace[i].length()-countClosingParenthesis));
					
					if(countClosingParenthesis==1)
					{
						multiWord.append("] ");
						stack.pop();
						countClosingParenthesis=countClosingParenthesis-1;
					}
					else
					{
						for(int i1=1;i1<=countClosingParenthesis-1;i1++)
						{
							multiWord.append("]");
							stack.pop();
						}
						multiWord.append("] ");
						stack.pop();
					}
					countClosingParenthesis=0;
					isCategoryAngledNOTFound=false;
				}
			}
			else if(!splitSpace[i].endsWith(")") && (!stack.isEmpty()))
			{
				i=i-1;
			}
		}
		else if(!str.contains("("))
		{
			if(str.startsWith("\""))
			{
				if(!isCategoryAngledNOTFound)
				multiWord.append(category+":"+str+" ");
				else
					multiWord.append("<"+category+":"+str+" ");
				i=i+1;
				while(!(splitSpace[i].endsWith("\"") || splitSpace[i].endsWith(")")))
				{
					multiWord.append(splitSpace[i]+" ");
					i=i+1;
				}
				int countClosepar=0;
				while((splitSpace[i].endsWith(")")||splitSpace[i].endsWith("\"")))
				{
					char x=splitSpace[i].charAt(splitSpace[i].length()-1);
					splitSpace[i]=splitSpace[i].substring(0, splitSpace[i].length()-1);
					if(x=='\"' && isCategoryAngledNOTFound)
						multiWord.append(splitSpace[i]+"\""+">");
					else if(x=='\"' && !isCategoryAngledNOTFound) 
						multiWord.append(splitSpace[i]+"\"");
					else 
					{
						countOpenParenthesis=countOpenParenthesis-1;
						countClosepar=countClosepar+1;
						stack.pop();								
					}
				 }
				int i1=1;
				for(;i1<=countClosepar-1;i1++)
				{
					multiWord.append("]");
				}
				if(i1==countClosepar)
				multiWord.append("]");
				multiWord.append(" ");
				isCategoryAngledNOTFound=false;
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
				int indcloses=0;
				while(str.endsWith(")"))
				{	
					indcloses=indcloses+1;
					str=str.substring(0,str.length()-1);
				}
				if(!isCategoryAngledNOTFound)
					multiWord.append(category+":"+str);
				else
					multiWord.append("<"+category+":"+str);
				for(int cl=1;cl<=indcloses;cl++)
					multiWord.append("]");
				multiWord.append(" ");
				isCategoryAngledNOTFound=false;
			}
		}
		if(countOpenParenthesis>0) setCatSequence=true;
		else setCatSequence=false;
		isCategoryFound=true;
		return i;
	}
	
	/**
	 * Method to parse a multiple word containing initQueryString belonging to Term Category
	 */
	public int processOnMultiwordParenthesis2(String[] splitSpace,int i,String defaultOperator)
	{
	if(splitSpace[i].startsWith("("))
	{
		while(splitSpace[i].startsWith("("))
		{
			stack.push('(');
			splitSpace[i]=splitSpace[i].substring(1,splitSpace[i].length());
			multiWord.append("[");
		}
		
		if(splitSpace[i].startsWith("\""))
		{
			if(!isTermAngledNOTFound)
				multiWord.append("TERM:"+splitSpace[i]+" ");
			else
				multiWord.append("<TERM:"+splitSpace[i]+" ");
			i=i+1;
			while(!(splitSpace[i].endsWith("\"") || splitSpace[i].endsWith(")")))
			{
				multiWord.append(splitSpace[i]+" ");
				i=i+1;
			}
			int countClosepar=0;
			while((splitSpace[i].endsWith(")")||splitSpace[i].endsWith("\"")))
			{
				char x=splitSpace[i].charAt(splitSpace[i].length()-1);
				splitSpace[i]=splitSpace[i].substring(0, splitSpace[i].length()-1);
				if(x=='\"' && isTermAngledNOTFound)
					multiWord.append(splitSpace[i]+"\""+">");
				else if(x=='\"' && !isTermAngledNOTFound) 
					multiWord.append(splitSpace[i]+"\"");
				else 
				{
					countClosepar=countClosepar+1;
					stack.pop();								
				}
			 }
			int i1=1;
			for(;i1<=countClosepar-1;i1++)
			{
				multiWord.append("]");
			}
			if(i1==countClosepar)
			multiWord.append("]");
			multiWord.append(" ");
			isTermAngledNOTFound=false;
			if(i==splitSpace.length-1) 
			{
				isEndReached=true;
			}
		}
		else
		{
			
			if(!isTermAngledNOTFound)
				multiWord.append("Term"+":"+splitSpace[i]+" ");
			else
				multiWord.append("<Term"+":"+splitSpace[i]+" ");
		}
		if(!isEndReached)
		{
			i=i+1;
			while(!splitSpace[i].contains(")") && (!stack.isEmpty()))
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
						while(!(splitSpace[i].endsWith("\"") || splitSpace[i].endsWith(")")))
						{
							multiWord.append(splitSpace[i]+" ");
							i=i+1;
						}
						int countClosepar=0;
						while((splitSpace[i].endsWith(")")||splitSpace[i].endsWith("\"")))
						{
							char x=splitSpace[i].charAt(splitSpace[i].length()-1);
							splitSpace[i]=splitSpace[i].substring(0, splitSpace[i].length()-1);
							if(x=='\"' && isTermAngledNOTFound)
								multiWord.append(splitSpace[i]+"\""+">");
							else if(x=='\"' && !isTermAngledNOTFound) 
								multiWord.append(splitSpace[i]+"\"");
							else 
							{
								countClosepar=countClosepar+1;
								stack.pop();								
							}
						 }
						int i1=1;
						for(;i1<=countClosepar-1;i1++)
						{
							multiWord.append("]");
						}
						if(i1==countClosepar)
						multiWord.append("]");
						multiWord.append(" ");
						if(i==splitSpace.length-1) 
						{
							isEndReached=true;
							break;
						}
						
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
		}
		if(!stack.isEmpty())
		{
			if(splitSpace[i].equals(")") )
			{
				countClosingParenthesis=countClosingParenthesis+1;
				stack.pop();
				if(Character.isWhitespace(multiWord.charAt(multiWord.length() - 1)))
				multiWord.deleteCharAt(multiWord.length() - 1);
				multiWord.append("] ");
				countClosingParenthesis=countClosingParenthesis-1;
			}
			else if(splitSpace[i].contains(")"))
			{
				char[] array=splitSpace[i].toCharArray();
				for(int i1=0;i1<array.length;i1++)
				{
					if(array[i1]==')')
					{
						countClosingParenthesis=countClosingParenthesis+1;
					}
				}
				if(isTermAngledNOTFound)
					multiWord.append("<TERM:"+splitSpace[i].substring(0, splitSpace[i].length()-countClosingParenthesis)+">");
				else
					multiWord.append("TERM:"+splitSpace[i].substring(0, splitSpace[i].length()-countClosingParenthesis));
				
				if(countClosingParenthesis==1)
				{
					multiWord.append("] ");
					stack.pop();
					countClosingParenthesis=countClosingParenthesis-1;
				}
				else
				{
					for(int i1=1;i1<=countClosingParenthesis-1;i1++)
					{
						multiWord.append("]");
						stack.pop();
					}
					multiWord.append("] ");
					stack.pop();
					countClosingParenthesis=0;
				}
				isTermAngledNOTFound=false;
			}
		}
		else if(!splitSpace[i].endsWith(")") && (!stack.isEmpty()))
		{
			i=i-1;
		}
	}					
	
	else if(!splitSpace[i].startsWith("("))
	{
		if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
		{
			isSet=true;
			isBooleanSet=true;
			multiWord.append(splitSpace[i].trim()+" ");
		}
		
		else if(splitSpace[i].equals("NOT"))
		{
			isSet=true;
			isNotSet=true;
			isTermAngledNOTFound=true;
			multiWord.append("AND"+" ");
		}
		else if(splitSpace[i].startsWith("\""))
		{
			if(!isBooleanSet && !isNotSet && i!=0)
				multiWord.append(defaultOperator+" ");
			if(!isTermAngledNOTFound)
			multiWord.append("TERM:"+splitSpace[i]+" ");
			else
				multiWord.append("<TERM:"+splitSpace[i]+" ");
			i=i+1;
			while(!(splitSpace[i].endsWith("\"") || splitSpace[i].endsWith(")")))
			{
				multiWord.append(splitSpace[i]+" ");
				i=i+1;
			}
			int countClosepar=0;
			while((splitSpace[i].endsWith(")")||splitSpace[i].endsWith("\"")))
			{
				char x=splitSpace[i].charAt(splitSpace[i].length()-1);
				splitSpace[i]=splitSpace[i].substring(0, splitSpace[i].length()-1);
				if(x=='\"' && isTermAngledNOTFound)
					multiWord.append(splitSpace[i]+"\""+">");
				else if(x=='\"' && !isTermAngledNOTFound) 
					multiWord.append(splitSpace[i]+"\"");
				else 
				{
					countClosepar=countClosepar+1;
					stack.pop();								
				}
			 }
			int i1=1;
			for(;i1<=countClosepar-1;i1++)
			{
				multiWord.append("]");
			}
			if(i1==countClosepar)
			multiWord.append("]");
			multiWord.append(" ");
			isTermAngledNOTFound=false;
			isBooleanSet=false;
			isNotSet=false;
		}
		else
		{
			int indcloses=0;
			while(splitSpace[i].endsWith(")"))
			{
				indcloses=indcloses+1;
				countClosingParenthesis=countClosingParenthesis-1;
				splitSpace[i]=splitSpace[i].substring(0,splitSpace[i].length()-1);
			}
			if(isBooleanSet)
			{
				multiWord.append("TERM:"+splitSpace[i]);
				isBooleanSet=false;
			}
			else if(isNotSet)
			{
				multiWord.append("<TERM:"+splitSpace[i]+">");
				isNotSet=false;
			}
			else if(i==0)
			{
				multiWord.append("Term:"+splitSpace[i]);
			}
			else
			{
				multiWord.append(defaultOperator+" "+"Term:"+splitSpace[i]);
			}
			for(int cl=1;cl<=indcloses;cl++)
				multiWord.append("]");
			multiWord.append(" ");
			isTermAngledNOTFound=false;
		}
	  }
	return i;
	}
}