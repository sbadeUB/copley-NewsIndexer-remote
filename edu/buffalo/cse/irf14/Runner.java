/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

/**
 * @author nikhillo
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
	public static void main(String[] args) {
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
				
			}
			
			writer.close();
			
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		IndexReader rdr=new IndexReader(indexDir, IndexType.TERM);
		PrintStream s=null;
		try {
			s = new PrintStream(new File(indexDir+ File.separator +"outputResults"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScoringModel model=null;
		String corpdir="C:\\Users\\srinivasareddy\\Desktop\\corpus";
		SearchRunner run=new SearchRunner(indexDir,corpdir, 'E',s);
		File file=new File(indexDir+ File.separator +"quer");
		//run.query(file);
		//run.query("NATO", model);
		//run.query("trade deficit foreign exchange trade surplus balance of trade",model);
		//run.query("Place:washington AND federal treasury", model);
		//run.query("Computer",model);
		int dist=SearchRunner.minDistance("computer", "Computer");
		System.out.println(dist);
		
	}


}