/*
Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016
*/


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JUnitTest extends TestCase {
	
	@Test
	public void testCheckOutputPos() {
		TestDemo test = new TestDemo();
		String output = test.output(2);
		junit.framework.Assert.assertEquals(output, "positive");
	}	
	
	@Test
	public void testCheckOutputNeg() {
		TestDemo test = new TestDemo();
		String output = test.output(-1);
		junit.framework.Assert.assertEquals(output, "negative");
	}
	
	@Test
	public void testCheckOutputOne() {
		TestDemo test = new TestDemo();
		String output = test.output(1);
		junit.framework.Assert.assertEquals(output, "positively 1");
	}
	
}
