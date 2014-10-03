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
	public static void parse(String userQuery, String defaultOperator)
	{
		String initUserQuery=userQuery;
		String finalQuery=null;
		boolean isNotSet=false;
		boolean isBooleanSet=false;
		boolean isCategoryAngledNOTFound=false;
		boolean isTermAngledNOTFound=false;
		boolean isCategoryFound=false;
		StringBuilder multiWord=new StringBuilder();
		String[] Categories={"Author","Category","Place"};
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
						if(splitColon[1].contains("("))
						{
							multiWord.append("["+splitColon[0]+":"+splitColon[1].substring(1)+" ");
							i=i+1;
							while(!splitSpace[i].contains(")"))
							{
								if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
								{
									multiWord.append(splitSpace[i]+" "+splitColon[0]+":");
								}
								else if(splitSpace[i].equals("NOT"))
								{
									multiWord.append("AND"+" <"+splitColon[0]+":");
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
							if(splitSpace[i].contains(")") && isCategoryAngledNOTFound) multiWord.append(splitSpace[i].substring(0, splitSpace[i].length()-1)+">] ");
							else if(splitSpace[i].contains(")") && !isCategoryAngledNOTFound) multiWord.append(splitSpace[i].substring(0, splitSpace[i].length()-1)+"] ");
						}					
						else if(!splitColon[1].contains("("))
						{
							multiWord.append(splitColon[0]+":"+splitColon[1]+" ");
						}
						isCategoryFound=true;
				}
				
				else if(!isCategoryFound)
				{
					
					str=splitSpace[i].trim();
					if(splitSpace[i].startsWith("("))
					{
						multiWord.append("["+"Term"+":"+splitSpace[i].substring(1)+" ");
						i=i+1;
						while(!splitSpace[i].endsWith(")"))
						{
							if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
							{
								multiWord.append(splitSpace[i]+" "+"TERM:");
							}
							else if(splitSpace[i].equals("NOT"))
							{
								multiWord.append("AND <"+"TERM:");
								isTermAngledNOTFound=true;
							}
							else
							{
								if(splitSpace[i].startsWith("\""))
								{
									multiWord.append(splitSpace[i]+" ");
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
									if(isTermAngledNOTFound) multiWord.append(splitSpace[i]+">"+" ");
									else multiWord.append(splitSpace[i]+" ");
									isTermAngledNOTFound=false;
								}
							}
							i=i+1;
						}
						if(splitSpace[i].endsWith(")") && isTermAngledNOTFound) multiWord.append(splitSpace[i].substring(0, splitSpace[i].length()-1)+">] ");
						else if(splitSpace[i].endsWith(")") && !isTermAngledNOTFound) multiWord.append(splitSpace[i].substring(0, splitSpace[i].length()-1)+"] ");
					}					
					
					else if(!splitSpace[i].startsWith("("))
					{
						if(splitSpace[i].equals("AND") || splitSpace[i].equals("OR"))
						{
							isBooleanSet=true;
							String[] stringCol=splitSpace[i+1].split(":");
							if(Arrays.asList(Categories).contains(stringCol[0].trim())) multiWord.append(splitSpace[i].trim()+" ");
							else
							multiWord.append(splitSpace[i].trim()+" Term:");
						}
						
						else if(splitSpace[i].equals("NOT"))
						{
							isNotSet=true;
							isTermAngledNOTFound=true;
							multiWord.append("AND"+" <Term:");
						}
						else if(splitSpace[i].startsWith("\""))
						{
							multiWord.append("TERM:"+splitSpace[i]+" ");
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
								multiWord.append(splitSpace[i]+" ");
								isBooleanSet=false;
							}
							else if(isNotSet)
							{
								multiWord.append(splitSpace[i]+">"+" ");
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
				}
				isCategoryFound=false;
			}
			//multiWord.trimToSize();
			multiWord.append("}");
			finalQuery=multiWord.toString();
		}
		
		System.out.println("Final String:"+finalQuery);
		
		//return null;
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