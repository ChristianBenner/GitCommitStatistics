/**
 * @author Christian Benner
 *
 *Christian Benner DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, 
 *INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS[,][.] 
 *IN NO EVENT SHALL Christian Benner BE LIABLE FOR ANY SPECIAL, INDIRECT 
 *OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR 
 *OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR 
 *PERFORMANCE OF THIS SOFTWARE.
 */

package com.christianbenner.gcsoptimal;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.christianbenner.gcsoptimal.Logging;
import com.christianbenner.gcsoptimal.LogManager;

public class GCSMain {
	LogManager m_logreader;
	
	private int m_authorState = -1;
	List<String> m_authorWhitelist = new ArrayList<String>();
	
	public GCSMain(){
		// Init variables
		Logging.setConsoleLogging(false);
		Logging.setFileLogging(false);
	}
	
	void run(String[] args){
		// Init logreader
		m_logreader = new LogManager();
		
		// Check arguments
		if(!interpretArguments(args)){
			// If no arguments, set file logging to true
			Logging.setFileLogging(true);
		}
		
		// Read log
		if(!m_logreader.readLog()){ //if failed
			return;
		}
		
		// Calculate stats for all files
		for(int i = 0; i < m_logreader.getFilelistSize(); i++){
			m_logreader.getFile(i).calculateStats();
		}
		
		// Output log of all files found
		m_logreader.logFiles();
		
		writeCSV();
		//writeExtraCSV();
		
		Logging.log("Exit");
	}
	
	private boolean interpretArguments(String[] args){
		// Loop through arguments
		
		/*Criteria
		 * Logging state: -true/-false/-console/-file
		 * Extension whitelist: --cc,h etc
		 */
		Logging.m_fileLogging = true;
		String foundArgs = "";
		for(int i = 0; i < args.length; i++){
			foundArgs += args[i];
			if(i < args.length - 1){
				foundArgs += ", ";
			}
		}
		Logging.log("Arguments = " + foundArgs);
		
		for(int i = 0; i < args.length; i++){
			// Interpret what argument type (one dash or two)
			if(args[i].startsWith("---")){
				Logging.log("Started author option argument scan");
				// Author whitelist and state
				//---all = view commit percentages from all authors
				//---creator = view commit percentages from file creator
				//---custom-user1,user2,user3 = view commit with a custom list of creators
				String substring = args[i].substring(3, args[i].length());
				if(substring.equals("all")){
					// View data on all file committers
					m_authorState = 0;
					Logging.log("Argument: Author state was recognized as 'all'");
				}else if(substring.equals("creator")){
					// Only view data on the file creators
					m_authorState = -1;
					Logging.log("Argument: Author state was recognized as 'creator'");
				}else if(substring.contains("custom-")){
					// Read custom author whitelist
					m_authorState = 1;
					Logging.log("Argument: Author state was recognized as 'custom'");
					
					//New substring after custom text - this is safe!
					String authors = substring.substring(7, substring.length());
					
					Scanner authorListScanner = new Scanner(authors);
					authorListScanner.useDelimiter(",");
					
					while(authorListScanner.hasNext()){
						m_authorWhitelist.add(authorListScanner.next());
					}
					authorListScanner.close();
					
					String logString = "Authors: ";
					for(int x = 0; x < m_authorWhitelist.size(); x++){
						logString += m_authorWhitelist.get(x);
						if(x < m_authorWhitelist.size() - 1){
							logString += ", ";
						}
					}
					Logging.log(logString);
				}
			}else if(args[i].startsWith("--")){
				// Extension whitelist
				String substring = args[i].substring(2, args[i].length());
				
				Scanner argumentScanner = new Scanner(substring);
				argumentScanner.useDelimiter(",");
				
				while(argumentScanner.hasNext()){
					String val = argumentScanner.next();
					m_logreader.addToWhitelist(val);
					Logging.log("Argument: A whitelist element was recognized as '" + val + "'");
				}
				
				argumentScanner.close();
			}else if(args[i].startsWith("-")){
				// Logging state
				boolean recognized = true;
				String substring = args[i].substring(1, args[i].length());
				
				if(substring.equals("false")){
					Logging.setConsoleLogging(false);
					Logging.setFileLogging(false);
				}else if(substring.equals("true")){
					Logging.setConsoleLogging(true);
					Logging.setFileLogging(true);
				}else if(substring.equals("console")){
					Logging.setConsoleLogging(true);
					Logging.setFileLogging(false);
				}else if(substring.equals("file")){
					Logging.setConsoleLogging(false);
					Logging.setFileLogging(true);
				}else{
					// Un-familiar argument
					recognized = false;
					Logging.logError("Argument: logging state '" + substring + "' is not familiar with GCS");
				}
				
				if(recognized){
					Logging.log("Argument: logging state was recognized as '" + substring + "'");
				}
			}else{
				// Unrecogized format
				Logging.logError("Argument [" + args[i] + "] was not recognized by GCS");
			}
		}
		
		if(args.length == 0){
			Logging.log("No arguments have been provided!");
			return false;
		}else{
			Logging.log("Interpreted arguments");
			return true;
		}
	}
	
