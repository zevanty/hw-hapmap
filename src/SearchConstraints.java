/* This class provides the constants used throughout the project.
 * 
 */
public class SearchConstraints {
	public static final int SubSeqLength = 30;	//length of search sequence
	public static final int SeqPartitionSize = 10;	// length of sequence partition
	public static final int NumOfPartitions = SubSeqLength/SeqPartitionSize;
	public static final int MaxThreshold = 2;	//max number of mismatches for search sequence	
}
