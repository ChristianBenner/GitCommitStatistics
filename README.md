# GitCommitStatistics
Thanks for using Git Commit Statistics (GCS)
author: Christian Benner

This software was developed by Christian Benner (christianbenner35@gmail.com) and is free to use and distribute, however do not publish this content as your own or remove 'Christian Benner' from any author tags.

Christian Benner DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS[,][.] IN NO EVENT SHALL Christian Benner BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

# About
GitCommitStatistics is a small Java/shell script project built for running through a command line to create reports on any Git repository. The reports provide data on each file and the percentage of commits made by the files creator, furthermore giving anyone the opportunity to review the collabaration and team work rates for any repository.

# Requirements
Java Runtime envrionent 8 - lower JRE versions may be supported however are untested
GitBash/Git command support - The shell scripts call Git commands

# Usage
Running:
To run the program you must call 'entry.sh' from a command line, however you must also supply the script with arguments. The call with arguments is structured as 'entry.sh cloneurl logging extension_whitelist'
	
Arguments:
You must call the program with marked (*) arguments. For example: 'entry.sh https://github.com/cookbooks/java.git true md,rb,java'.
			
GCS takes three different arguments 'cloneurl', 'boolean_consolelogging' and extension_whitelist'
cloneurl (*):
This is the clone URL of your Git repository (e.g. https://github.com/ChristianBenner/testGIT.git). This argument is required for the program to produce a report.
			
boolean_consolelogging:
This is a boolean entry and only excepts the input 'true' or 'false'. By default, console logging is set to false, furthermore entering this parameter is optional if entering no whitelist attributes.
			
extension_whitelist:
The extension whitelist is used to allow specific filetypes from being monitored by the program. If no arguments are supplied, all files found in the repository are used. This argument takes as many strings as it is supplied. Usage example: 'entry.sh https://github.com/cookbooks/java.git true md,rb,java'. Seperate arguments with a comma only. To call this argument you must also call 'boolean_consolelogging', as this argument is always read as the third supplied.
	
# Program output
If GCS successfully reads a log file, it will output the data in a CSV file format. Furthermore the data is structured in the following way:
	
	file,percentageOfPrimaryCommits,owner
	anotherFile,percentageOfPrimaryCommits,owner
		
File:
	The file CSV output contains the path relative from the main branch of the Git repository, furthermore some files may be structured as 'path/file.extension' whilst others may be structured as 'file.extension'
		
Percentage of primary commits:
	The percentage of primary commit data is the gem of GCS, this data supplies the percentage of commits that the creator of the file has made. Furthermore if this value displays '100', the file creator makes up 100 percent of the users who have committed to that file, however if the value displays a different value 'x', then the creator has committed to the file x percent of times (as other users have also committed).
		
Owner:
	The owner is the user that the program detects as the earliest committee of the file. Furthermore if 'user1' committed to the file in 2016, and 'user2' committed to the same file in 2015, the program marks user2 as the 'Owner' of the file.
