/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		Document docs=new Document();
			File file=null;
			File category=null;
			FileReader inf =null;
			int ch;
			String wholetextString= null;
			String intermDateCont=null;
			String place="";
			String content="";
			
			try
			{
				if(filename!=null && !filename.isEmpty())
				{
					file=new File(filename);
					if(file.exists())
					{
						category=new File(file.getParent());
						System.out.println("FILEID="+file.getName());
						System.out.println("CATEGORY="+category.getName());
						//--Code Goes Here---//
						 try {
							 inf=new FileReader(file);
			            	 int count=0;
							 while((ch=inf.read())!=-1)
							 {
								if(count==0) count=count+1;
								else
							   	wholetextString=wholetextString+(char)ch;
							 }
							 inf.close();
							 String[] totallines=wholetextString.split("\n");
							 String title="";
							 if(totallines[0].length()<10)
							 {
								 title=totallines[1].trim();
							 }
							 else
							 {
						     title=totallines[0].substring(5);
							 }
						     System.out.println("TITLE="+title);	
						     String authorInfo=null;//Checking for Author Info
						     int authorExists=0;
						     if(totallines[2].isEmpty()){
						    	 if(totallines[3].contains("<AUTHOR>")){
						    		 authorInfo=totallines[3];
						    		 authorExists=1;
						    		 count=3;
						    	 }
						    	 else{ count=2; }
						     	}
						     else{
						    	 if(totallines[2].contains("<AUTHOR>")){
						    		 authorInfo=totallines[2];
						    		 authorExists=1;
						    		 count=2;
						    	 }
						    	 else{ count=1; }
						     	}
						
						     String author=null;
						     String authorOrg=null;
						     if(authorExists==1)//Author Info Processing
						     {
						    	 authorInfo.trim();
						    	 int strtPos = authorInfo.indexOf("<AUTHOR>") + "<AUTHOR>".length();  
						    	 int endPos = authorInfo.indexOf("</AUTHOR>", strtPos);  
						    	 String authorTrimd = authorInfo.substring(strtPos, endPos).trim(); 
						    	 String authorRefine[]=authorTrimd.split(",");
						    	 authorRefine[0]=authorRefine[0].replaceAll("(?i)by","").trim();
						    	 author=authorRefine[0];
						    	 System.out.println("AUTHOR="+author);
						    	 if(authorRefine.length>1)
						    	 {
						    		 if(!authorRefine[1].isEmpty())
							    	 {
							    		 authorOrg=authorRefine[1].trim();
							    		 System.out.println("AUTHORORG="+authorOrg);
							    	 }
						    	 }
						    	 else authorOrg="";
						     }
						     else{
						    	 author="";
						    	 authorOrg=null;
						     }
						     
						     String text = totallines[count+1].replaceAll("^\\s+", ""); //Removing Leading White Spaces
						
						     String[] dateContent = text.split("-");
						     
						     int commas=0;
						     for(int i = 0; i < dateContent[0].length(); i++)
						     {
						    	 if(dateContent[0].charAt(i) == ',') commas++;
						     }
						    
						     
						    	 if(commas!=0)
						    	 {
						    		 if(dateContent.length<2)
								     {
								    	intermDateCont="";
								    	
									     String[] placeDate = dateContent[0].split(",");
								
									     if(commas==1) {
									    	 place=placeDate[0];
									    	 content=placeDate[1];
									    	 System.out.println("PLACE="+place);
									    	 System.out.println("CONTENT="+content);
									    	 }
									     else{
									    	 place=placeDate[0];
									    	 for(int i=1;i<placeDate.length;i++)
									    	 content=content+placeDate[1];
									    	 System.out.println("PLACE="+place);
									    	 System.out.println("CONTENT="+content);
										 }
									     
								     }
						    		else
								    {
						    		 String[] placeDate = dateContent[0].split(",");
						    			
								     if(commas==2) {
								    	 place=placeDate[0]+","+placeDate[1];
								    	 System.out.println("PLACE="+place);}
								     else{
								    	 place=placeDate[0];
								    	 System.out.println("PLACE="+place);
									 }
								     
								     if(commas==1) intermDateCont=placeDate[1];
								     else          intermDateCont=placeDate[2];
								
								     System.out.println("DATE="+intermDateCont);
								     System.out.print("CONTENT="+dateContent[1]);
								     content=dateContent[1];
						    	 }
						    		 
						    	 for (int i = count+2; i < totallines.length; i++)
						    	 {
						    		 content=content+totallines[i];
							    	 System.out.print(" "+totallines[i]);
						    	 }
						     }
						    	 
						    	 else if(commas==0)
						    	 {
						    		 place="";
						    		 intermDateCont="";
						    		 content="";
						    	 }
						
						     docs.setField(FieldNames.FILEID, file.getName());
						     docs.setField(FieldNames.CATEGORY, category.getName());
						     if(title!=null && title!="")
						     docs.setField(FieldNames.TITLE, title);
						     if(author!=null && author!="")
						     docs.setField(FieldNames.AUTHOR, author);
						     if(authorOrg!=null && authorOrg!="")
						     docs.setField(FieldNames.AUTHORORG, authorOrg);
						     if(place!=null && place!="")
						     docs.setField(FieldNames.PLACE, place.trim());
						     if(intermDateCont!=null && intermDateCont!="")
						     docs.setField(FieldNames.NEWSDATE, intermDateCont.trim());
						     if(content!=null && content!="")
						     docs.setField(FieldNames.CONTENT, content);
						/*System.out.println(docs.getField(FieldNames.TITLE));
						System.out.println(docs.getField(FieldNames.TITLE)[0]);
						*/
						
			            } catch (IOException e) {
							System.out.println("Sorry!an IO error is occured");
							e.printStackTrace();
						}

							
			    
					}
					else 
						{
						throw new ParserException();
						}
				 } 
				
				else
				{
					throw new ParserException();
				}
			}
			finally
			{
				//do nothing
			}
	
			return docs;
			
           
	}

}
