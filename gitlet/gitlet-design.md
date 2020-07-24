# Gitlet Design Document

**Name**: Keeyou Kim

## Classes and Data Structures

- Main.java: The main function takes an argument from the terminal to run Gitlet.

- The command classes:
    - Init.java: creates a new version in the current directory.
    - Add.java: adds a copy of the file 
    - Commit.java: saves a snapshot of the file
    - Rm.java: upstages the file if the file is staged for addition.
    - Log.java: displays the commit tree.
    - GlobalLog.java: same as log, but displays all commits ever made.
    - Find.java: prints out all commits related to the given commit message.
    - Status.java: displays all branches and files.
    - CheckOut.java: overwrites the current version.
    - Branch.java: creates a new branch.
    - RmBranch.java: removes the branch.
    - Reset.java: resets the program.
    - Merge.java: merges files from one branch into another branch.

- Blob.java: contains content of a file.
    1. Byte[] contents: store SHA-1 of the file
    2. int version: counts number of version? (not sure of maybe in CommitObj)

- CommitObj.java: contains information of commit.
    1. String _message: contains log message
    2. String _time: time of commit
    3. Branch _branch: a reference to its tree
    4. CommitObj _parent: a reference to its parent
    5. Blob _content: points to the Blob object that contains the content of file
    
- BranchObj.java
    1. CommitObj head: it points to the head commit
    
- Repository.java
    1. 

## Algorithms

#### Main class

 - It only figures out what args contain and call the correct command with 
 proper parameters.
 
#### Command classes

 - Each class is able to be called from the Main class

#### Blob class

 - It has a constructor.
 - It must get a parameter of SHA-1 hash code for content.

#### CommitObj class

 - It has a constructor.
 - If parent is null, then set date equals to 00:00:00 UTC, 
 Thursday, 1 January 1970 (initial commit).
 - Series of commitObj will make a commit tree.
 - It can contain more than one pointer that points to blob. 
 (ex: blob0 -> Hello.txt, blob1 -> World.txt) maybe by using array
 - Setter and getter methods.

#### BranchObj class

 - It needs to keep updating the head whenever the program commits.
 - 
 
 #### Repository class

## Persistence

 - Init:
    1. Always start with one commit
    2. A single branch

 - Commit: when the commit command runs,
    1. It should the parent object should be connected to its most recent commit
    if it is not initial commit.
    2. Change the head of its branch to the new commit. (Keep updating 
    the branch)
    3. Points to the previous blob if the content (SHA-1 hash value) is same.
    If not, create a new blob.

Describe your strategy for ensuring that you don’t lose the state of your 
program across multiple runs. Here are some tips for writing this section:

This section should be structured as a list of all the times you will need 
to record the state of the program or files. For each case, you must prove 
that your design ensures correct behavior. For example, explain how you 
intend to make sure that after we call java gitlet.Main add wug.txt, on 
the next execution of java gitlet.Main commit -m “modify wug.txt”, the 
correct commit will be made.

A good strategy for reasoning about persistence is to identify which pieces 
of data are needed across multiple calls to Gitlet. Then, prove that the 
data remains consistent for all future calls.

This section should also include a description of your .gitlet directory 
and any files or subdirectories you intend on including there.

