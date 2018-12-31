/* This class performs the search using a Hashtable.  This class takes a Hashtable
 * as part of the object initialization (hashtable will be created elsewhere). It 
 * searches the Hashtable by parts, where if it finds one partition, it searches for the
 * next partition and makes sure the relative positions match.  It also performs
 * mutation search, meaning it permutes every single possibility of the search string.
 * Because it is a hashtable, it will be very memory intensive, so it is most suitable
 * for reference sequences of medium size, such as 1 million.
 */

import java.util.Hashtable;
import java.util.Vector;

public class SequenceFinderIndex {
	private Hashtable<String, Vector<Long>> map;
	
	//constructor.  Needs a hashtable for initialization.
	SequenceFinderIndex(Hashtable<String, Vector<Long>> input) {
		map = input;
	}
	
	//public function for the user to perform the search
	public long substrFinder(String substr) throws Exception {
		System.out.println("Sequence\tPosition");
		return substrRandomizer(substr.toUpperCase(), SearchConstraints.MaxThreshold, 0);
	}
	
	//this function will generate all the permutations possible of the search sequence
	private long substrRandomizer(String origSubstr, int threshold, int index) {
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
				switch(currChar){
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
	private long countSubstr(String substr) {
		long count = 0;
		String currSeqPartition;	//current partition of substr
		Vector<Long> seqPos;		//all the locations of a partition
		int partitionSize = SearchConstraints.SeqPartitionSize;
		int numPartitions = SearchConstraints.NumOfPartitions;
		long[] currSeqPos = new long[numPartitions]; //currently viewed location
		int i = 0;
		boolean substrExist = true;
		
		//assumes that: substr.length() % SearchConstraints.SeqPartitionSize = 0
		currSeqPartition = substr.substring((i)*partitionSize, (i+1)*partitionSize);
		
		//find 1st partition
		if (map.containsKey(currSeqPartition)) {
			//get all positions of 1st substring partition
			seqPos = map.get(currSeqPartition);
			
			//for each of the position of the 1st partition
			for (Long el : seqPos) {
				//store position's current location
				currSeqPos[i] = el;
				
				//find the other partitions to see if their positions match
				for (i = 1; i < numPartitions; i++) {
					//get a partition
					currSeqPartition = substr.substring((i)*partitionSize, (i+1)*partitionSize);
					
					//find the partition in the map
					if (map.containsKey(currSeqPartition)) {
						//if partition found, see if the position matches
						if (map.get(currSeqPartition).contains(currSeqPos[i-1]+partitionSize)) {
							//if position matches, store the position
							currSeqPos[i] = currSeqPos[i-1]+partitionSize;
							
							//if done finding all partitions, increase the count
							if (i+1==numPartitions) {
								System.out.println(substr + "\t" + el);
								count++; 
							}
						}
						//if partition's position doesn't match, try next for position of 1st partition
						else
							break;
					}
					//input substring doesn't exist (partition doesn't exist)
					else {
						substrExist = false;
						break;
					}
				}
				
				//if any partition doesn't exist, then the entire substring can't be found
				if (!substrExist) {
					substrExist = true;
					break;
				}
				//else, try next position
				i = 0;
			}
		}
		return count;
	}
}
