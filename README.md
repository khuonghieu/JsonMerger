# Requirement: Merging a collection of JSON docs stored in txt files.
Suppose we have a large number of JSON files with the same structure and about the same type of data. The problem is merge (consolidate) them into one file and reduce duplicates. The main problem is that multiple files may have overlapping pieces of data. We want to remove the duplicates.

Format:
- The names of the files follow the following naming
convention: ArticleID_datetime.txt
- Each comment node has its unique id (shown in bold). If two comments from different files have the same ids, then they are one and the same comment.

Procedure:
- Keep all unique comment codes (id).
- Update the attribute value for the duplicated nodes based on the json latest document.

Task of merge function:
- Duplicates: If a comment node with the same id appears in two json document, then the merge json document will contain only one of them. Verify whether there are attributes whose values have changed. If so, update the values according to the most recent json document as given by the datetime in the file name.
- Additions: If a new comment node appears in a newer version of the json document, simply add it to the merged version.

Input dataset for implementation/testing: Folder "Disqus file".
Output dataset: Folder "Output File"
