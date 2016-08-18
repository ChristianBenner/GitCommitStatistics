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

public class FileEditor {
	public FileEditor(String name){
		m_name = name;
	}
	
	String m_name;
	int m_commits = 1;
	float m_commitPercentage = 0;
}
