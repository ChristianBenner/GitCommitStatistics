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
import java.util.Scanner;

import com.christianbenner.gcsoptimal.Logging;
import com.christianbenner.gcsoptimal.LogManager;

public class GCSMain {
	LogManager logreader;
	
	public GCSMain(){
		// Init variables
		Logging.setConsoleLogging(false);
		Logging.setFileLogging(false);
	}
	
	void run(String[] args){
		// Init logreader
		logreader = new LogManager();
		
		// Check arguments
		if(!interpretArguments(args)){
			// If no arguments, set file logging to true
			Logging.setFileLogging(true);
		}
		
		// Read log
		if(!logreader.readLog()){ //if failed
			return;
		}
		
		// Calculate stats for all files
		for(int i = 0; i < logreader.getFilelistSize(); i++){
			logreader.getFile(i).calculateStats();
		}
		
		// Output log of all files found
		logreader.logFiles();
		
		writeCSV();
		
		Logging.log("Exit");
	}
	
	private boolean interpretArguments(String[] args){
		// Loop through arguments
		
		/*Criteria
		 * Logging state: -true/-false/-console/-file
		 * Extension whitelit: --cc,h etc
		 */
		for(int i = 0; i < args.length; i++){
			// Interpret what argument type (one dash or two)
			if(args[i].startsWith("--")){
				// Extension whitelist
				String substring = args[i].substring(2, args[i].length());
				
				Scanner argumentScanner = new Scanner(substring);
				argumentScanner.useDelimiter(",");
				
				while(argumentScanner.hasNext()){
					String val = argumentScanner.next();
					logreader.addToWhitelist(val);
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
	
	private void writeCSV(){
		System.out.println("Writing CSV");
		
		PrintWriter csvOut = null;
		try {
			csvOut = new PrintWriter("gcsdata.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Failed to create CSV file, no permission?");
		}
		
		// Iterate through file list
		for(int n = 0; n < logreader.getFilelistSize(); n++){
			float ownerCommitment = 0.0f;
			// Iterate through files editors
			for(int i = 0; i < logreader.getFile(n).m_editors.size(); i++){
				// Find owner and assign percentage commit
				if(logreader.getFile(n).m_owner.equals(logreader.getFile(n).m_editors.get(i).m_name)){
					ownerCommitment = logreader.getFile(n).m_editors.get(i).m_commitPercentage;
				}
			}
			
			// Print owner and percentage commit
			csvOut.println(logreader.getFile(n).m_filename + "," +
							ownerCommitment + "," + logreader.getFile(n).m_owner);
		}
		
		csvOut.close();
	}
}
