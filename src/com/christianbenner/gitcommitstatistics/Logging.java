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
import java.io.PrintStream;

public class Logging {
	static boolean m_consoleLogging = false;
	static boolean m_fileLogging = false;
	
	private static boolean m_fileLoggingInit = false;
	private static PrintStream m_fileLogger;
	
	public static void setConsoleLogging(boolean state){
		m_consoleLogging = state;
	}
	
	public static void setFileLogging(boolean state){
		m_fileLogging = state;
	}
	
	public static void log(String text){
		if(m_consoleLogging){
			// Print to console
			System.out.println("[GCS] " + text);
		}
		
		if(m_fileLogging){
			// Check if file logger has been initialized
			if(!m_fileLoggingInit){
				try {
					m_fileLogger = new PrintStream("gcslog.txt");
					m_fileLoggingInit = true;
				} catch (FileNotFoundException e) {
					m_fileLogging = false;
					logError("Failed to create gcs log file");
					e.printStackTrace();
				}
			}
			
			//Use file stream and print to that too
			if(m_fileLogging){
				m_fileLogger.println(text);
			}
		}
	}
	
	public static void logError(String text){
		if(m_consoleLogging){
			// Print to console
			System.err.println("[GCS ERROR] " + text);
		}
		
		if(m_fileLogging){
			// Check if file logger has been initialized
			if(!m_fileLoggingInit){
				try {
					m_fileLogger = new PrintStream("gcslog.txt");
					m_fileLoggingInit = true;
				} catch (FileNotFoundException e) {
					m_fileLogging = false;
					logError("Failed to create gcs log file");
					e.printStackTrace();
				}
			}

			//Use file stream and print to that too
			if(m_fileLogging){
				m_fileLogger.println("[ERROR] " + text);
			}
		}
	}
}
