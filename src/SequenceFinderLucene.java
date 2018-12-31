/* This class performs the search using a Lucene Index.  Lucene is a tool written
 * in Java that helps performs indexing and searching.  I am using this to do the
 * 3 billion length reference sequence.  The index is stored in a separate file in the
 * harddisk.  The index is created in a different class.  Also, this will search using
 * all permutations of the search sequence.  It searches it in partitions of the search
 * sequence.  If it finds one partition, it'll find the next partition and make sure
 * the position matches.
 */

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class SequenceFinderLucene {
	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private String indexDir = "./indexes";	//location of the index files

	//constructor
	public SequenceFinderLucene() throws IOException {
		searcher = new IndexSearcher(indexDir);
		parser = new QueryParser("subsequence", new StandardAnalyzer());
	}

	//public function for the user to perform the search
	public long substrFinder(String substr) throws Exception {
		System.out.println("Sequence\tPosition");
		return substrRandomizer(substr.toUpperCase(), SearchConstraints.MaxThreshold, 0);
	}
	
	//searches the lucene index to get the results
	private Hits performSearch(String queryString) throws IOException, ParseException {
		Query query = parser.parse(queryString);
		Hits hits = searcher.search(query);
		return hits;
	}
	
	//this function will generate all the permutations possible of the search sequence
	private long substrRandomizer(String origSubstr, int threshold, int index) throws Exception {
		long count = 0;
		
		//no more permutations for search sequence, so get the number of appearances
		if (threshold == 0)
			return countSubstr(origSubstr);

		//generate permutations and get the number of appearances
		else {
			StringBuffer temp;
			int strlen = origSubstr.length();
			char currChar;
			
			//number of appearances for current search sequence
			count += countSubstr(origSubstr);

			//generate permutations for each position in search sequence
			for (int j = index; j < strlen; j++) {
				currChar = origSubstr.charAt(j);
				temp = new StringBuffer(origSubstr);

				//permutations is based on the current character,
				//meaning change current character to get different permutation
				switch (currChar) {
				case 'A':
					temp.setCharAt(j, 'T');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'C');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'G');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					break;
				case 'T':
					temp.setCharAt(j, 'A');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'C');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'G');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					break;
				case 'C':
					temp.setCharAt(j, 'A');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'T');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'G');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					break;
				case 'G':
					temp.setCharAt(j, 'A');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'T');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					temp.setCharAt(j, 'C');
					count += substrRandomizer(temp.toString(), threshold-1, j+1);
					break;
				default:
					//do nothing
				}
			}
		}
		return count;
	}
	
	//finds the number of appearances for a sequence
	public long countSubstr(String substr) throws Exception {
		long count = 0;
		String currSeqPartition;	//current partition of substr
		String luceneQuery;
		Hits results_part1;		//all locations of the first partition
		Hits results_rest;		//locations of the other partitions
		Hit currResult;			//current result (Hit object)
		Document doc;			//current result (Document object)
		long currSeqPos;		//current location of partition
		int partitionSize = SearchConstraints.SeqPartitionSize;
		int numPartitions = SearchConstraints.NumOfPartitions;
		int i = 0;
		boolean substrExist = true;
		
		//assumes that: substr.length() % SearchConstraints.SeqPartitionSize = 0
		currSeqPartition = substr.substring((i)*partitionSize, (i+1)*partitionSize);
		
		//find the first partition
		luceneQuery = "subsequence: " + currSeqPartition;
		results_part1 = performSearch(luceneQuery);
		if (results_part1.length() > 0) {
			//get all positions of 1st substring partition
			Iterator<Hit> iter = results_part1.iterator();
			while (iter.hasNext()) {
				currResult = iter.next();
				doc = currResult.getDocument();
				currSeqPos = Long.parseLong(doc.get("position"));
				currSeqPos += 10;	//position of the next partition
				
				//get the other partitions
				for (i = 1; i < numPartitions; i++) {
					//get a partition
					currSeqPartition = substr.substring((i)*partitionSize, (i+1)*partitionSize);
					luceneQuery = "subsequence: " + currSeqPartition + 
								" AND position: " + currSeqPos;
					results_rest = performSearch(luceneQuery);
					
					//if the next partition is found, find the next partition
					if (results_rest.length() > 0) {
						currSeqPos = Long.parseLong(((Document)((Hit)results_rest.iterator().next()).getDocument()).get("position"));
						currSeqPos += 10;	//position of the next partition
					}
					//next partition cannot be found at the proper location, so try another location of 1st partition
					else {
						substrExist = false;
						break;
					}
				}
				
				//output the result if a subsequence is found
				if (substrExist) {
					System.out.println(substr + "\t" + doc.get("position"));
					count++;
				}
				substrExist = true;
			}
		}
		return count;
	}	
}
