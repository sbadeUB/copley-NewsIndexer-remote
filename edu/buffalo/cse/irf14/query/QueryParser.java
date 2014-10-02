/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.Arrays;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator)
	{
		String initUserQuery=userQuery;
		String finalQuery=null;
		boolean isQuotes=false;
		boolean isinParenthesis=false;
		boolean isCategoryAngledNOTFound=false;
		boolean isCategoryFound=false;
		String[] Categories={"Author","Category","Place"};
		String[] splitSpace=initUserQuery.split(" ");
		if(splitSpace.length==1)
		{
			finalQuery=processOnSingleWord(splitSpace[0]);
		}
		else
		{
			StringBuilder multiWord=new StringBuilder();
			multiWord.append('{');
			for(int i=0;i<splitSpace.length;i++)
			{
				String str=splitSpace[i];
				str.trim();
				String[] splitColon=str.split(":");
				if(Arrays.asList(Categories).contains(splitColon[0].trim()))
				{
						str=splitColon[1].trim();
						if(splitColon[1].contains("("))
						{
							multiWord.append("["+splitColon[0]+":");
							i=i+1;
							while(!splitSpace[i].contains(")"))
							{
								if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
								{
									multiWord.append(splitSpace[i]+" ");
								}
								else if(splitSpace[i].equals("NOT"))
								{
									multiWord.append("<"+splitColon[0]+":");
									isCategoryAngledNOTFound=true;
								}
								else
								{
									if(splitSpace[i].contains("\""))
									{
										multiWord.append(splitSpace[i]+" ");
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
										if(isCategoryAngledNOTFound) multiWord.append(splitSpace[i]+">"+" ");
										else multiWord.append(splitSpace[i]+" ");
										isCategoryAngledNOTFound=false;
									}
								}
								i=i+1;
							}
							if(splitSpace[i].contains(")")) multiWord.append(splitSpace[i]+"]");
						}					
						else if(!splitColon[1].contains("("))
						{
							multiWord.append(splitColon[0]+":"+splitColon[1]+" ");
						}
						isCategoryFound=true;
					 }
				
				else if(!isCategoryFound)
				{
					
					
				}
				
			}
		}
		
		return null;
	}
	
	/**
	 * Method to parse a single word containing user Query
	 */
	public static String processOnSingleWord(String input)
	{
		input.trim();
		String[] splitColon=input.split(":");
		input="";
		if(splitColon.length==1) return input="{Term:"+splitColon[0]+"}"; //--{Term:Hello}---//
		else if (splitColon.length==2) return input="{"+splitColon[0]+":"+splitColon[1]+"}";//--{Author:rushdie}--//
		else return null;
	}
}