	private void writeExtraCSV(){
		PrintWriter csvOut = null;
		try {
			csvOut = new PrintWriter("extragcsdata.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Failed to create CSV file, no permission?");
		}
		
		//Write mean
		
		//Write active developers
		//scan the last 6 months for developers that commit every two weeks minimum
		//list of file creators with date of commit
		
		csvOut.close();
	}
	
	private void writeCSV(){
		Logging.log("Writing CSV");
		
		PrintWriter csvOut = null;
		try {
			csvOut = new PrintWriter("gcsdata.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Failed to create CSV file, no permission?");
		}
		
		// Iterate through files
		for(int n = 0; n < m_logreader.getFilelistSize(); n++){
			// Print owner
			csvOut.print(m_logreader.getFile(n).m_filename + ",[" +
					m_logreader.getFile(n).m_ownerCommitPercentage + "," + m_logreader.getFile(n).m_owner + "]");
			
			switch(m_authorState){
			case -1:
				csvOut.println();
				break;
			case 0:
				// all
				csvOut.print(",{");
				for(int i = 0; i < m_logreader.getFile(n).getEditorlistSize(); i++){
					csvOut.print("[" + m_logreader.getFile(n).m_editors.get(i).m_commitPercentage + "," +
								m_logreader.getFile(n).m_editors.get(i).m_name + "]");
					
					if(i < m_logreader.getFile(n).getEditorlistSize() - 1){
						csvOut.print(",");
					}
				}
				csvOut.println("}");
				break;
			case 1:
				// Make custom list first
				List<String> allowedAuthorsName = new ArrayList<String>();
				List<Float> allowedAuthorsData = new ArrayList<Float>();
				for(int i = 0; i < m_logreader.getFile(n).getEditorlistSize(); i++){
					for(int j = 0; j < m_authorWhitelist.size(); j++){
						Logging.log("Comparing: [" + m_logreader.getFile(n).m_editors.get(i).m_name + "] with [" +
									m_authorWhitelist.get(j));
						if(m_logreader.getFile(n).m_editors.get(i).m_name.equals(m_authorWhitelist.get(j))){
							allowedAuthorsName.add(m_authorWhitelist.get(j));
							allowedAuthorsData.add(m_logreader.getFile(n).m_editors.get(i).m_commitPercentage);
						}
					}
				}
				
				// Print list
				csvOut.print(",{");
				for(int i = 0; i < allowedAuthorsName.size(); i++){
					csvOut.print("[" + allowedAuthorsData.get(i) + "," +
								allowedAuthorsName.get(i) + "]");
					
					if(i < allowedAuthorsName.size() - 1){
						csvOut.print(",");
					}
				}
				csvOut.println("}");
				break;
			}
		}
		csvOut.close();
	}
}
