package com.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FIleParse {
	static String LOG_ENTRY_PATTERN =
		      // 1:IP  2:client 3:user 4:date time                   5:method 6:req 7:proto   8:respcode 9:size
		      "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+)";
	static Pattern pattern = Pattern.compile(LOG_ENTRY_PATTERN);;
	public static void main(String[] args) throws IOException {
		String str="D:/file/ac.txt";
		String str2="D:/file/acl.txt";
		String i=null;
		Matcher m;
		BufferedReader bu = new BufferedReader(new FileReader(str));
		BufferedWriter bw = new BufferedWriter(new FileWriter(str2));
		//System.out.println(LOG_ENTRY_PATTERN);
		 while((i = bu.readLine()) != null) {
			 m = pattern.matcher(i);
			 if (!m.find()) {
			      //logger.log(Level.ALL, "Cannot parse logline" + logline);
				 System.out.print("");
			    }
			 else{
				 bw.write(i+"\n");;
				 }
		  }
		 bu.close();
		 bw.close();
	}

}
