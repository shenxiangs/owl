package com.test;

import org.apache.spark.api.java.function.Function2;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.Label;

public class lam {
	public static void main(String[] args) throws Exception {
		lam l = new lam();
		System.out.println(String.format("%s",l.sum.call(1, 1)));
	}
	private static Function2<Integer, Integer, Integer> sum = (x,y)-> x + y;
}
