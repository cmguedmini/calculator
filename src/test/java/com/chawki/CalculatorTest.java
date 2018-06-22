package com.chawki;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.chawki.service.Calculator;

public class CalculatorTest {
	private Calculator calculator = new Calculator();
	
	@Test
	public void testSum() {
		assertEquals(5, calculator.sum(2, 3));
	}

}
