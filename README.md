# Project: Mutant Generation Testing
## Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016



### Table of Contents
1. How to Compile
2. How to Run
3. Run Options
4. Created Directories
5. Reading Results




## 1. How to compile:
```javac -cp "src/*;bin;" -d "bin" src/*.java```

*This step is unnecessary because the source files come compiled.




## 2. How to run:
```java -cp "src/*;bin/" GenerateMutants```

or use the batch script: run.bat




## 3. Run Options:
```java -cp "listOfJars;classPath" GenerateMutants [switch option [| ... ]] [Filelist]```

```
  help or ? = This help menu
  "listOfJars;classPath" = list of Jar files ; path to class files
  Filelist = SPACE separated list of files to mutate at random
  -num  = Number of Mutants: Integer of how many mutants to randomly generate
  -sim  = Simultaneous Mutations: True or False to run in parallel
  -max  = Max Threads: Integer of max number of threads to run at once
  -pf   = Pick File: Integer of the 0-indexed filelist (random by default)
  -pf+  = Pick File+: Integer of change to the file pick at every generation (0 by default)
  -pe   = Pick Expression: Integer of the 0-indexed list of expressions generated internally (random by default)
  -pe+  = Pick Expression+: Integer of change to expression to pick made at every generation (0 by default)
  -pc   = Pick Change: Integer of the 0-indexed list of changes: constant, operators (random by default)
  -pc+  = Pick Change+: Integer of change to the change option per generation (0 by default)
  -pm   = Pick Mutation: Integer of mutation to make or pick depending on the change selected (random by default)
  -pm+  = Pick Mutation+: Integer of change to the mutation made at every generation (0 by default)
```
  Note: All JUnit test cases reside in TestAll.java.

For example:
    ```java -cp "src/*;bin/" GenerateMutants -num 10 -sim true src/org/bukkit/Color.java src/org/bukkit/Note.java```

The command above generates 10 mutants simultaneously from the files Color.java and Note.java.

	
	
	
## 4. Created Directories
GenerateMutants creates two folders: `mutations` and `results`

`mutations` is a list of folders, each a new repository with one mutant file.

`results` is the output of a JUnit test on the respective mutations folder.	




## 5. Reading Results
Results are simply notated with a timestamp, a mutation, and pass or fail (fail showing the tests that rejected the mutation). You can see an example run of 50 mutations in the folder "saved results." 10 of these have been highlighted and renamed to demonstrate the different types of results that can be produced (called Fail# and Pass#).
