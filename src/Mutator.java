/*
Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016
*/


import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Mutator
{
	
	///////////////////
	//Class Variables//
	///////////////////
	
	//Original source code
	String content; 
	
	//List of infix expressions that can be changed
	public ArrayList<InfixExpression> list;
	
	//last change to occur
	public int lastIndexMutated;
	
	//Root of the statement tree
	CompilationUnit astRoot;
	
	//print expressions in list
	public static boolean printExpressions = GenerateMutants.printExpressions;
	
	//Comparative Operators to exchange
	InfixExpression.Operator[] coperators = { InfixExpression.Operator.EQUALS,
											 InfixExpression.Operator.NOT_EQUALS,
											 InfixExpression.Operator.LESS, 
											 InfixExpression.Operator.LESS_EQUALS,
											 InfixExpression.Operator.GREATER,
											 InfixExpression.Operator.GREATER_EQUALS
											};
	
	//Logical Operators to exchange
	InfixExpression.Operator[] loperators = { InfixExpression.Operator.CONDITIONAL_AND,
												InfixExpression.Operator.CONDITIONAL_OR
											};
	
	//Math Operators to exchange
	InfixExpression.Operator[] moperators = { InfixExpression.Operator.PLUS, 
												InfixExpression.Operator.MINUS,
												InfixExpression.Operator.TIMES,
												InfixExpression.Operator.DIVIDE
											};
	
	//Bitwise Operators to exchange (Don't use because they can break things)
	InfixExpression.Operator[] boperators = { InfixExpression.Operator.XOR, 
											 	InfixExpression.Operator.AND,
											 	InfixExpression.Operator.OR,
											};
	

	
	//Operators to avoid (because we didn't handle them)
	InfixExpression.Operator[] noperators = { InfixExpression.Operator.REMAINDER, 
											 InfixExpression.Operator.LEFT_SHIFT,
											 InfixExpression.Operator.RIGHT_SHIFT_SIGNED,
											 InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED
											};
	
	
	//List of all the operator lists (except noperators and boperators)
	InfixExpression.Operator[][] opList = {	coperators,
											loperators,
											moperators/*,
											boperators*/
										};
			
	///////////
	//Methods//
	///////////
	
	//Constructor
	public Mutator(String code){
		content = code;
		list = new ArrayList<InfixExpression>();	
		lastIndexMutated = -1;
		
		//Compiler
		Map options = JavaCore.getOptions();
		String highest = (String)options.get(JavaCore.COMPILER_SOURCE);
		if("1.5".compareTo(highest) > 0)
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5); //or newer version

		//Rewriter
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setCompilerOptions(options);
		astRoot = (CompilationUnit) parser.createAST(null);
		
		gatherCandidates();
	}
	
	//Main method
	public static void main(String args[]) throws Exception {
		    
		String filename = "src/TestMain.java";
		String code = "";
		
		FileInputStream file = new FileInputStream(filename);
		byte[] bytes = new byte[2048];
		while(file.available() > 0)
		{
			int read = file.read(bytes);
			
			code += new String(bytes, 0, read, StandardCharsets.UTF_8);
		}
		file.close();
		
		Mutator m = new Mutator(code);
		
		m.mutate();
		
		//m.instrumentation(content);
	}

	//Read through the source code to gather infix expressions into list
	public void gatherCandidates()
	{

		// iterate over all types
		Object[] types = astRoot.types().toArray();
		for (int t = 0; t < astRoot.types().size(); t++) {
			if(types[t] instanceof EnumDeclaration)
			{
				//Get the class/interface
				EnumDeclaration type = (EnumDeclaration)types[t];

				List<BodyDeclaration> body = type.bodyDeclarations();
				
				//Loop through the methods
				for(int b = 0; b < body.size(); b++)
				{
					if(body.get(b) instanceof MethodDeclaration)
					{
						MethodDeclaration md = (MethodDeclaration)body.get(b);
						
						recurseStatement(md.getBody());
					}
				}
			}			
			else if(types[t] instanceof TypeDeclaration)
			{
				//Get the class/interface
				TypeDeclaration type = (TypeDeclaration)types[t];

				MethodDeclaration[] methods = type.getMethods();
				
				//Loop through the methods
				for(int m = 0; m < methods.length; m++)
				{
					//Get the name and body of the method
					SimpleName name = methods[m].getName();
					Block body = methods[m].getBody();
					//List<Statement> statements = body.statements();

					recurseStatement(body);	
				}
			}			
		}
	}
	
	//Helper method used by gatherCandidates
	public void recurseStatement(Statement statement)
	{
		if(statement == null)
			return;
		
		//System.out.println(statement.getClass());
		
		//Opening block statement...
		if(statement instanceof Block)
		{
			List<Statement> statements = ((Block)statement).statements();
			for(int s = 0; s < statements.size(); s++)
			{				
				Statement line = statements.get(s);
				recurseStatement(line);							
			}
		} 
		//Opening if statement...
		else if(statement instanceof IfStatement)
		{
			//Add its conditional to the list
			IfStatement ifS = (IfStatement)statement;
			
			recurseExpression(ifS.getExpression());
			
			//Body of the IF
			recurseStatement(ifS.getThenStatement());
			
			//Check for an else block...
			recurseStatement(ifS.getElseStatement());
		}
		//Opening return statement...
		else if(statement instanceof ReturnStatement)
		{
			recurseExpression(((ReturnStatement)statement).getExpression());
		}
		//Opening an expression statement... (which could nearly be anything)
		else if(statement instanceof ExpressionStatement)
		{
			recurseExpression(((ExpressionStatement)statement).getExpression());
		}
	}
	
	//Helper method used by recurseStatement
	public void recurseExpression(Expression expr)
	{

		if(expr instanceof InfixExpression)
		{
			InfixExpression e = (InfixExpression)expr;
			
			list.add(e);
			
			if(printExpressions)
				System.out.println("    " + e);
		} else if(expr instanceof ConditionalExpression)
		{
			Expression cexpr = ((ConditionalExpression)expr).getExpression();
			
			recurseExpression(cexpr);
		} else if(expr instanceof PrefixExpression)
		{
			recurseExpression(((PrefixExpression)expr).getOperand());
		}
	}
	
	//Randomly mutate one expression in list
	public MutationResult mutate() throws MalformedTreeException, BadLocationException, Exception
	{
		return mutate(null, null, null);
	}
	
	//Mutate one expression in list
	//	Integer preferredExpression -> 0 ... n - 1 expressions, null = random
	//  Integer preferredChange -> 0 = change constant, 1 = change operator, null = random
	//  Integer preferredMutation -> Mutation input (constant for change 0, operator selection for change 1), null = random
	public MutationResult mutate(Integer preferredExpression, Integer preferredChange, Integer preferredMutation) 
			throws MalformedTreeException, BadLocationException, Exception
	{
		//Output
		MutationResult result = new MutationResult();

		if(list.size() == 0)
			throw new Exception("List has gathered no expressions.");
		
		// create a ASTRewrite
		ASTRewrite rewriter = ASTRewrite.create(astRoot.getAST());
		
		//This keeps the loop from running infinitely
		//because it is set to false if the user manual specifies a change
		//(which can greatly limit the random potential of it all)
		//Note that when running randomly, we assume that at least one expression
		//can be changed given the changes below!
		boolean randomlyGenerating = true;
		while(randomlyGenerating && result.text == null)
		{
			//Get expression to mutate
			int index;
			if(preferredExpression != null)
			{
				index = Math.floorMod(preferredExpression, list.size());	
				randomlyGenerating = false;
			}
			else
				index = (int)(Math.random() * list.size());

			InfixExpression e = (InfixExpression)list.get(index);	

			//Total number of changes
			int total = 2; //SET THIS MANUALLY
			
			//Change constant or change operator
			int pickChange;
			if(preferredChange != null)
			{
				pickChange = Math.floorMod(preferredChange, total);
				randomlyGenerating = false;
			}
			else
				pickChange = (int)(Math.random() * total);

			//Try changes starting at a random place until one works.
			//If none apply, jump back and pick a new expression.
			int startingPoint = pickChange;

			do
			{
				//change constant
				if(pickChange == 0)
				{
					Expression l = e.getLeftOperand();
					Expression r = e.getRightOperand();


					//Get constant mutation
					int randomNum;
					if(preferredMutation != null)
					{
						randomNum = preferredMutation;	
						randomlyGenerating = false;
					}
					else
						//randomNum = (int)(Math.random() * Integer.MAX_VALUE) - Integer.MAX_VALUE/2;
						randomNum = (int)(Math.random() * 200) - 100;

					NumberLiteral sub = e.getAST().newNumberLiteral();
					sub.setToken(randomNum + "");

					if(l instanceof NumberLiteral)
					{					
						//rewrite the statement
						rewriter.replace(l, sub, null);
						lastIndexMutated = index;
						System.out.println("Mutating left constant from expression " + (index+1) + " of " + list.size() + ": " + e + " to " + sub);					
						break;
					} else if (r instanceof NumberLiteral)
					{
						//rewrite the statement
						rewriter.replace(r, sub, null);
						lastIndexMutated = index;
						result.text = "Mutating right constant from expression " + (index+1) + " of " + list.size() + ": " + e + " to " + sub;
						break;
					} else {
						pickChange++;
						continue;
					}
				}


				//Change operator
				else if(pickChange == 1)
				{

					InfixExpression.Operator o = e.getOperator();

					//check that the operator CAN be changed without breaking the code
					//Save the operator list so similar ops can be subbed in
					InfixExpression.Operator[] opType = null;
					for(int list = 0; opType == null && list < opList.length; list++)
					{
						for(int i = 0; opType == null && i < opList[list].length; i++)
						{
							if(opList[list][i] == o)
							{
								opType = opList[list];
							}
						}
					}
					
					if(opType == null)
					{
						//jump back and try to change the next thing
						pickChange++;
						continue;
					}

					//Assuming the operator can be substituted
					InfixExpression.Operator subO = o;

					while(subO == o)
					{
						//Get operator mutation
						int randO;
						if(preferredMutation != null)
						{							
							randO = Math.floorMod(preferredMutation, opType.length);
							randomlyGenerating = false;
						}
						else
							randO = (int)(Math.random() * opType.length);

						subO = opType[randO];
					}		

					//Do the change			
					InfixExpression sub = e.getAST().newInfixExpression();
					sub.setLeftOperand((Expression) rewriter.createCopyTarget(e.getLeftOperand()));
					sub.setOperator(subO);
					sub.setRightOperand((Expression) rewriter.createCopyTarget(e.getRightOperand()));

					//rewrite the statement
					rewriter.replace(e, sub, null);
					lastIndexMutated = index;
					result.text = "Mutating operator from expression " + (index+1) + " of " + list.size() + ": " + e + " to " + subO;

					break;
				}
				
				//loop to try all changes
				if(pickChange == total)
					pickChange = 0;
				
			}while(pickChange != startingPoint);

		}
		
		// apply all the edits to the compilation unit
		Document document = new Document(content);
		TextEdit edits = rewriter.rewriteAST(document, null);
		//System.out.println("All Edits: " + edits);
		edits.apply(document);

		// this is the code with the substitution
		result.code = document.get();

		return result;
	}
	
	//Unused
	public void instrumentation() throws Exception {

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);

		// create a ASTRewrite
		AST ast = astRoot.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);

		// the original document
		Document document = new Document(content);
		
		// iterate over all types
		Object[] types = astRoot.types().toArray();
		for (int t = 0; t < astRoot.types().size(); t++) {
			if(types[t] instanceof TypeDeclaration)
			{
				//Get the class/interface
				TypeDeclaration type = (TypeDeclaration)types[t];

				MethodDeclaration[] methods = type.getMethods();
				
				//Loop through the methods
				for(int m = 0; m < methods.length; m++)
				{
					//Get the name and body of the method
					SimpleName name = methods[m].getName();
					Block body = methods[m].getBody();
					List<Statement> statements = body.statements();
					for(int s = 0; s < statements.size(); s++)
					{
						System.out.println("1: " + statements.get(s).toString());
						if(statements.get(s) instanceof IfStatement){
							System.out.println("2 - If: " + statements.get(s).toString());
							IfStatement ifS = (IfStatement)statements.get(s);
							
							
							InfixExpression e = (InfixExpression)ifS.getExpression();
							
							InfixExpression.Operator o = e.getOperator();
							System.out.println("2 - o: " + o.toString());
							if(o == Operator.LESS)
							{
								InfixExpression sub = e.getAST().newInfixExpression();

								sub.setLeftOperand((Expression) rewriter.createCopyTarget(e.getLeftOperand()));
								sub.setOperator(Operator.GREATER);
								sub.setRightOperand((Expression) rewriter.createCopyTarget(e.getRightOperand()));

								//Prepare to rewrite the statement
								rewriter.replace(e, sub, null);
							} else if(o == Operator.GREATER)
							{
								InfixExpression sub = e.getAST().newInfixExpression();

								sub.setLeftOperand((Expression) rewriter.createCopyTarget(e.getLeftOperand()));
								sub.setOperator(Operator.LESS);
								sub.setRightOperand((Expression) rewriter.createCopyTarget(e.getRightOperand()));

								//Prepare to rewrite the statement
								rewriter.replace(e, sub, null);								
							}
							else
							{
								
								Statement ifElse = ifS.getElseStatement();
								if(ifElse != null)
								{
									ifS = (IfStatement)((Block)ifElse).statements().get(s);
	
									e = (InfixExpression)ifS.getExpression();
								}
							}

							
						}							
					}

				}
			}
			
		}
		System.out.println("\n--------------------------------------------\n");
		// apply all the edits to the compilation unit
		TextEdit edits = rewriter.rewriteAST(document, null);
		System.out.println("All Edits: " + edits);
		edits.apply(document);

		// this is the code for adding statements
		System.out.println(document.get());
	}
	
	class MutationResult
	{
		public String code;
		public String text;
		
		public MutationResult()
		{
			code = null;
			text = null;
		}
		
		public MutationResult(String codeIn, String textIn)
		{
			code = codeIn;
			text = textIn;
		}
	}
}