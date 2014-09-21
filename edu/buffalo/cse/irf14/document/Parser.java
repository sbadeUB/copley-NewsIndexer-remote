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
		// TODO YOU MUST IMPLEMENT THIS
			File file=null;
			File category=null;
			FileReader inf =null;
			int ch;
			String wholetextString= null;
           
			try {
				 file=new File(filename);
				 category=new File(file.getParent());
				 inf = new FileReader(file);
				 System.out.println("FILEID="+file.getName());
				 System.out.println("CATEGORY="+category.getName());
			} catch (FileNotFoundException e) {
				System.out.println("Sorry, File not found!");
				e.printStackTrace();
			}
            try {
            	 int count=0;
				 while((ch=inf.read())!=-1)
				 {
					if(count==0) count=count+1;
					else
				   	wholetextString=wholetextString+(char)ch;
				 }
				 inf.close();
				 String[] totallines=wholetextString.split("\n");
			     String title=totallines[1]; 
			     System.out.print("TITLE="+title);	
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
			    	 if(!authorRefine[1].isEmpty())
			    	 {
			    		 authorOrg=authorRefine[1].trim();
			    		 System.out.println("AUTHORORG="+authorOrg);
			    	 }
			    	 else authorOrg="";
			     }
			     else{
			    	 author="";
			    	 authorOrg="";
			     }
			     
			     String text = totallines[count+1].replaceAll("^\\s+", ""); //Removing Leading White Spaces
			
			     String[] dateContent = text.split("-");
			     
			     int commas=0;
			     for(int i = 0; i < dateContent[0].length(); i++)
			     {
			    	 if(dateContent[0].charAt(i) == ',') commas++;
			     }
			
			     String place=null;
			     String[] placeDate = dateContent[0].split(",");
			     if(commas==2) {
			    	 place=placeDate[0]+","+placeDate[1];
			    	 System.out.println("PLACE="+place);}
			     else{
			    	 place=placeDate[0];
			    	 System.out.println("PLACE="+place);
				 }
			     String intermDateCont=null;
			     if(commas==1) intermDateCont=placeDate[1];
			     else          intermDateCont=placeDate[2];
			
			     System.out.println("DATE="+intermDateCont);
			     System.out.print("CONTENT="+dateContent[1]);
			     String content=dateContent[1];
			     for (int i = count+2; i < totallines.length; i++) {
			    	 content=content+totallines[i];
			    	 System.out.print(" "+totallines[i]);
			    	 }
		    
			
			     docs.setField(FieldNames.FILEID, file.getName());
			     docs.setField(FieldNames.CATEGORY, category.getName());
			     docs.setField(FieldNames.TITLE, title);
			     docs.setField(FieldNames.AUTHOR, author);
			     docs.setField(FieldNames.AUTHORORG, authorOrg);
			     docs.setField(FieldNames.PLACE, place.trim());
			     docs.setField(FieldNames.NEWSDATE, intermDateCont.trim());
			     docs.setField(FieldNames.CONTENT, content);
			
			
			
            } catch (IOException e) {
				System.out.println("Sorry!an IO error is occured");
				e.printStackTrace();
			}
    
		return docs;
	}

}
