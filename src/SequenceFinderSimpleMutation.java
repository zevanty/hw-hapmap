/* This class provides the search using the simple method, where each character is
 * searched one at a time.  This class performs a mutation check on the search
 * string, meaning if search string was 'CAT' and reference string was 'AAT', it will
 * still be marked as valid find.  This will not handle an insertion.
 */

import java.io.BufferedReader;
import java.io.FileReader;

public class SequenceFinderSimpleMutation {
	
	//perform the search
	public long countSubstr(String substr, String sequenceFilePath) throws Exception {
		int maxStrLen = 1000;	//want to limit the length of a string before searching (for memory reasons)
		long count = 0;
		long filePos = 0;	//actual location of the substr in the file
		BufferedReader br = new BufferedReader(new FileReader(sequenceFilePath));
		StringBuffer tempSeqPartition = new StringBuffer();
		int currSeqItem;	//current subsequence of reference sequence (length = maxStrLen)
		int currPos = 0;	//current position inside substr
		int startPos = 0;	//starting position in currSeqItem
		int i = 0;			//current position in currSeqItem
		int numErrors = 0;
		boolean subSeqFound = false;
		
		substr = substr.toUpperCase();
		
		System.out.println("Position\tNumMutations");
		
		//read the file, 1 character at a time
		currSeqItem = br.read();
		while(currSeqItem > -1) {
			switch(currSeqItem) {
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
				//create the subsequence of reference sequence
				tempSeqPartition.append((char)currSeqItem);
				
				//get enough read character reads first before searching
				if (tempSeqPartition.length() >= maxStrLen) {
					//do search
					for (i = 0; i < maxStrLen; i++) {
						subSeqFound = false;
						
						//record 1st instance of comparison
						if (currPos == 0) {
							startPos = i;
						}
						
						//if match
						if (substr.charAt(currPos) == tempSeqPartition.charAt(i)) {
							//next substring character
							currPos++;
							
							//if done searching substring
							if (currPos == SearchConstraints.SubSeqLength) {
								subSeqFound = true;
							}
						}
						//if mismatch
						else {
							//if not reach error threshold yet, ignore error (mutation)
							if (numErrors < SearchConstraints.MaxThreshold) {
								currPos++;
								numErrors++;

								//if done searching substring
								if (currPos == SearchConstraints.SubSeqLength) {
									subSeqFound = true;
								}
							}
							//if reach error threshold, restart search at the next position
							else {
								numErrors = 0;
								if (currPos > 0) {
									filePos = filePos - (i - startPos);
									i = startPos;	//search next position in sequence from last point
								}
								currPos = 0;	//reset cursor for substring
							}
						}		
						
						if (subSeqFound) {
							count++;		//increase count
							currPos = 0;	//reset cursor for substring
							filePos = filePos - (i - startPos);	//actual location in file
							i = startPos;	//search next position in sequence
							System.out.println(filePos + "\t" + numErrors);
							numErrors = 0;	//reset error counter
						}
						
						//ran out of room, so clear the subsequence of reference sequence
						if (currPos == 0 && i >= (maxStrLen-SearchConstraints.SubSeqLength)) {
							tempSeqPartition.delete(0, i);
							break;
						}
						filePos++;
					}
				}
			default: 
				currSeqItem = br.read();
				break;
			}
		}
		
		//if we still have more room to search for subsequence after finish reading file, continue searching
		if (tempSeqPartition.length() >= SearchConstraints.SubSeqLength) {
			numErrors = 0;
			//do search
			for (i = 0; i < tempSeqPartition.length(); i++) {
				//record 1st instance of comparison
				if (currPos == 0) {
					startPos = i;
				}
				
				//if match
				if (substr.charAt(currPos) == tempSeqPartition.charAt(i)) {
					//next substring character
					currPos++;
					
					//if done searching substring
					if (currPos == SearchConstraints.SubSeqLength) {
						count++;		//increase count
						currPos = 0;	//reset cursor for substring
						filePos = filePos - (i - startPos);	//actual location in file
						i = startPos;	//search next position in sequence
						System.out.println(filePos + "\t" + numErrors);
						numErrors = 0;	//reset error counter
					}
				}
				//if mismatch
				else {
					//if not reach error threshold yet, ignore error (mutation)
					if (numErrors < SearchConstraints.MaxThreshold) {
						currPos++;
						numErrors++;
					}
					//else if reach error threshold, restart search at the next position
					else {
						numErrors = 0;
						if (currPos > 0) {
							filePos = filePos - (i - startPos);
							i = startPos;	//search next position in sequence from last point
						}
						currPos = 0;	//reset cursor for substring
					}
				}
				
				//ran out of room, so stop search
				if (currPos == 0 && i >= (tempSeqPartition.length()-SearchConstraints.SubSeqLength)) {
					break;
				}
				filePos++;
			}
		}
		
		return count;
	}
}
