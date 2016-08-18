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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.christianbenner.gitcommitstatistics.FileEditor;

public class CommittedFile {
	public CommittedFile(String filename){
		m_filename = filename;
	}
	
	public void addEditor(String editorName, String date){
		// Check if editor exists on array
		boolean match = false;
		for(int n = 0; n < m_editors.size() && !match; n++){
			if(m_editors.get(n).m_name.equals(editorName)){
				match = true;
				
				// If developer is already found, increase their commit count
				m_editors.get(n).m_commits++;
			}
		}
		
		if(match){
			Main.log("Editor already existent"); //DEV
		}else{
			Main.log("Added new editor to " + m_filename); //DEV
			m_editors.add(new FileEditor(editorName));
		}
		
		// Determine if the editor is the earliest to edit the file (creator)
		if(m_ownerDate == null){
			m_ownerDate = date;
			m_owner = editorName;
		}else{
			Main.log("Year[" + scanDate(date, 0) + "], Month[" + scanDate(date, 1) + "], day[" + scanDate(date, 2) + "]");

			if(scanDate(date, 0) < scanDate(m_ownerDate, 0)){ //find a way to interpret date properly
				m_ownerDate = date;
				m_owner = editorName;
				Main.log("Added " + editorName + " as owner of " + m_filename);
			}else if(scanDate(date, 0) == scanDate(m_ownerDate, 0) &&
					scanDate(date, 1) < scanDate(m_ownerDate, 1)){
				m_ownerDate = date;
				m_owner = editorName;
				Main.log("Added " + editorName + " as owner of " + m_filename);
			}else if(scanDate(date, 0) == scanDate(m_ownerDate, 0) &&
					scanDate(date, 1) == scanDate(m_ownerDate, 1) &&
					scanDate(date, 2) < scanDate(m_ownerDate, 2)){
				m_ownerDate = date;
				m_owner = editorName;
				Main.log("Added " + editorName + " as owner of " + m_filename);
			}
		}
	}
	
	private int scanDate(String date, int section){
		@SuppressWarnings("resource")
		Scanner dateScanner = new Scanner(date);
		dateScanner.useDelimiter("-");
		
		for(int n = 0; n < section; n++){
			if(dateScanner.hasNext()){
				dateScanner.next();
			}else{
				System.err.println("Date couldn't be interpreted");
				return -1;
			}
		}
		
		int scan = dateScanner.nextInt();
		dateScanner.close();
		
		return scan;
	}
	
	public void calculateStats(){
		m_commitCount = 0;
		for(int n = 0; n < m_editors.size(); n++){
			m_commitCount += m_editors.get(n).m_commits;
		}
		
		for(int n = 0; n < m_editors.size(); n++){
			m_editors.get(n).m_commitPercentage = ((float) m_editors.get(n).m_commits / (float) m_commitCount) * 100.0f;
		}
	}
	
	String m_filename = null;
	String m_owner = null;
	String m_ownerDate = null;
	
	List<FileEditor> m_editors = new ArrayList<FileEditor>();
	
	int m_commitCount = 0;
}
