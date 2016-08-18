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

package com.christianbenner.gitcommitstatistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.christianbenner.gitcommitstatistics.CommittedFile;

public class Main {
	static List<CommittedFile> fileList = new ArrayList<CommittedFile>();
	static List<String> whiteList = new ArrayList<String>();
	
	static boolean logging = false;
	static boolean fileLogging = false;
	
	static PrintStream logStream = null;
	
	public static void main(String[] args){
		// Setup logging and whitelist state
		if(args.length > 0){
			if(args[0].equals("true")){
				logging = true;
				try {
					logStream = new PrintStream("GCSLog.txt");
					fileLogging = true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.err.println("Failed to create log file");
				}
			}
		}
		if(args.length > 1){
			String unstructuredWhitelist = args[1];
			Scanner extensionScanner = new Scanner(unstructuredWhitelist);
			extensionScanner.useDelimiter(",");
			
			while(extensionScanner.hasNext()){
				whiteList.add("." + extensionScanner.next());
			}
			
			extensionScanner.close();
		}

		// Retrieve a Git log from user input custom path
		File logFile = new File("gitlog.log");

		// Read log - introduce custom format
		System.out.println("[GCS] Scanning...");
		if(readLog(logFile)){
			System.out.println("[GCS] Successfully scanned");
		}else{
			System.out.println("[GCS] Scan unsuccessful");
		}
		
		// Form data - introduce custom format
		for(int n = 0; n < fileList.size(); n++){
			fileList.get(n).calculateStats();
		}
		
		// Print files and editors
		fileLog();
		
		//Write a CSV file presenting the data
		writeCSV();
	}
	
	private static void writeCSV(){
		System.out.println("Writing CSV");
		PrintWriter csvOut = null;
		try {
			csvOut = new PrintWriter("gcsdata.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Failed to create CSV file, no permission?");
		}
		
		for(int n = 0; n < fileList.size(); n++){
			float ownerCommitment = 0.0f;
			for(int i = 0; i < fileList.get(n).m_editors.size(); i++){
				if(fileList.get(n).m_owner.equals(fileList.get(n).m_editors.get(i).m_name)){
					ownerCommitment = fileList.get(n).m_editors.get(i).m_commitPercentage;
				}
			}
			csvOut.println(fileList.get(n).m_filename + "," +
							ownerCommitment + "," + fileList.get(n).m_owner);
		}
		
		csvOut.close();
	}
	
	private static boolean readLog(File logFile){
		Scanner logScanner = null;
		try {
			logScanner = new Scanner(logFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace(); //remove before release
			System.err.println("Failed to open log");
			return false;
		}
		
		logScanner.useDelimiter(">>>"); // Implement custom delimeters
		
		Scanner commitScanner;
		while(logScanner.hasNext()){
			String commit = logScanner.next(); // Fetch whole commits at a time
			
			commitScanner = new Scanner(commit); // Scan the commit string
			commitScanner.useDelimiter(" "); // Implement custom delimeter
			
			int lineCount = 0;
			String username = null;
			String date = null;
			boolean commitCorrupt = false;
			Scanner lineScanner;
			while(commitScanner.hasNextLine() && !commitCorrupt){ // Loop through each line in commit
				String line = commitScanner.nextLine();
				
				//Scan each lines
				lineScanner = new Scanner(line);
				lineScanner.useDelimiter(" ");
				
				if(lineCount == 0){ // On user and date line
					if(lineScanner.hasNext()){ // Should return user
						username = lineScanner.next();
						log("Found user: " + username); // Dev
					}else{
						System.err.println("Could not determine user, skipping a commit");
						commitCorrupt = true;
					}
					
					if(lineScanner.hasNext()){ // Should return date
						date = lineScanner.next();
						log("Found date: " + date); // Dev
					}else{
						System.err.println("Could not determine date, skipping this commit");
						commitCorrupt = true;
					}
				}else{ // On a content line
					if(lineScanner.hasNext()){
						String content = lineScanner.next();
						
						// Try to determine the content type - customizable (HOW SHOULD WE DO THIS?)
						
						boolean acceptedFile = true;
						for(int i = 0; i < whiteList.size(); i++){
							if(!content.contains(whiteList.get(i))){
								acceptedFile = false;
							}
						}
						
						if(acceptedFile){
							//System.out.println("Accepted as a file [" + content + "]"); //DEV
							
							// Add file to list
							addFile(new CommittedFile(content));
							
							// Add editor to file
							getFile(content).addEditor(username, date);
						}else{
							log("Content: " + content); // Dev
						}
					}
				}
				
				lineCount++;
				lineScanner.close();
			}
			log("\n");
			commitScanner.close();
		}
		logScanner.close();
		
		log("Successfully read log file");
		return true; //successful
	}
	
	private static void addFile(CommittedFile file){
		// Check if file is already in the list
		boolean exists = false;
		for(int n = 0; n < fileList.size() && !exists; n++){
			if(file.m_filename.equals(fileList.get(n).m_filename)){
				// File already exists in array
				exists = true;
			}
		}
		
		if(exists){
			log("File [" + file.m_filename + "] already exists"); //Dev
		}else{
			log("Added file [" + file.m_filename + "] to array"); //Dev
			fileList.add(file);
		}
	}
	
	private static CommittedFile getFile(String filename){
		for(int n = 0; n < fileList.size(); n++){
			if(filename.equals(fileList.get(n).m_filename)){
				return fileList.get(n);
			}
		}
		
		System.err.println("Failed to find file");
		return null;
	}
	
	private static void fileLog(){
		if(logging){
			for(int n = 0; n < fileList.size(); n++){
				System.out.print("File[" + fileList.get(n).m_filename + "], created by: " + fileList.get(n).m_owner + " with editors: ");
				
				for(int i = 0; i < fileList.get(n).m_editors.size(); i++){
					System.out.print(fileList.get(n).m_editors.get(i).m_name + " (" +
							fileList.get(n).m_editors.get(i).m_commits + "/" + 
							fileList.get(n).m_editors.get(i).m_commitPercentage + "%)");
					if(i < fileList.get(n).m_editors.size() -1){
						System.out.print(", ");
					}else{
						System.out.println("");
					}
				}
			}
		}
	}
	
	public static void log(String text){
		if(logging){
			System.out.println("[GCS] " + text);
			
			if(fileLogging){
				logStream.println(text);
			}
		}
	}
}
