/*
Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016
*/

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.tools.*;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class GenerateMutants implements ThreadCompleteListener {

	//////////////////////
	//Optional Variables//
	//////////////////////
	
	//PRINT LOG
	//Print meta data (file info)
	public static boolean printData = true;
	
	//Print mutated source code
	public static boolean printNewCode = false;
	
	//Print commands executed on the OS
	public static boolean printCommands = false;
	
	//Print expressions in list
	public static boolean printExpressions = false;
	
	
	//FILE PATHS
	//Files to mutate
	public static String filenames[] = 
	{
		"src/org/bukkit/ChatColor.java",
		"src/org/bukkit/Color.java",
		"src/org/bukkit/Note.java"
	};

			
	//The output folder of result files
	public static String resultPath = "results";
	
	//Location to store mutations (appended by a number)
	public static String outputPath = "mutations/"; 
	
	//NOTE that the Test file is controlled from RunTest.java manually
	
	
	//GENERATION OPTIONS
	//List of mutants
	public ArrayList<GenerateMutant> mutants;
	
	//number of mutants to create
	public static int numOfMutants = 10;
	
	//Test simultaneously
	public static boolean testSimultaneously = false;
	
	//Max threads
	public static int maxThreads = 5;
	
	//The file in the list above to select (0 ... n - 1 files, null = random)
	public static Integer file = null; 
	public static int fileDelta = 0; //change automatically on every iteration
	
	//The expression in the list to change (0 ... n - 1 expressions, null = random)
	public static Integer exp = null; 
	public static int expDelta = 0; //change automatically on every iteration
	
	//Type of expression to change (0 = change constant, 1 = change operator, null = random)
	public static Integer change = null; 
	public static int changeDelta = 0; //change automatically on every iteration
	
	//Mutation input (constant for change 0, operator selection for change 1, null = random)
	public static Integer mutant = null;
	public static int mutantDelta = 0; //change automatically on every iteration
	
	
	//EXECUTE PROCESS
	//Compile timeout in seconds
	public static int compileTimeout = 15;
	
	//Test timeout in seconds
	public static int testTimeout = 10;
	
	
	//Argument Options
	public static String numberKey = "num";	
	public static String simultaneousKey = "sim";
	public static String maxThreadsKey = "max";
	public static String pickFileKey = "pf";
	public static String pickFileDeltaKey = "pf+";
	public static String pickExpKey = "pe";
	public static String pickExpDeltaKey = "pe+";
	public static String pickChangeKey = "pc";
	public static String pickChangeDeltaKey = "pc+";
	public static String pickMutantKey = "pm";
	public static String pickMutantDeltaKey = "pm+";
	
	//Other variables
	Date startTime;
	int mutint; //current mutant id
	int passCount; //Number of passed tests
	int failCount; //Number of failed tests
	
	///////////////
	//Main method//
	///////////////
	//Options:
	//args0 = int number of mutants (Default is 5)
	//args1 = boolean multi-threaded? (Default is no, change printData if true)
	//args2-n = String[] list of files to use (Defaults are listed above)
	public static void main(String[] args) {
		int num = 0;
		while(num < args.length)
		{
			num = options(args, num);
		}
		
		new GenerateMutants();

	}
	
	public GenerateMutants(){

		System.out.println("Starting... Please wait... Preparing to run " + numOfMutants + " mutation test(s)...");
		
		startTime = new Date();
		
		//Delete old results
		GenerateMutant.deleteFolder(resultPath);
		
		//Delete old mutations
		GenerateMutant.deleteFolder(outputPath);
				
		//////////////////
		//Create mutants//
		//////////////////
		String filename = null;
		String code = null;
		String outputFile = null;
		String classPath = null;
		mutants = new ArrayList<GenerateMutant>(maxThreads);
		
		for(mutint = 1; mutint <= numOfMutants; mutint++)
		{
			//Randomly pick a file from the list above
			//Or select it manually using 'file'
			String newFilename;
			if(file != null)
			{
				//Loop the counter to stay in the context of the list
				if(file >= filenames.length)
					file = 0;
				else if(file < 0)
					file = filenames.length-1;
				
				newFilename = filenames[file];
			}
			else
				newFilename = filenames[(int)(Math.random() * filenames.length)];
			
			//If this is a new file, then populate the necessary values
			if(newFilename.equals(filename) == false)
			{
				filename = newFilename;
				int slash = filename.indexOf("/");
				outputFile = filename.substring(slash + 1); //File to mutate
				classPath = filename.substring(0, slash); //self explanatory
				
				//Check if file exists
				if((new File(filename)).exists() == false)
				{
					System.err.println("Error: " + filename + " does not exist!");
					filename = null;
					continue;
				}
				
				//Read original source code
				//System.out.print("Reading source code from " + filename + "... ");
				code = getSourceCode(filename);
				//System.out.println("Done!\n");
			} else {
				//System.out.println("Reusing source code from " + filename + "!");
				code = getSourceCode(filename);
			}
			
			//Wait until there are fewer mutants than the max count
			while(mutants.size() >= maxThreads)
			{
				//System.out.println("Sleeping...");
				sleep(1000);				
			}
			
			//Generate!
			System.out.println("---Generating mutant test " + mutint + " on " + outputPath + mutint + "/" + outputFile + "---");
			GenerateMutant m = new GenerateMutant(mutint, code, outputPath + mutint + "/", outputFile, classPath, exp, change, mutant);
			m.addListener(this);
			mutants.add(m);
			
			//Start the thread!
			Thread t = new Thread(m);
			t.start();
			
			if(!testSimultaneously)
			{
				try {				
					t.join();
				} catch (InterruptedException e) {
					System.err.println("GenerateMutant thread was interrupted! " + e.toString());
				}
			}
			
			
			//Increment values
			if(file!=null)
				file += fileDelta;
			if(exp!=null)
				exp += expDelta;
			if(change!=null)
				change += changeDelta;
			if(mutant!=null)
				mutant += mutantDelta;
			
			/*
			//Show separator
			if(mutint != numOfMutants)
			{
				System.out.println("---------------------------------------------------------");
			}
			*/
		}	
	}
	
	public void notifyOfThreadComplete(GenerateMutant mutant) {
		
		//Print the result
		String print = "---Mutant test " + mutant.id + " finished in " + mutant.runTime + " seconds: Mutant ";
		if(mutant.passed)
		{
			print += "Survived";
			passCount++;
		}
		else
		{
			print += "Killed";
			failCount++;
		}
		System.out.println(print + "!---");
		
		//Remove the mutant so more can join
		mutants.remove(mutant);
		
		//Finish
		if(mutants.size() == 0 && mutint >= numOfMutants)
		{
			Date endTime = new Date();
			System.out.println("Done! Generated and tested " + numOfMutants + " mutants in " + ((float)(endTime.getTime() - startTime.getTime()) / 1000.0f) + " seconds.\n"+
					failCount + " (" + ((float)failCount/numOfMutants*100) + "%) killed and " + 
					passCount + " (" + ((float)passCount/numOfMutants*100) + "%) survived.\n" +
					"Results are in the folder: \"" + resultPath + "\"");
		}
		
	}
	
	public static String getSourceCode(String filename)
	{

		String code = "";
		
		try{
			FileInputStream file = new FileInputStream(filename);
			byte[] bytes = new byte[2048];
			while(file.available() > 0)
			{
				int read = file.read(bytes);
				
				code += new String(bytes, 0, read, StandardCharsets.UTF_8);
			}
			file.close();
		}catch(FileNotFoundException ex)
		{
			System.err.println("File not found: " + filename);
		}catch(IOException ex)
		{
			System.err.println("Could not read file: " + filename);
		}
		
		return code;
	}
	
	//Sleep
	public static void sleep(int milli)
	{
		try {
			Thread.sleep(milli);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
	
	//Process argument num 
	//return the number that is next to process
	public static int options(String[] args, int num)
	{
		//check for out of bounds
		if(num >= args.length)
		{
			return num;
		}
		
		//process help
		if(args[num].toLowerCase().contains("help") || args[num].contains("?"))
		{
			System.out.println(help());
			System.exit(0);
		}
			
		//number of mutants
		if(match(args[num], numberKey))
		{		
			num++;
			try{
				numOfMutants = Integer.parseInt(args[num++]);
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + numberKey + " (number of mutants): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//multi-threaded
		else if (match(args[num],simultaneousKey))
		{
			num++;
			try{
				testSimultaneously = Boolean.parseBoolean(args[num++]);
				
				if(testSimultaneously)
					printData = false;
				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected true or false for -" + simultaneousKey + " (simultaneous): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//max number of threads
		else if (match(args[num],maxThreadsKey))
		{
			num++;
			try{
				maxThreads = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + maxThreadsKey + " (max number of threads): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Pick the file to use
		else if (match(args[num],pickFileKey))
		{
			num++;
			try{
				file = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickFileKey + " (file to use from the 0-indexed filelist): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Change the pick of the file to use
		else if (match(args[num],pickFileDeltaKey))
		{
			num++;
			try{
				fileDelta = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickFileDeltaKey + " (change the pick of the file to use from the 0-indexed filelist): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Pick the expression to use
		else if (match(args[num],pickExpKey))
		{
			num++;
			try{
				exp = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickExpKey + " (expression to use from the 0-indexed list of expressions generated internally): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Change the pick of the expression to use
		else if (match(args[num],pickExpDeltaKey))
		{
			num++;
			try{
				expDelta = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickExpDeltaKey + " (change expression to use from the 0-indexed list of expressions generated internally): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Pick the change to use
		else if (match(args[num],pickChangeKey))
		{
			num++;
			try{
				change = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickChangeKey + " (change to use from the 0-indexed list of changes; see help for more): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Change the pick of the change to use
		else if (match(args[num],pickChangeDeltaKey))
		{
			num++;
			try{
				changeDelta = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickChangeDeltaKey + " (change the change to use from the 0-indexed list of changes; see help for more): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Pick the mutation to use
		else if (match(args[num],pickMutantKey))
		{
			num++;
			try{
				mutant = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickMutantKey + " (mutation to make or pick from the 0-indexed list of changes; see help for more): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//Change the pick of the expression to use
		else if (match(args[num],pickMutantDeltaKey))
		{
			num++;
			try{
				mutantDelta = Integer.parseInt(args[num++]);				
			}catch(NumberFormatException ex)
			{
				System.err.println("Expected an integer for -" + pickMutantDeltaKey + " (change the mutation to make or use from the 0-indexed list of changes; see help for more): " + args[num] + "\n\n" + help());
				System.exit(-1);
			}
		}
		//File names are the default if no switch was found
		else {
			int start = num;
			filenames = new String[args.length - num];
			for(;args.length > num; num++)
			{
				//Files
				String newPath = args[num];
				
				if((new File(newPath)).exists() == false)
				{
					System.err.println("File does not exist: " + newPath + "\n\n" + help());
					System.exit(-1);
				}
				
				filenames[num - start] = newPath;
			}	
		}
		
		return num;
	}
	
	//Convenience method for matching string inputs with options
	public static boolean match(String input, String option)
	{
		return (input.toLowerCase().equals("-"+option) || input.toLowerCase().equals("/"+option));
	}
	
	public static String help() {
		return "How to use GenerateMutants:\n" +
			   "java -cp \"listOfJars;classPath\" GenerateMutants [switch option [| ... ]] [Filelist]\n" +
			   "\n" + 
			   "  help or ? = This help menu\n" +
			   "  \"listOfJars;classPath\" = list of Jar files ; path to class files\n" + 
			   "  Filelist = SPACE separated list of files to mutate at random\n" +
			   "  -" + numberKey + "\t= Number of Mutants: Integer of how many mutants to randomly generate\n" +
			   "  -" + simultaneousKey + "\t= Simultaneous Mutations: True or False to run in parallel\n" +
			   "  -" + maxThreadsKey + "\t= Max Threads: Integer of max number of threads to run at once\n" +
			   "  -" + pickFileKey + "\t= Pick File: Integer of the 0-indexed filelist (random by default)\n" +
			   "  -" + pickFileDeltaKey + "\t= Pick File+: Integer of change to the file pick at every generation (0 by default)\n" +
			   "  -" + pickExpKey + "\t= Pick Expression: Integer of the 0-indexed list of expressions generated internally (random by default)\n" +
			   "  -" + pickExpDeltaKey + "\t= Pick Expression+: Integer of change to expression to pick made at every generation (0 by default)\n" +
			   "  -" + pickChangeKey + "\t= Pick Change: Integer of the 0-indexed list of changes: constant, operators (random by default)\n" +
			   "  -" + pickChangeDeltaKey + "\t= Pick Change+: Integer of change to the change option per generation (0 by default)\n" +			   
			   "  -" + pickMutantKey + "\t= Pick Mutation: Integer of mutation to make or pick depending on the change selected (random by default)\n" +
			   "  -" + pickMutantDeltaKey + "\t= Pick Mutation+: Integer of change to the mutation made at every generation (0 by default)\n" +	
			   "  Note: All JUnit test cases reside in TestAll.java.\n" +
			   "\n" +
			   "For example:\n" +
			   "    java -cp \"src/*;bin/\" GenerateMutants -"+numberKey+" 5 -" + simultaneousKey + " true " +
			   "src/org/bukkit/Color.java " + "src/org/bukkit/Note.java\n" +
			   "\n" +
			   "The command above generates 5 mutants simultaneously from the files Color.java and Note.java.";
	}

}


interface ThreadCompleteListener {
    void notifyOfThreadComplete(final GenerateMutant thread);
}


class GenerateMutant implements Runnable 
{
	//Print meta data (file info)
	public static boolean printData = GenerateMutants.printData;
	
	//Print mutated source code
	public static boolean printNewCode = GenerateMutants.printNewCode;
	
	//Print commands executed on the OS
	public static boolean printCommands = GenerateMutants.printCommands;
	
	//Print expressions in the list
	public static boolean printExpressions = GenerateMutants.printExpressions;
	
	//Compile timeout in seconds
	public static int compileTimeout = GenerateMutants.compileTimeout;
	
	//Test timeout in seconds
	public static int testTimeout = GenerateMutants.testTimeout;
	
	//Id of this mutant
	public int id;
	
	//Time taken to run
	public float runTime; //in seconds
	
	//True if test passed
	public boolean passed;
	
	//Listeners
	final Set<ThreadCompleteListener> listeners;
		
	//Source code
	String code;
	
	//Output
	public String outputPath;
	public String outputFile;
	String resultPath = GenerateMutants.resultPath;
	
	//Mutation Folder
	String classPath;
	
	//The expression in the list to change (0 ... n - 1 expressions, null = random)
	Integer preferredExpression;
	
	//Type of expression to change (0 = change constant, 1 = change operator, null = random)
	Integer preferredChange;
	
	//Mutation input (constant for change 0, operator selection for change 1, null = random)
	Integer preferredMutation;
	
	
	public GenerateMutant(int id, String sourceCode, String outputFolder, String outputFile, String classDir)
	{
		this(id, sourceCode, outputFolder, outputFile, classDir, null, null, null);
	}
	
	public GenerateMutant(int id, String sourceCode, String outputFolder, String outputFile, String classDir,
			Integer specifyExpression, Integer specifyChange, Integer specifyMutation)
	{
		this.id = id;
		code = sourceCode;
		//int lastSlash = output.lastIndexOf("/");
		outputPath = outputFolder;//output.substring(0, lastSlash + 1);
		this.outputFile = outputFile;//output.substring(lastSlash + 1);
		classPath = classDir;
		preferredExpression = specifyExpression;
		preferredChange = specifyChange;
		preferredMutation = specifyMutation;
		listeners = new CopyOnWriteArraySet<ThreadCompleteListener>();
		passed = false;
	}
	
	public void run()
	{
		//Save time stats
		Date startTime = new Date();
		
		
		//Mutation
		Mutator.MutationResult result = null;
		
		
		//create folder
		if(printData)
			System.out.print("Creating mutant folder " + outputPath + "... ");
		
		//Delete old folder and make new one
		boolean deleted = deleteFolder(outputPath);
		//Wait a moment for windows to catch up
		if(deleted)
			sleep(1000);		
		//create a new directory
		(new File(outputPath)).mkdirs();
		
		
		//Copy source files
		copyFolder(classPath + "/");

		if(printData)
			System.out.println("Done!");
		//End create folder
		
		
		//Generate Mutant
		if(printData)
			System.out.print("Performing mutation on " + outputPath + outputFile + "... ");
		
		//add linebreak if we are showing a list of expressions for formatting
		if(printExpressions)
			System.out.println("");
		
		Mutator m = new Mutator(code);
		
		try
		{
			result = m.mutate(preferredExpression, preferredChange, preferredMutation);
			
			result.text = outputFile + ": " + result.text;
			
			if(printNewCode)
				System.out.println("NEW CODE:\n" + result.code +"\n\n");
			
			File dir = new File(outputPath);
			
			FileWriter writer = new FileWriter(outputPath + outputFile);
			writer.write(result.code);
			writer.flush();
			writer.close();
		}catch(Exception ex)
		{
			System.err.println("Failed! Mutate threw an exception: ");	
			ex.printStackTrace();
			System.exit(-1);
		}
		
		if(printData)
			System.out.println("Done!");
		
		if(printData)
			System.out.println(result.text);
		//End generate mutant
		
		
	
		//Compile
		if(printData)
			System.out.print("Compiling " + outputPath + outputFile + "... ");

		try{		
			String command = "javac -cp \"" + classPath + "/*;" + outputPath + ";\" " + outputPath + "*.java";
			if(printCommands)
				System.out.println(command);
			Process compile = Runtime.getRuntime().exec(command);
			compile.waitFor(compileTimeout, TimeUnit.SECONDS);
			
			//Gather error message(s)
			String error = "";
			byte[] b = new byte[1024];
			int read = 0;
			do {
				read = compile.getErrorStream().read(b);
				error += new String(b);
			}while(read > 0);
			/*
			if(error.contains("error"))
				System.out.println("Error:\n" + error.trim());*/
		}catch(IOException ex)
		{
			System.err.println("Could not read mutant file during compilation: " + outputPath + outputFile);
			System.exit(-1);
		}catch(InterruptedException ex)
		{
			System.err.println("Interrupted while compiling mutant file: " + outputPath + outputFile);
			System.exit(-1);
		}
		
		if(printData)
			System.out.println("Done!");
		//End compile
		
		
		
		//Run Test
		if(printData)
			System.out.print("Testing " + outputPath + outputFile + "... ");

		try{		
			File resultDir = new File(resultPath);
			if(!resultDir.exists())
				resultDir.mkdir();
			
			String resultFile = outputPath.substring(outputPath.substring(0, outputPath.length() - 1).lastIndexOf("/") + 1);
			resultFile = resultFile.substring(0, resultFile.length() - 1)+ ".txt";
			
			String mutationString = outputFile;
			if(result.text != null)
				mutationString = result.text;
			
			String command = "java -cp \"" + classPath + "/*;" + outputPath + ";\" RunTest " + resultPath+"/"+resultFile + " \"" + mutationString + "\"";
			if(printCommands)
				System.out.println(command);
			Process compile = Runtime.getRuntime().exec(command);
			compile.waitFor(testTimeout, TimeUnit.SECONDS);
			
			//Gather test output
			String output = "";
			byte[] b = new byte[1024];
			int read = 0;
			do {
				read = compile.getInputStream().read(b);
				output += new String(b);
			}while(read > 0);
			
			//Pass or fail
			if(output.contains("Pass"))
			{
				passed = true;
			}
			else if(output.contains("Fail"))
			{
				
			}else{
				System.out.println("Unexpected output from test: " + output.trim());
			}
			
			//Gather error message(s)
			String error = "";
			b = new byte[1024];
			read = 0;
			do {
				read = compile.getErrorStream().read(b);
				error += new String(b);
			}while(read > 0);
			
			//Display errors
			if(error.contains("error"))
				System.out.println("Error:\n" + error.trim());
			
		}catch(IOException ex)
		{
			System.err.println("Could not read mutant file during compilation: " + outputPath + outputFile);
			System.exit(-1);
		}catch(InterruptedException ex)
		{
			System.err.println("Interrupted while compiling mutant file: " + outputPath + outputFile);
			System.exit(-1);
		}
		
		if(printData)
			System.out.println("Done!");
		//End run test
		Date endTime = new Date();
		runTime = (float)((endTime).getTime() - startTime.getTime()) / 1000.0f;

		notifyListeners();
	}
	
	//Helper method to copy the src directory
	//Skip jars
	public void copyFolder(String directory){
		copyFolder(directory, "");
	}
	
	public void copyFolder(String directory, String subdirectory){
		if(subdirectory == null)
			subdirectory = "";
		
		File folder = new File(directory);
		File[] contents = folder.listFiles();
		for(int i = 0; i < contents.length; i++)
		{
			
			try {
				if(contents[i].isDirectory())
				{
					File newDir = new File(outputPath + subdirectory + contents[i].getName());
					newDir.mkdir();
					copyFolder(contents[i].getPath(), subdirectory + contents[i].getName() + "/");
				}
				else if(!(contents[i].getName().contains(".jar")))
				{
					//System.out.println("Copying " + outputPath + subdirectory + contents[i].getName() + "...");
					Files.copy(Paths.get(contents[i].getPath()), Paths.get(outputPath + subdirectory + contents[i].getName()), StandardCopyOption.REPLACE_EXISTING);
				}
				} catch (IOException e) {
				System.err.println("Failed! File could not be copied: " + contents[i].getName() + "\n" +
						e.toString());
			}
		}
	}
	
	//Helper method to delete a directory recursively
	public static boolean deleteFolder(String directory) {
		File path = new File(directory);
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	        	//System.out.println("Deleting " + files[i] + "...");
	        	
	            if (files[i].isDirectory()) {
	            	deleteFolder(files[i].getPath());
	            } else {
	                files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
	}
	
	//Sleep
	public static void sleep(int milli)
	{
		try {
			Thread.sleep(milli);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
	
	
	public final void addListener(final ThreadCompleteListener listener) {
		listeners.add(listener);
	}
	public final void removeListener(final ThreadCompleteListener listener) {
		listeners.remove(listener);
	}
	private final void notifyListeners() {
		for (ThreadCompleteListener listener : listeners) {
			listener.notifyOfThreadComplete(this);
		}
	}
}
