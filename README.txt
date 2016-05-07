Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016

README



Table of Contents
1. How to Compile
2. How to Run
3. Run Options
4. Created Directories
5. Reading Results




1. How to compile:
javac -cp "src/*;bin;" -d "bin" src/*.java

*This step is unnecessary because the source files come compiled.




2. How to run:
java -cp "src/*;bin/" GenerateMutants

or use the batch script: run.bat




3. Run Options:
GenerateMutants [#OfMutants] [Multi-Threaded] [File1 [, File2 [, ... ]]]
        where   "src/*;bin/" = path to Jar files ; path to class files
                #OfMutants = Integer of how many mutants to randomly generate
                Multi-Threaded = True or False to run in parallel
                File1, File2, ... = Comma separated list of files to mutate at random
Note: All JUnit test cases belong in TestAll.java.

For example:
	java -cp "src/*;bin/" GenerateMutants 10 false src/org/bukkit/Color.java src/org/bukkit/ChatColor.java src/org/bukkit/Note.java
	
	will run 10 times in serial with 3 files as candidates for mutation.

	
	
	
4. Created Directories
GenerateMutants creates two folders: mutations and results
mutations is a list of folders, each a new repository with one mutant file.
results is the output of a JUnit test on the respective mutations folder.	




5. Reading Results
Results are simply notated with a timestamp, a mutation, and pass or fail (fail showing the tests that rejected the mutation). You can see an example run of 50 mutations in the folder "saved results." 10 of these have been highlighted and renamed to demonstrate the different types of results that can be produced (called Fail# and Pass#).