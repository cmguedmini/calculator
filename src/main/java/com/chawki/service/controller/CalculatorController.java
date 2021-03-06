package com.chawki.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chawki.service.Calculator;

@RestController
public class CalculatorController {

	@Autowired
	private Calculator calculator;
	
	@RequestMapping("/sum")
	public String sum(@RequestParam("a") Integer a, @RequestParam("b") Integer b) {
		System.out.println("Test for RoamSmart Team");
		return String.valueOf(calculator.sum(a, b));
	}
}
