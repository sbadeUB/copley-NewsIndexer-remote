/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.*;

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
	 * @throws ParserException 
	 */
	public static void main(String[] args) throws ParserException {
		
		String ipDir = args[0];
		String indexDir = args[1];
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		String[] files;
		File dir;
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		long start = System.currentTimeMillis();
		
/*		try {
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
		
		//QueryParser.parse("(blue AND black) AND Author:bruises", "OR");
		String[] terms={"COMPUTER","SALE"};
		IndexReader rdr=new IndexReader(indexDir, IndexType.TERM);
		PrintStream s=null;
		ScoringModel model=null;
		String corpdir="C:\\Users\\TEJA\\Desktop\\corpus";
		SearchRunner run=new SearchRunner(indexDir,corpdir, 'Q',s);
		/*File dir2=new File(indexDir);
		indexDir=dir2.getAbsolutePath();
		File quer=new File(indexDir+ File.separator +"quer");
		//File dictionaryFile=new File(indexDir+ File.separator +"DocumentDictionary");
		PrintWriter out=null;
            try {
				out = new PrintWriter( new FileWriter(quer) );
				out.println("xyz");
				out.flush();out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
//	run.query("Category:(bade AND (geeks AND Deltas) OR (Needs AND Deeds) OR \"shit institute\")", model);
	//run.query("consumer price index consumer index cpi index consumer surplus",model);
		//run.query("trade deficit foreign exchange trade surplus balance of trade",model);
		//run.query("Place:tokyo NOT bank", model);
		//run.query("Author:\"Petrol Sucks\" OR \"bade rocks\" norming OR Place:Bangalore AND Author:(\"Petrol Sucks\" AND oil) AND Category:(invest OR ((lower OR stake) AND \"Analysts Sucks\"))", model);
		//run.query("Category:((\"lower class\" OR \"stake high\") OR (invest AND Baede) AND (bade OR (rozy AND rocks)) OR (bade OR (rozy AND \"rocks virgin\"))) AND Place:((\"lower class\" OR \"stake high\") OR (invest AND Baede) AND (bade OR (rozy AND rocks)) OR (bade OR (rozy AND \"rocks virgin\")))", model);
		//run.query("Category:acq OR computer", model);
		run.query("Category:(((acq OR cdf) OR (retail OR (rubber AND rye)) OR (ship OR (silver AND wheat)) OR (yen OR (silver AND zinc)))) OR Place:((LAKE OR SAN) OR (DUBLIN AND Atlanta) AND (ROME OR (mass AND ROME)) OR (MIAMI OR (Tenn AND PARIS)))", model);
		
		//Category:((acq OR retail) OR (rubber AND rye) OR (ship OR (silver AND wheat)) OR (yen OR (silver AND zinc))) OR 
		//run.query("author:torday AND (debt OR currency)", model);
		//run.query("Category:war AND Author:Dutt AND Place:Baghdad AND prisoners detainees rebels", model);
		//run.query("(love NOT war) AND Category:(movies NOT crime)", model);
		//Query2:{author:(brian OR richard) AND place:(paris OR washington)}
	//	Query3:{place:washington AND federal treasury}
	//run.query(quer);
		String rm="gjhckzjdv";
	}
}
