# HapMap Reader

This was an old homework assignment to implement a HapMap reader, where I have to find a DNA sequence from a longer DNA sequence. I'm finally uploading it to GitHub to backup some of my old work just to make improvements to it when I have some free time.

# Search Methods
I did my assignment using several methods.  

## Simple Search
I implemented two versions: one that deals with insertions and one that deals with mutations.  

**Pros**
* Easy to implement

**Cons**
* Can be slow for very large datasets, since it is a character by character search

## Hashtable Indexing
This method uses hashtables and it deals with mutations as well. The difference between this and the simple search is that I permutate every single possible combination of the search sequence, rather than trying to find only the matching sequences.  

**Pros**
* Ideally, can perform searches faster after hashtable is built

**Cons**
* Uses up a lot of memory to store the hashtable, meaning it probably won't be ideal for very large reference sequences

## Lucene Indexing
This is similar to the hashtable indexing, except I index onto a file instead of in memory. This is achieved with Lucene, a tool written in Java that handles indexes and searching. You will need to download the tool (Java library) before you can use it. For more information on Lucene, see: https://lucene.apache.org/core/.

**Pros**
* Ideally, can perform searches faster after index is built
* Uses diskspace rather than memory. Since diskspace is "cheap", can easily handle very large reference sequences
* Since external files, the indexes can be ported to other machines

**Cons**
* Building index takes time
* Uses quite a large amount of diskspace

# Usage
When the user starts the program (`HapMapProject.java`), the user will be given a menu of which algorithm to perform the search. The user also must specify the file location of the reference sequence and provide a subsequence to search for. All my algorithms filter out non-ATCG characters. After a user performs a search, the user can search again using different sequences. For the file index search, the user has the option to either build a new index or use an existing one. 

# Sample Data
To generate a sample data for testing, run either the perl or python script located in the `util` folder and it will generate a `sample-sequence.txt` file that contains a random DNA sequence with invalid characters as well.