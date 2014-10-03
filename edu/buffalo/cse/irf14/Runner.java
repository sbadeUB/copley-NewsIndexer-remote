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
import edu.buffalo.cse.irf14.query.QueryParser;

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
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		String[] files;
		File dir;
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		long start = System.currentTimeMillis();
		
		/*try {
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				if (files == null)
				continue;
				for (String f : files) {
					try {
					
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						writer.addDocument(d);
					} catch (ParserException e) {
						System.out.println("Sorry,Error Occurred due to invalid file");
						e.printStackTrace();
					} 
					
				}
				
			}
			
			writer.close();
			// do operation to be timed here
			long time = System.currentTimeMillis() - start;
			System.out.println(time/1000 +"(in secs)");
		
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		QueryParser.parse("(blue AND black) AND Author:bruises", "OR");
		
	}
	

}
