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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogManager {
	private List<CommittedFile> m_filelist = new ArrayList<CommittedFile>();
	private List<String> m_whitelist = new ArrayList<String>();
	
	public boolean readLog(){
		Logging.log("Scanning...");
		
		// Locate log file
		File logFile = new File("gitlog.log");
		if(!logFile.exists()){
			Logging.logError("Log file does not exist, the script failed to retrieve log file");
			return false;
		}
		
		// Read log file
		Scanner logScanner = null;
		try {
			logScanner = new Scanner(logFile);
		} catch (FileNotFoundException e) {
			Logging.logError("Scan unsucessful");
			e.printStackTrace();
			return false;
		}
		
		// Separate commits with '>>>' delimiter
		logScanner.useDelimiter(">>>");
		
		while(logScanner.hasNext()){
			//Iterate through commits
			String commit = logScanner.next();
			
			// Scan commit
			Scanner commitScanner = new Scanner(commit);
			commitScanner.useDelimiter(" ");
			
			// Commit variables
			int currentLine = 0;
			boolean commitValid = true;
			
			String username = null;
			String date = null;
			
			while(commitScanner.hasNextLine() && commitValid){ // Loop through each line in commit
				// Grab each line in commit
				String line = commitScanner.nextLine();
				
				// Scan line in commit
				Scanner lineScanner = new Scanner(line);
				if(currentLine == 0){
					// Username and date line
					lineScanner.useDelimiter("#");
					if(lineScanner.hasNext()){
						username = lineScanner.next();
					}else{
						Logging.logError("Failed to retrieve username for commit");
						commitValid = false;
					}
					
					if(lineScanner.hasNext()){
						date = lineScanner.next();
					}else{
						Logging.logError("Failed to retrieve date for commit");
						commitValid = false;
					}
					
					if(commitValid){
						Logging.log("Found user [" + username + "]");
						Logging.log("Found date [" + date + "]");
					}
				}else{
					// Content line
					lineScanner.useDelimiter(" ");
					
					if(lineScanner.hasNext()){
						String content = lineScanner.next();
						
						//See if mismatch on content
						boolean acceptedFile = false;
						if(m_whitelist.size() > 0){
							for(int i = 0; i < m_whitelist.size() && !acceptedFile; i++){
								int extLocation = -1;
								for(int n = 0; n < content.length(); n++){
									if(content.charAt(n) == '.'){
										// Found extension location
										extLocation = n;
									}
								}
								
								if(extLocation != -1){
									// Has an extension, now cut off extension and compare
									if(content.substring(extLocation + 1).equals(m_whitelist.get(i))){
										acceptedFile = true;
									}
								}
							}
						}else{
							acceptedFile = true;
						}
						
						if(acceptedFile){
							Logging.log("Accepted file [" + content + "]");
							
							// Add file to list
							addFile(new CommittedFile(content));
							
							// Add editor to file
							getFile(content).addEditor(username, date);
						}
					}
				}
				lineScanner.close();
				currentLine++;
			}
			Logging.log(""); // Put spaces between the commits
			commitScanner.close();
		}
		logScanner.close();
		
		Logging.log("Successfully scanned");
		return true;
	}
	
	private void addFile(CommittedFile file){
		// Check if file is already in the list
		boolean exists = false;
		for(int n = 0; n < m_filelist.size() && !exists; n++){
			if(file.m_filename.equals(m_filelist.get(n).m_filename)){
				// File already exists in array
				exists = true;
			}
		}
		
		if(exists){
			Logging.log("File [" + file.m_filename + "] already exists"); //Dev
		}else{
			Logging.log("Added file [" + file.m_filename + "] to array"); //Dev
			m_filelist.add(file);
		}
	}
	
	private CommittedFile getFile(String filename){
		for(int n = 0; n < m_filelist.size(); n++){
			if(filename.equals(m_filelist.get(n).m_filename)){
				return m_filelist.get(n);
			}
		}
		
		Logging.logError("Failed to find file");
		return null;
	}
	
	public int getFilelistSize(){
		return m_filelist.size();
	}
	
	public CommittedFile getFile(int elementPosition){
		return m_filelist.get(elementPosition);
	}
	
	public void addToWhitelist(String element){
		m_whitelist.add(element);
	}
	
	public void logFiles(){
		String sendToLog = "";
		
		for(int n = 0; n < m_filelist.size(); n++){
			sendToLog += ("File[" + m_filelist.get(n).m_filename + "], created by: " + m_filelist.get(n).m_owner + " with editors: ");
			
			for(int i = 0; i < m_filelist.get(n).m_editors.size(); i++){
				sendToLog += (m_filelist.get(n).m_editors.get(i).m_name + " (" +
						m_filelist.get(n).m_editors.get(i).m_commits + "/" + 
						m_filelist.get(n).m_editors.get(i).m_commitPercentage + "%)");
				if(i < m_filelist.get(n).m_editors.size() -1){
					sendToLog += ", ";
				}else{
					Logging.log(sendToLog);
					sendToLog = "";
				}
			}
		}
	}
}
