/* This is the main class.  It will call the other classes to perform the search.
 * It mainly handles mutations of the input sequence, but it can also handle the
 * insertion case if use the simple search.  The extra function to handle 3 billion
 * sequence size is to use a tool built in for Java called Lucene which creates
 * external indexes for searching. 
 * 
 * The reference sequence must be placed inside a file.  That file must only contain the
 * sequence, although I did set up filters to ignore non-ATCG characters.  Also, it 
 * assumes that the inputs the user provide will be enough for a search, such as the
 * reference sequence and the subsequence are appropriate lengths.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HapMapProject {

	public static void main(String[] args) throws Exception {
		long startTime;
		float endTime;
		long count = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "Yes";
		String substr = "";
		String sequence = "";
		
		//maps to hold the reference sequence
		SequenceProcessor mapHashTable = null;
		SequenceProcessor mapLuceneIndex = null;
		
		//searchers
		SequenceFinderIndex searcher = null;
		SequenceFinderSimpleMutation searcherSimpleMutation = null;
		SequenceFinderSimpleInsertion searcherSimpleInsertion = null; 
		SequenceFinderLucene searcherLucene = null;
		
		//get the location of the reference sequence.  It must be a file.
		System.out.print("Enter pathname of sequence file: ");
		sequence = br.readLine().trim();
		
		//prompt user 
		while (!input.equals("5")) {
			
			//get the search substring
			substr = "";
			while (substr.length() != SearchConstraints.SubSeqLength) {
				System.out.println("Enter substring to search (length must be exactly " +
									SearchConstraints.SubSeqLength + " characters long):");
				substr = br.readLine().trim();
				
				//invalid substring. User must re-enter
				if (substr.length() != SearchConstraints.SubSeqLength) {
					System.out.println("Length of substring is invalid.  Try again. \n");
				}
			}
			System.out.println();
			
			//select a search option
			System.out.println("Select a search option:");
			System.out.println("1. Simple search (can be slow for large datasets). Deals with insertions in reference sequence.");
			System.out.println("2. Simple search (can be slow for large datasets). Deals with mutations in reference sequence.");
			System.out.println("3. Search using built-in memory (memory intensive for map creation). Deals with mutations in reference sequence.");
			System.out.println("4. Search using index stored in a file (uses quite a bit of harddisk space to store index). Deals with mutations in reference sequence.");
			System.out.println("5. Quit");
			System.out.print("Make your selection: ");
			input = br.readLine().trim();
			
			//simple search that handles insertions
			if (input.equals("1")) {
				if (searcherSimpleInsertion == null)
					searcherSimpleInsertion = new SequenceFinderSimpleInsertion();
				startTime = System.currentTimeMillis();
				//System.out.println("Start Time = " + startTime);
				System.out.println("Begin simple2 search for: " + substr);
				count = searcherSimpleInsertion.countSubstr(substr, sequence);
				endTime = (System.currentTimeMillis()-startTime)/1000F;
				System.out.println("\nDone Searching\nElapsed Time = " + endTime);
				System.out.println("Number of simple2 results: " + count);
				System.out.println();				
			}
			//simple search that handles mutations
			else if (input.equals("2")) {
				if (searcherSimpleMutation == null) 
					searcherSimpleMutation = new SequenceFinderSimpleMutation();
				startTime = System.currentTimeMillis();
				//System.out.println("Start Time = " + startTime);
				System.out.println("Begin simple2 search for: " + substr);
				count = searcherSimpleMutation.countSubstr(substr, sequence);
				endTime = (System.currentTimeMillis()-startTime)/1000F;
				System.out.println("\nDone Searching\nElapsed Time = " + endTime);
				System.out.println("Number of simple results: " + count);
				System.out.println();				
			}
			//search using a hashtable as index. This handles mutations.
			else if (input.equals("3")) {
				//build the hashtable if necessary
				if (mapHashTable == null) {
					startTime = System.currentTimeMillis();
					//System.out.println("Start Time = " + startTime);
					System.out.println("Begin making map (this may take a while)\n");
					mapHashTable = new SequenceProcessor(true);
					mapHashTable.readFile(sequence);
					endTime = (System.currentTimeMillis()-startTime)/1000F;
					System.out.println("Done making map\nElapsed Time = " + endTime);
					System.out.println();
					searcher =  new SequenceFinderIndex(mapHashTable.getMap());
				}
				//perform the search
				startTime = System.currentTimeMillis();
				//System.out.println("Start Time = " + startTime);
				System.out.println("Begin complex search for: " + substr);
				count = searcher.substrFinder(substr);
				endTime = (System.currentTimeMillis()-startTime)/1000F;
				System.out.println("\nDone Searching\nElapsed Time = " + endTime);
				System.out.println("Number of complex results: " + count);
				System.out.println();
			}
			//search using a Lucene index. This handles mutations.
			else if (input.equals("4")) {
				//build the index if necessary
				if (mapLuceneIndex == null) {
					mapLuceneIndex = new SequenceProcessor(false);
					//if user wishes to build a new index rather than use existing
					System.out.print("Do you want to create a new index (Yes/No)? Default is \"Yes\".: ");
					String inputIndex = br.readLine().trim().toLowerCase();
					if(!(inputIndex.equals("no") || inputIndex.equals("n"))) {
						startTime = System.currentTimeMillis();
						//System.out.println("Start Time = " + startTime);
						System.out.println("Begin creating indexes (this may take a while)\n");
						mapLuceneIndex.getIndexWriter(true);
						mapLuceneIndex.readFile(sequence);
						endTime = (System.currentTimeMillis()-startTime)/1000F;
						System.out.println("Done creating indexes\nElapsed Time = " + endTime);
						System.out.println();
					}
					searcherLucene = new SequenceFinderLucene();
				}
				//perform the search
				startTime = System.currentTimeMillis();
				//System.out.println("Start Time = " + startTime);
				System.out.println("Begin index search for: " + substr);
				count = searcherLucene.substrFinder(substr);
				endTime = (System.currentTimeMillis()-startTime)/1000F;
				System.out.println("\nDone Searching\nElapsed Time = " + endTime);
				System.out.println("Number of search results: " + count);
				System.out.println();
			}
			//user quits the program
			else {
				input = "5";
				System.out.println("Goodbye");
				System.out.println();				
			}
			
			//if user performed a search, see if user wishes to perform another search
			if (!input.equals("5")) {
				System.out.print("Search another substring (Yes/No)?: ");
				input = br.readLine().trim().toLowerCase();
				//if user doesn't wish to perform another search, quit the program
				if(!(input.equals("yes") || input.equals("y"))) { 
					System.out.println("Goodbye");
					input = "5";
				}
				System.out.println();
			}
		}
	}

}
