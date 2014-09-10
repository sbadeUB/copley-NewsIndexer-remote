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
		File file=new File(filename);
		System.out.println("FILEID="+file.getName());
		
		File category=new File(file.getParent()); 
		
		System.out.println("CATEGORY="+category.getName());
		int ch;
		String wholetextString= null;
            FileReader inf =null;
			try {
				 inf = new FileReader(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
            	int count=0;
				while((ch=inf.read())!=-1)
				{
					if(count==0) count=count+1;
					else
				   // System.out.print((char)ch);
					wholetextString=wholetextString+(char)ch;
				    
				}
				inf.close();
				//System.out.println(wholetextString);
				String[] totallines=wholetextString.split("\n");
				
				//for (int i = 1; i < totallines.length; i++) {
					//System.out.println(i+" "+totallines[i]);
				//}
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
			
			//System.out.println("Author Line:"+authorInfo);
			String author=null;
			String authorOrg=null;
			if(authorExists==1)//Author Info Processing
			{
				authorInfo.trim();
				int strtPos = authorInfo.indexOf("<AUTHOR>") + "<AUTHOR>".length();  
				int endPos = authorInfo.indexOf("</AUTHOR>", strtPos);  
				String authorTrimd = authorInfo.substring(strtPos, endPos).trim(); 
				//System.out.println(authorTrimd);
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
			//System.out.println("Author and Date:"+totallines[count+1]);
			String text = totallines[count+1].replaceAll("^\\s+", ""); //Removing Leading White Spaces
			//System.out.println("Text="+text);
			int commas=0;
			for(int i = 0; i < text.length(); i++)
			{
			    if(text.charAt(i) == ',') commas++;
			}
			//System.out.println(commas);
			String place=null;
			String[] placeDate = text.split(",");
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
			
			String[] dateContent = intermDateCont.split("-");
			System.out.println("DATE="+dateContent[0]);
			System.out.print("CONTENT="+dateContent[1]);
			String content=dateContent[1];
			for (int i = count+2; i < totallines.length; i++) {
				content=content+totallines[i];
				System.out.print(" "+totallines[i]);}
		    
			//String[] strids={"FILEID", "CATEGORY", "TITLE", "AUTHOR", "AUTHORORG", "PLACE", "NEWSDATE", "CONTENT"};
			
			docs.setField(FieldNames.FILEID, file.getName());
			docs.setField(FieldNames.CATEGORY, category.getName());
			docs.setField(FieldNames.TITLE, title);
			docs.setField(FieldNames.AUTHOR, author);
			docs.setField(FieldNames.AUTHORORG, authorOrg);
			docs.setField(FieldNames.PLACE, place.trim());
			docs.setField(FieldNames.NEWSDATE, dateContent[0].trim());
			docs.setField(FieldNames.CONTENT, content);
			
			
			
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    
		return docs;
	}

}
