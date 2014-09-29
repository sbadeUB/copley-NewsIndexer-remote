/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public  class TokenFilterDates extends TokenFilter implements Analyzer
{
	public TokenFilterDates(TokenStream tokenStream)
	{
		super(tokenStream);
	}
@SuppressWarnings("unused")
public TokenFilter datesProcessing(TokenStream ts)
{
	TokenFilter tfs=null;
	String[] strArray=null;
	
	boolean setWeekday=false;
	boolean setHours=false;
	boolean setMinutes=false;
	boolean setSeconds=false;
	boolean setDay=false;
	boolean setMonth=false;
	boolean setYear=false;
	boolean dateEncounter=true;
	String weekday = "";  
	int hrs=-1;
	int mnts=-1;
	int secs=-1;
	int day=-1;
	int month=-1;
	int year=-1;
	int year2=-1;
	boolean setAssigned=false;
	
		String strOrig=ts.getCurrent().getTermText(); //---String str declaration already here so removed one that came after--//
		String[] testarray={}; //Dummy testarray
		String str=""; 
		str=RemoveDotSuffix(strOrig);
		str = str.replaceAll("[,]+$", "");
		for(int i=1;i<=1;i++)
		{
			if(str.matches("(?i)(?:sunday|monday|tuesday|wednesday|thursday|friday|saturday)"))
			{
				setWeekday=true;
				weekday=str.toLowerCase();
			  if(ts.hasNext())
			  {
				   //------------------------------------------------//
				  //----Searching for Month-Date-Year Sequence------//
				  //------------------------------------------------//
				String str2=ts.getNextTokenValue();
				str2=TokenCleaning(str2);
				String[] MasterArray=checkIfMonth(str2);
				if(MasterArray[0]=="1")
				{
					ts.remove();
					TokenFilter.IsTokenRemoved=true;
					setMonth=true;
					
					month=Integer.parseInt(MasterArray[1]);
					ts.next();
					
					//------Continuation requires-------//
					if(ts.hasNext())
					{
						
						TokenFilter.IsTokenRemoved=true;
						MasterArray=decomposeMasterArray(MasterArray);
						str2=ts.getNextTokenValue();
						str2=TokenCleaning(str2);
						//----- Checking for Date or Year<=31 with BC or AD-------------//
						MasterArray=checkIfDateOrYear(str2,ts);
						if(MasterArray[0]=="1")
						{
							ts.remove();
							setDay=true;
							day=Integer.parseInt(MasterArray[1]);
							
							ts.next();
							//break;
						}
						else if(MasterArray[0]=="2")
						{
							ts.remove();
							setYear=true;
							year=Integer.parseInt(MasterArray[1]);
							//Do if Continuation requires
							setDay=true;
							day=01;
							ts.next();
							break; //--look possibility of error--//
						}
						else
						{
							if(setDay==false)
							{
								setDay=true;
								day=01;
							}
							break;
						}
						
					}
					if(ts.hasNext() && setDay==true)
					{
						str2=ts.getNextTokenValue();
						str2=TokenCleaning(str2);
						MasterArray=decomposeMasterArray(MasterArray);
						MasterArray=checkIfYear(str2,ts);
						//------------ Checking for Year even with token containing year+BC/AD -------------//
						if(MasterArray[0]=="1")
						{
							ts.remove();
							setYear=true;
							year=Integer.parseInt(MasterArray[1]);
							
							ts.next();
							break;
							
						}
						else
						{
							setYear=true;
							year=1900;
							break;
						}
					}
					
						if(setDay==false) 
						{
							setDay=true;
							day=01;
						}
						if(setYear==false)
						{
							setYear=true;
							year=1900;
						}
						break;
					
				}
				
					//----------------------------------------------//
				   //---Searching for Date-Month-Year Sequence------//
				  //------------------------------------------------//
				else if(MasterArray[0]!="1")
				{
					MasterArray=decomposeMasterArray(MasterArray);
					MasterArray=checkIfDateOrYear(str2,ts);
					//----- Checking for Date-----//
					if(MasterArray[0]=="1")
					{
						ts.remove();
						IsTokenRemoved=true;
						setDay=true;
						day=Integer.parseInt(MasterArray[1]);
						//--Continuing for Month------//
						ts.next();
						MasterArray=decomposeMasterArray(MasterArray);
						if(ts.hasNext())
						{
							//Date encountered so deleted 							
							str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							MasterArray=checkIfMonth(str2);
							if(MasterArray[0]=="1")
							{
								ts.remove();//Date removed
								IsTokenRemoved=true;
								setMonth=true;
								month=Integer.parseInt(MasterArray[1]);
								ts.next();
							}
							else
							{
								setMonth=true;
								month=00;
								setYear=true;
								year=1900;
								break;
							}
						}
						//--Continuing for Year----//
						if(ts.hasNext() && setMonth==true)
						{
							MasterArray=decomposeMasterArray(MasterArray);
							str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							MasterArray=checkIfYear(str2,ts);
							//------------ Checking for Year even with token containing year+BC/AD -------------//
							if(MasterArray[0]=="1")
							{
								ts.remove();//Month removed
								IsTokenRemoved=true;
								setYear=true;
								year=Integer.parseInt(MasterArray[1]);
								ts.next();
								IsTokenRemoved=true;
								
							}
							else
							{
								setYear=true;
								year=1900;//Default set as No Year is Found
								break;
							}
						}
						if(setMonth==false)
						{
							month=00;
							setMonth=true;
						}
						if(setYear==false)
						{
							setYear=true;
							year=1900;
						}
						break;
						
					}
					
				  }
				
				
			  }
					
			}
			
			
			if(str.matches(".*\\d.*"))
			{
					str = str.replaceAll("[st|nd|rd|th|fth|,]+$", ""); //To remove st,nd,rd,th
					str=AddPrefixZero(str);
					if(str.matches("^(1[012]|(0)?[1-9]):[0-5][0-9]:[0-5][0-9](?i)(?:AM|PM)$") || str.matches("^(1[012]|(0)?[1-9]):[0-5][0-9](?i)(?:AM|PM)?$") || str.matches("^(1[012]|(0)?[1-9])(?i)(?:AM|PM)?$"))
					{
						str=str.toUpperCase();
						if(!str.matches("(AM|PM)$") && ts.hasNext())
						{
							String str2=ts.getNextTokenValue();   //--For the sake of checking if next token has AM/PM in it--//
							str2=RemoveDotSuffix(str2);
							if(str2.equalsIgnoreCase("AM")||str2.equalsIgnoreCase("PM"))
							{
								setHours=true;
								String[] strarr=str.split("\\:");
								if(strarr[0].matches("^[0-9]$"))
								{
									int xi=strarr.length;
									strarr[0]="0"+strarr[0];
									str="";
									
									for(int j=0;j<(xi-1);j++)
									{
										str=str+strarr[j]+":";
									}
									str=str+strarr[xi-1];
								}
								hrs=Integer.parseInt(str.substring(0, 2));
								if(str2.equalsIgnoreCase("PM"))
								{ 
									if(hrs<12)
										hrs=hrs+12;
								}
								if(str2.equalsIgnoreCase("AM") && str.substring(0,2).equals("12"))
								{
									hrs=hrs-12;
								}
								setMinutes=true;
								mnts=00;
								setSeconds=true;
								secs=00;
								if(str.length()>4)
								{
									if(str.charAt(2)==':')
									{
										mnts=Integer.parseInt(str.substring(3, 5));
									}
								}
							
								if(str.length()>7)
								{
									if(str.charAt(5)==':')
									{
										secs=Integer.parseInt(str.substring(6, 8));
									}
								 }
								//--Added code to append 0s--//
								str=hrs+":"+mnts+":"+secs;
								String[] strarr1=str.split("\\:");
								int xi=strarr1.length;
								str="";
								for( int k=0;k<xi;k++)
								{
									if(strarr1[k].matches("^[0-9]$"))
									{
										strarr1[k]="0"+strarr1[k];
									}
									if(k<(xi-1))
									str=str+strarr1[k]+":";
									else str=str+strarr1[xi-1];
								}
								//---------------------------------//
								strOrig=str;
								strOrig=strOrig.trim();
								if(!strOrig.isEmpty())
								{
									ts.getCurrent().setTermText(strOrig);
									ts.getCurrent().setTermBuffer(strOrig.toCharArray());
								}
								else
								{
									ts.remove();
									TokenFilter.IsTokenRemoved=true;
								}
								ts.next();//To forward really to next token
								ts.remove();//Removed the next token
								setAssigned=true;
								break;
							}
							else
							{
								//No Time quantity
							}
					     }
						
						else if(str.contains("AM") || str.contains("PM"))
						{
							setHours=true;
							String[] strarr=str.split("\\:");
							if(strarr[0].matches("^[1-9]$"))
							{
								int xi=strarr.length;
								strarr[0]="0"+strarr[0];
								str="";
								
								for(int j=0;j<(xi-1);j++)
								{
									str=str+strarr[j]+":";
								}
								str=str+strarr[xi-1];
							}
							
							hrs=Integer.parseInt(str.substring(0, 2));
							if(str.contains("PM"))
							{ 
								if(hrs<12)
									hrs=hrs+12;
							}
							if(str.contains("AM") && str.substring(0,2).equals("12"))
							{
								hrs=hrs-12;
							}
							str=str.replaceAll("AM|PM","");
							
							setMinutes=true;
							mnts=00;
							setSeconds=true;
							secs=00;
							if(str.length()>4)
							{
								if(str.charAt(2)==':')
								{
									mnts=Integer.parseInt(str.substring(3, 5));
								}
							}
						
							if(str.length()>7)
							{
								if(str.charAt(5)==':')
								{
									secs=Integer.parseInt(str.substring(6, 8));
								}
							 }
							
						 }
						
						else if(str.length()>4 && str.charAt(2)==':')
						{
							
							setHours=true;
							hrs=Integer.parseInt(str.substring(0, 2));
							setMinutes=true;
							mnts=00;
							setSeconds=true;
							secs=00;
							if(str.length()>4)
							{
								if(str.charAt(2)==':')
								{
									mnts=Integer.parseInt(str.substring(3, 5));
								}
							}
						
							if(str.length()>7)
							{
								if(str.charAt(5)==':')
								{
									secs=Integer.parseInt(str.substring(6, 8));
								}
							 }
						}
						if(setHours==true)
						{
							str=hrs+":"+mnts+":"+secs;
							//--Added code to append 0s--//
							String[] strarr1=str.split("\\:");
							int xi=strarr1.length;
							str="";
							for( int k=0;k<xi;k++)
							{
								if(strarr1[k].matches("^[0-9]$"))
								{
									strarr1[k]="0"+strarr1[k];
								}
								if(k<(xi-1))
								str=str+strarr1[k]+":";
								else str=str+strarr1[xi-1];
							}
							//---------------------------------//
							strOrig=str;
							setAssigned=true;
							break;
						}
					}
					
					else if(str.matches("^(2[0-3]|1[0-9]|0[0-9]):[0-5][0-9]:[0-5][0-9]([A-Za-z]*)$") || str.matches("^(2[0-3]|1[0-9]|0[0-9]):[0-5][0-9]([A-Za-z]*)$") ) 
					{
						//--This is 24-Hour Time Loop which even neglects Time Zone--//
						setHours=true;
						hrs=Integer.parseInt(str.substring(0, 2));
						setMinutes=true;
						mnts=00;
						setSeconds=true;
						secs=00;
						if(str.length()>4)
						{
							if(str.charAt(2)==':')
							{
								mnts=Integer.parseInt(str.substring(3, 5));
							}
						}
					
						if(str.length()>7)
						{
							if(str.charAt(5)==':')
							{
								secs=Integer.parseInt(str.substring(6, 8));
							}
						 }
						//--Added code to append 0s--//
						str=hrs+":"+mnts+":"+secs;
						String[] strarr1=str.split("\\:");
						int xi=strarr1.length;
						str="";
						for( int k=0;k<xi;k++)
						{
							if(strarr1[k].matches("^[0-9]$"))
							{
								strarr1[k]="0"+strarr1[k];
							}
							if(k<(xi-1))
							str=str+strarr1[k]+":";
							else {
								strarr1[xi-1]=strarr1[xi-1].substring(0,2);
								str=str+strarr1[xi-1];
							}
						}
						//---------------------------------//
						strOrig=str;
						setAssigned=true;
						break;
					}
					//Doing and Checking If Date Constituents are matching
					String[] MasterArray= checkIfMMDDYY(str);
					//-----Checking for Date with mm/dd/yyyy or yy or mm-dd-yyyy or yy or mm.dd.yyyy or yy format-----------//
					if(MasterArray[0]=="1")
					{
						setMonth=true;
						setDay=true;
						setYear=true;
						month=Integer.parseInt(MasterArray[1])-1;
						day=Integer.parseInt(MasterArray[2]);
						year=Integer.parseInt(MasterArray[3]);
						month=month+1;
						strOrig=formDate(year,month,day);
						setAssigned=true;
						break;
						
					}
					
					MasterArray=decomposeMasterArray(MasterArray);
					MasterArray=checkIfDDMMYY(str);
					//-------Checking for Date with dd/mm/yyyy or yy or dd-mm-yyyy or yy or dd.mm.yyyy or yy format ------------//
					if(MasterArray[0]=="1")
					{
						setDay=true;
						setMonth=true;
						setYear=true;
						day=Integer.parseInt(MasterArray[1]);
						month=Integer.parseInt(MasterArray[2])-1;
						year=Integer.parseInt(MasterArray[3]);
						month=month+1;
						strOrig=formDate(year,month,day);
						setAssigned=true;
						break;
						
					}
					
					MasterArray=decomposeMasterArray(MasterArray);
					MasterArray=checkIfYearsRange(str);
					//---------Checking for Years with range(4-digit)type------------//
					if(MasterArray[0]=="1")
					{
						setYear=true;
						year=Integer.parseInt(MasterArray[1]);
						year2=Integer.parseInt(MasterArray[2]);
						str=year+"01"+"01-"+year2+"01"+"01";
						strOrig=str;
						setAssigned=true;
						break;
						
					}
					
					MasterArray=decomposeMasterArray(MasterArray);
					//----- Checking for Date or Year<=31 with BC or AD-------------//
					MasterArray=checkIfDateOrYear(str,ts);
					//----- Checking for Date-----//
					if(MasterArray[0]=="1")
					{
						setDay=true;
						day=Integer.parseInt(MasterArray[1]);
						//--Continuing for Month------//
						MasterArray=decomposeMasterArray(MasterArray);
						if(ts.hasNext())
						{
							String str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							MasterArray=checkIfMonth(str2);
							if(MasterArray[0]=="1")
							{
								ts.remove();
								IsTokenRemoved=true;
								setMonth=true;
								month=Integer.parseInt(MasterArray[1]);
								ts.next();
							}
							else
							{
								//Do nothing
							}
						}
						//--Continuing for Year----//
						if(ts.hasNext() && setMonth==true)
						{
							MasterArray=decomposeMasterArray(MasterArray);
							String str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							MasterArray=checkIfYear(str2,ts);
							//------------ Checking for Year even with token containing year+BC/AD -------------//
							if(MasterArray[0]=="1")
							{
								ts.remove();
								IsTokenRemoved=true;
								setYear=true;
								year=Integer.parseInt(MasterArray[1]);
								ts.next();
								break;
								
							}
							else
							{
								setYear=true;
								year=1900;//Default set as No Year is Found
								break;
							}
						}
						 if(setYear==false)
						{
							setYear=true;
							year=1900;//Default set as No Year is Found
							break;
						}
						
							
						
					}
					//----- Checking for  Year<=31 with BC or AD------//
					else if(MasterArray[0]=="2")
					{
						setYear=true;
						year=Integer.parseInt(MasterArray[1]);
						//Do if Continuation requires
						setDay=true;
						day=01;
						setMonth=true;
						month=00;
						break;
						//--Chance is der to continue---//
						
					}

					
					MasterArray=decomposeMasterArray(MasterArray);
					str=TokenCleaning(str);
					MasterArray=checkIfYear(str,ts);
					//------------ Checking for Year even with token containing year+BC/AD -------------//
					if(MasterArray[0]=="1")
					{
						setYear=true;
						year=Integer.parseInt(MasterArray[1]);
						if(ts.hasNext())
						{
							
							MasterArray=decomposeMasterArray(MasterArray);
							//------Continuation for Month-------//
							String str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							MasterArray=checkIfMonth(str2);
							if(MasterArray[0]=="1")
							{
								ts.remove();
								setMonth=true;
								month=Integer.parseInt(MasterArray[1]);
								ts.next();
							
								//------Continuation for Day-------//
								if(ts.hasNext())
								{
									MasterArray=decomposeMasterArray(MasterArray);
									str2=ts.getNextTokenValue();
									str2=TokenCleaning(str2);
									//----- Checking for Date or Year<=31 with BC or AD-------------//
									MasterArray=checkIfDateOrYear(str2,ts);
									if(MasterArray[0]=="1")
									{
										ts.remove();
										setDay=true;
										day=Integer.parseInt(MasterArray[1]);
										ts.next();
										break;
									}
									else
									{
										setDay=true;
										day=01;
										break;
									}
								}
								else
								{
									setDay=true;
									day=01;
									break;
								}
							 }
							
							else if(MasterArray[0]!="1")
							{
								MasterArray=decomposeMasterArray(MasterArray);
								str2=ts.getNextTokenValue();
								str2=TokenCleaning(str2);
								MasterArray=checkIfDateOrYear(str2,ts);
								if(MasterArray[0]=="1")
								{
									setDay=true;
									day=Integer.parseInt(MasterArray[1]);
									ts.next();
									
									if(ts.hasNext())
									{
										MasterArray=decomposeMasterArray(MasterArray);
										//------Continuation for Month-------//
										str2=ts.getNextTokenValue();
										str2=TokenCleaning(str2);
										MasterArray=checkIfMonth(str2);
										if(MasterArray[0]=="1")
										{
											ts.remove();
											setMonth=true;
											month=Integer.parseInt(MasterArray[1]);
											ts.next();
											break;
										}
										else
										{
											setMonth=true;
											month=00;
											break;
										}
									}
								}
								else
								{
									setDay=true;
									day=01;
									setMonth=true;
									month=00;
									break;
								}
								
								
							}
							else
							{
								setDay=true;
								day=01;
								setMonth=true;
								month=00;
								break;
							}
							 
						}
						
						else
						{
							setDay=true;
							day=01;
							setMonth=true;
							month=00;
							break;
						}
						
					}
					
				}
			
			       //---------------Checking for Month Matching for Discrete Month Names---------------//
					String[] MasterArray=checkIfMonth(str);
					if(MasterArray[0]=="1")
					{
						setMonth=true;
						month=Integer.parseInt(MasterArray[1]);
						
						//------Continuation requires-------//
						if(ts.hasNext())
						{
							MasterArray=decomposeMasterArray(MasterArray);
							String str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							//----- Checking for Date or Year<=31 with BC or AD-------------//
							MasterArray=checkIfDateOrYear(str2,ts);
							if(MasterArray[0]=="1")
							{
								ts.remove();
								setDay=true;
								day=Integer.parseInt(MasterArray[1]);
								
								ts.next();
								
								//break;
							}
							else if(MasterArray[0]=="2")
							{
								ts.remove();
								setYear=true;
								year=Integer.parseInt(MasterArray[1]);
								//Do if Continuation requires
								setDay=true;
								day=01;
								ts.next();
								break;
								
							}	
						}
						if(ts.hasNext() && setDay==true)
						{
							String str2=ts.getNextTokenValue();
							str2=TokenCleaning(str2);
							MasterArray=decomposeMasterArray(MasterArray);
							MasterArray=checkIfYear(str2,ts);
							//------------ Checking for Year even with token containing year+BC/AD -------------//
							if(MasterArray[0]=="1")
							{
								ts.remove();
								setYear=true;
								year=Integer.parseInt(MasterArray[1]);
								//Do if Continuation requires
								if(setDay==false)
								{
									setDay=true;
									day=01;
									
								}
								ts.next();
								
							}
							else
							{
								
								setYear=true;
								year=1900;
								break;
							}
							
						}
						
							if(setDay==false)
							{
								setDay=true;
								day=01;
							}
							if(setYear==false)
							{
								setYear=true;
								year=1900;
							}
						break;
						
					}
		}
				
		if(setAssigned==false && setYear==true && setMonth==true && setDay==true)
		{
			month=month+1;
			strOrig=formDate(year,month,day);
			setAssigned=true;
		}
		
		//---Before Code Starts--//
		
		if(setAssigned==true)
		{
			strOrig=strOrig.trim();
			if(!strOrig.isEmpty())
			{
				ts.getCurrent().setTermText(strOrig);
				ts.getCurrent().setTermBuffer(strOrig.toCharArray());
			}
			else
			{
				ts.remove();
			}
		}
		else
		{
			ts.getCurrent().setTermText(strOrig);
			ts.getCurrent().setTermBuffer(strOrig.toCharArray());
		
	
	
	//ts.reset();
	tfs =new TokenFilterDates(ts);
	}
	return tfs;
}

public String[] decomposeMasterArray(String[] MasterArray)
{
	for(int i=0;i<MasterArray.length;i++)
		MasterArray[i]="";
	
	return MasterArray;
}

public String[] checkIfDateOrYear(String str,TokenStream ts)
{
	String[] MasterArray=new String[7];
	if(checkIfDate(str) && (!str.contains("BC")) && (!str.contains("AD")) && ts.hasNext())
	{
		MasterArray[0]="1"; //--Found and Day--//
		String str2=ts.getNextTokenValue();
		str2=RemoveDotSuffix(str2);
		if(str2.equalsIgnoreCase("BC")||str2.equalsIgnoreCase("AD"))
		{
			MasterArray[0]="2"; //--Found and BC/AD Year--//
			if(str2.equalsIgnoreCase("BC")) str="-"+str; 
			MasterArray[1]=str;
			
			ts.next();
			ts.remove();
			MasterArray[2]="1";
		}
		else {
			MasterArray[1]=str;
			MasterArray[2]="0";
		}
	}
	else MasterArray[0]="0";
	return MasterArray;
}

public boolean checkIfDate(String tokenData)
{
	if(tokenData.matches("^([0][1-9]|[12]\\d|3[0-1])$")) return true;
	else return false;
}
public String[] checkIfYear(String str,TokenStream ts)
{
	String[] MasterArray=new String[7];
	if(str.matches("^\\d{4}$"))
	{
		MasterArray[0]="1";
		MasterArray[1]=str;
		MasterArray[2]="0";
	}
	else if(str.matches("^\\d{1,3}AD$|^\\d{1,3}BC$"))
	{
		MasterArray[0]="1";
		if(str.contains("BC")) str="-"+str; 
		MasterArray[1]=str.replaceAll("[AD|BC]+$", "");
		MasterArray[2]="0";
	}
	else if(str.matches("^\\d{1,3}$") && ts.hasNext())
	{
		//Checking next token
		String str2=ts.getNextTokenValue();
		str2=RemoveDotSuffix(str2);
		if(str2.equalsIgnoreCase("BC")||str2.equalsIgnoreCase("AD")){
			MasterArray[0]="1";
			if(str2.equalsIgnoreCase("BC")) str="-"+str; 
			MasterArray[1]=str;
			ts.next();
			ts.remove();
			MasterArray[2]="1";
		}
	}
	else MasterArray[0]="0";
	return MasterArray;
}

public String[] checkIfMonth(String str)
{
	String[] MasterArray=new String[7];
	if(str.matches("(?i)(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?)"))
	{
		MasterArray[0]="1";
		str=str.trim().substring(0,3);
		Date date;
		try
		{
			date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(str);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			MasterArray[1] = Integer.toString(cal.get(Calendar.MONTH));
			//----Keep in mind month number starts from 0 to 11---//
			
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
	else MasterArray[0]="0";
	return MasterArray;
}

public String[] checkIfMMDDYY(String str)
{
	String[] MasterArray=new String[7];
	if(str.matches("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.]\\d\\d\\d\\d$")|| str.matches("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.]\\d\\d$"))  
	{
		MasterArray[0]="1"; //--Found--//
		String strProc=str.replaceAll("/|-|\\.", " ");
		MasterArray[1]=strProc.substring(0,2);
		MasterArray[2]=strProc.substring(3,5);
		if(strProc.length()>9)
			MasterArray[3]=strProc.substring(6);
			else
				MasterArray[3]="19"+str.substring(6);
	}
	else
		MasterArray[0]="0";
	return MasterArray;
}

public String[] checkIfDDMMYY(String str)
{
	String[] MasterArray=new String[7];
	if(str.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.]\\d\\d\\d\\d$")|| str.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.]\\d\\d$"))  
	{
		MasterArray[0]="1"; //--Found--//
		String strProc=str.replaceAll("/|-|\\.", " ");
		MasterArray[1]=strProc.substring(0,2);
		MasterArray[2]=strProc.substring(3,5);
		if(strProc.length()>9)
			MasterArray[3]=strProc.substring(6);
			else
				MasterArray[3]="19"+str.substring(6);
	}
	else
		MasterArray[0]="0";
	return MasterArray;
}
public String[] checkIfYearsRange(String str)
{
	String[] MasterArray=new String[7];
	if(str.matches("^\\d{4}-\\d{4}$") || str.matches("^\\d{4}-\\d{2}$"))
	{ 
		MasterArray[0]="1"; //--Found--//
		MasterArray[1]=str.substring(0,4);
		if(str.length()>8)
			MasterArray[2]=str.substring(5);
		else
			MasterArray[2]=str.substring(0,2)+str.substring(5);
	}
	else if(str.matches("^\\d{4}/\\d{4}$") || str.matches("^\\d{4}/\\d{2}$"))
	{ 
		MasterArray[0]="1"; //--Found--//
		MasterArray[1]=str.substring(0,4);
		if(str.length()>8)
			MasterArray[2]=str.substring(5);
		else
			MasterArray[2]=str.substring(0,2)+str.substring(5);
	}
	else
		MasterArray[0]="0";
	return MasterArray;
}

public String formDate(int year,int month,int day)
{
	String dateString="";
	if(year<=(-1))
	{
		dateString=dateString+"-";
		year=(-(year));
	}
	if(year>=0 && year<=9) dateString=dateString+"000"+String.valueOf(year);
	else if(year>=10 && year<=99) dateString=dateString+"00"+String.valueOf(year);
	else if(year>=100 && year<=999) dateString=dateString+"0"+String.valueOf(year);
	else dateString=dateString+String.valueOf(year);
	
	if(month>=0 && month<=9) dateString=dateString+"0"+String.valueOf(month);
	else dateString=dateString+String.valueOf(month);
	
	if(day>=0 && day<=9) dateString=dateString+"0"+String.valueOf(day);
	else dateString=dateString+String.valueOf(day);
	
	return dateString;
}

public String TokenCleaning(String str)
{
	str=RemoveDotSuffix(str);
	if(str.matches(".*\\d.*"))
	{
		str = str.replaceAll("[st|nd|rd|th|fth|,]+$", ""); //To remove st,nd,rd,th
		str=AddPrefixZero(str);
	}
	return str;
}
public String RemoveDotSuffix(String str)
{
	if(str.endsWith(".")) 
	{
		str=str.substring(0,str.length()-1);
	}
	return str;
}

public String AddPrefixZero(String str)
{
	if(str.matches("^[1-9]$"))
	{
		str="0"+str;
	}
	return str;
}

}

