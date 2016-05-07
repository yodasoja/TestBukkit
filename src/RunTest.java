/*
Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016
*/


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

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

public class RunTest {

	//args[0] = file name
	//args[1-n] = mutation text to be placed in file (optional)
	public static void main(String[] args) {
		if(args.length < 1)
		{
			System.out.println(help());
			System.exit(-1);
		}
		
		String mutationText = "";		
		for(int i = 1; i < args.length; i++)
		{
			mutationText += args[i] + " ";	
		}
		mutationText.trim();
		
		//Trim off quotes
		if(mutationText.substring(0,1).equals("\""))
			mutationText = mutationText.substring(1);
		
		if(mutationText.substring(mutationText.length() - 1).equals("\""))
			mutationText = mutationText.substring(0, mutationText.length() - 1);

		
		//Test
		try{
			File file = new File(args[0]);
			FileWriter writer = new FileWriter(file);
			
			writer.write((new Date()).toString() + "\n");
			writer.write("\n");
			writer.write(mutationText+"\n");			
			writer.write("\n");
			writer.write("Testing mutant... ");
			
			Result result = null;
			try{
				result = JUnitCore.runClasses(TestAll.class);
			}catch(Exception ex)
			{
				System.out.println("An exception has been thrown while testing:");
				ex.printStackTrace();
			}

			if(result == null)
			{
				writer.write("An exception has been thrown while testing, therefore it failed!");
				writer.flush();
				writer.close();
				return;
			}
			
			
			writer.write("Done (" + ((float)result.getRunTime() / 1000) + " sec). Out of " + result.getRunCount() + " tests, ");			
			List<Failure> fails = result.getFailures();

			if(fails.size() > 0)
			{
				writer.write(result.getFailureCount() + " failed:\n");

				for(int i = 0; i < fails.size(); i++)
				{
					writer.write(fails.get(i).getTestHeader() + ": " + fails.get(i).getMessage() + "\n");
				}
				
				System.out.println("Fail");
			} else
			{
				writer.write("all passed!\n");
				System.out.println("Pass");
			}
			
			writer.flush();
			writer.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String help() {
		return "How to use RunTest:\n" + 
				"	java RunTest outputFile [mutation]\n"+
				"\n"+
				"where	outputFile = name of file to which to save the test results\n"+
				"		mutation = textual representation of the mutation that was made (blank by default).\n";
	}

}
