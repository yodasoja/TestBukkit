/*
Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016
*/

import java.lang.annotation.*;
import demo.DemoAnnotation;
import demo.TypeGeneratingAnnotation;
import junit.framework.*;
public class TestDemo {
	
	//@DemoAnnotation(what = "spam")
	@TypeGeneratingAnnotation
	public String output(int input) {
		
		String output = null;
		
		int i = input;
		
		if(i < 0)
			output = "negative";
		else if (i > 0)
		{
			output = "positive";
			if(i == 1)
			{
				output = "positively 1";				
			}
		}
		
		return output;
	}

}
