# Results
## Reference sequence size: 1 million
* Method 1a (insertion) - 0.311 seconds
* Method 1b (mutation) - 0.322 seconds
* Method 2 (hashtable)
  * building hashtable - 10.335 seconds
  * searching - 0.037 seconds
* Method 3 (file index)
  * building indexes - 47.483 seconds
  * searching - 19.372 seconds

### Comments
It's not surprising the insertion test (1a) is faster than mutation test (1b) since with insertion, I just skip values. Also, it isn't very surprising that using a file index was slower than using hashtable since reads and writes to a file is much slower than reads and writes to memory. One interesting remark about this was that with the file index search, it found the results very fast, but then there was a period of computation time before the program terminated. Perhaps it was trying to close the file-reader object. I was actually surprised by the speed of the simple search. I thought that it would've taken much longer since it is a character by character search.

## Reference sequence size: 100 million
* Method 1a (insertion) - 42.331 seconds
* Method 1b (mutation) - 46.793 seconds
* Method 2 (hashtable) - didn't test, since not enough memory for this
* Method 3 (file index)
  * building indexes - 5835.674 seconds (about 97.261 minutes)
  * searching - 2184.521 seconds (about 36.409 minutes)

### Comments
As we can see, it takes much longer to do the file indexing. I'm not sure why there is the hang time even though the results are immediately found.

# Conclusion
From my own testing, my alternative method is not better than the simple method, which I found surprisingly weird. I think it is due to the fact that there is an overhead with file access. Also, there is that weird hang time when the program isn't doing anything when it finishes searching the file index. When I have time, I would like to work on this project more, especially to re-write the whole thing in a different language and come up with a better algorithm.