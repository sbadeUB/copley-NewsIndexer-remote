/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
/**
 * @author srinivasareddy
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!
		System.out.println(args[0]);
		//System.out.println(args[1]);
		
		File ipDirectory = new File(ipDir);
		
		//System.out.print(ipDirectory.getPath());
		String[] catDirectories = ipDirectory.list();
		System.out.println(catDirectories[0]);
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		
		try {
		for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				//System.out.println(dir);
				files = dir.list();
				//System.out.println(files);
				if (files == null)
					continue;
				//System.out.println(files);
				//System.out.println(dir.getAbsolutePath());
				//System.out.println(File.separator);
				for (String f : files) {
					//System.out.println(f);
					//System.out.println(dir.getAbsolutePath() + File.separator +f);
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						System.out.println("Document Prepared!");
						writer.addDocument(d);
						System.out.println("Document Fedded into System!");
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
				
			}
			
			writer.close();
		
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
