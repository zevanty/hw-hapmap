/* This class will read the sequence file and create either a Hashtable or a Lucene index,
 * depending on the user's choice.  It creates a Hashtable or index, with keys of a shorter
 * string length than the search string.  The values for these keys will be the position.
 * It takes all possible keys, so it finds it at every single position possible.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class SequenceProcessor {
	
	private Hashtable<String, Vector<Long>> map;
	private boolean isHashTable;
	private IndexWriter indexWriter = null;
	private String indexDir = "./indexes";

	//constructor.  Takes a boolean that determines if Hashtable or Lucene index.
	SequenceProcessor(boolean makeHashTable) {
		//hashtable
		if (makeHashTable) {
			map = new Hashtable<String, Vector<Long>>(128);
			isHashTable = true;
		}
		//lucene index
		else {
			isHashTable = false;
		}
	}	
	
	// creates (or retrieves) index at directory indexDir
	public IndexWriter getIndexWriter(boolean create) throws IOException {
		if (indexWriter == null) {
			indexWriter = new IndexWriter(indexDir, new StandardAnalyzer(), create);
		}
		return indexWriter;
	}

	// closes index
	public void closeIndexWriter() throws IOException {
		if (indexWriter != null)
			indexWriter.close();
	}	
	
	//reads the reference sequence file and creates a hashtable or index
	public void readFile(String filePath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		StringBuffer tempSeqPartition = new StringBuffer();
		String currSeqPartition = "";	//current subsequence of reference sequence (length = maxStrLen)
		Vector<Long> seqPos;	//all the locations of the partition
		Document doc = null;
		int currSeqItem;
		long currPos = 0;
		int progressCount = 1000000;	//number of sequences to process before displaying progress report
		
		//read the file, 1 character at a time
		currSeqItem = br.read();
		while (currSeqItem > -1) {
			switch (currSeqItem) {
			//only consider valid characters
			case 'A':
			case 'a':
			case 'C':
			case 'c':
			case 'G':
			case 'g':
			case 'T':
			case 't':
				//change to uppercase
				if (currSeqItem > 'T') {
					currSeqItem = currSeqItem - 'a' + 'A';
				}
				tempSeqPartition.append((char)currSeqItem);
				if (tempSeqPartition.length() >= SearchConstraints.SeqPartitionSize) {
					if(tempSeqPartition.length() > SearchConstraints.SeqPartitionSize) {
						tempSeqPartition.deleteCharAt(0);
					}
					//create the subsequece of reference sequence
					currSeqPartition = tempSeqPartition.toString();
					
					//make hashtable
					if (isHashTable) {
						//if a partition already exist, add new location
						if(map.containsKey(currSeqPartition)) {
							seqPos = map.get(currSeqPartition);
							seqPos.add(currPos);
						}
						//if partition doesn't exist, add partition and location
						else {
							seqPos = new Vector<Long>();
							seqPos.add(currPos);
						}
						map.put(currSeqPartition, seqPos);
						seqPos = null;
					}
					
					//make lucene index
					else {
						//must store each partition and location as its own entry
						doc = new Document();
						doc.add(new Field("position", String.valueOf(currPos), Field.Store.YES, Field.Index.UN_TOKENIZED));
						doc.add(new Field("subsequence", currSeqPartition.toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
						indexWriter.addDocument(doc);
					}
					if(currPos > 0 && (currPos % progressCount == 0)) {
						System.out.println("Number of sequences processed so far: " + currPos);
					}
					currPos++;
				}
			default: 
				currSeqItem = br.read();
				break;
			}
			
			//want a limit on the size, so it won't take too much memory
			if (currPos > 0 && (currPos % 300000 == 0)) {
				tempSeqPartition.setLength(0);
				tempSeqPartition = new StringBuffer(currSeqPartition);
				currSeqPartition = null;
			}
		}
		
		//close the lucene index writer
		if (!isHashTable) {
			closeIndexWriter();
		}
	}
	
	//returns a hashtable
	public Hashtable<String, Vector<Long>> getMap() {
		return map;
	}

}
