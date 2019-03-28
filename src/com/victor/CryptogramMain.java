package com.victor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.PrintWriter;

public class CryptogramMain {
	static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
					   'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	static int bestKey = 0;
	static int maxFoundWords = 0;
	static int crLength = 0;
	static String crNum = "";
	
	static File inFile;
	static File outFile;
	static File dictionaryFile;
	static PrintWriter writeOut;
	
	static boolean getByLine = true;
	
	static Scanner readIn;
	static Scanner readOut;
	static Scanner readDictionary;
	
	static HashMap<String, String> dictionaryMap;
	static HashMap<Character, Integer> charToInt;
	static HashMap<Integer, Character> intToChar;
	
	static ArrayList<ArrayList<String>> cryptogram;
	static ArrayList<ArrayList<String>> testCr;
	static Scanner cont = new Scanner(System.in);
	public static void main(String[] args) throws IOException {
		
		System.out.println("Start");
		
		
		outFile = new File("Output.txt");
		dictionaryFile = new File("Dictionary.txt");
		
		
		readOut = new Scanner(outFile);
		readDictionary = new Scanner(dictionaryFile);
		writeOut = new PrintWriter(outFile);
		
		dictionaryMap = new HashMap();
		charToInt = new HashMap();
		intToChar = new HashMap();
		
		cryptogram = new ArrayList();
		
		
		buildWordMap();
		buildCharToInt();
		Caesar.giveDictionary(dictionaryMap);
		Subsitution.giveDictionary(dictionaryMap);
		Caesar.buildCharToInt();
		Caesar.buildIntToChar();

		System.out.println("WordMap Start");
		Subsitution.buildCharToInt();
		Subsitution.buildIntToChar();
		Subsitution.buildWordMap();
		System.out.println("WordMap End");
		int count = 0;
		do {
			inFile = new File("Input.txt");
			readIn = new Scanner(inFile);
			while(readIn.hasNextLine()) {
				getNextGram();
				//System.out.println(cryptogram);
				crLength = getLength();
				if(crNum.length() > 0) {
					System.out.print(crNum.substring(0, crNum.length() - 1) + ". ");
				}
				
				//==============================================
				/*
				Caesar.tryCaesars(cryptogram);
				if(Caesar.maxFoundWords > (crLength / 3) * 2) {
					printGram(Caesar.cryptogram);
				}
				
				else {
				*/
					//System.out.println("Try Subsitution");
				//System.out.println(cryptogram);
					Subsitution.trySubsitution(cryptogram);
					if(Subsitution.ret.size() > 0) {
						printGram(Subsitution.ret);
					}
					else {
						System.out.println();
						printGram(null);
					}
				//}
				
				//==============================================
			} 
			System.out.println("End___________REMEMBER_TO_SAVE_INPUT_________________________________");
		} while(!cont.next().toLowerCase().equals("stop"));
		writeOut.close();
		
	}
	public static int getLength() {
		int l = 0;
		for(ArrayList<String> ar : cryptogram) {
			l += ar.size();
		}
		return l;
	}
	public static void buildWordMap() {
		String word = "";
		while(readDictionary.hasNext()) {
			word = readDictionary.next().toLowerCase();
			dictionaryMap.put(word, word);
		}
	}
	
	public static void getNextGram() {
		cryptogram.clear();
		boolean foundLine = false;
		String[] line = {};
		String stLine = "";
		String word = "";
		crNum = "";
		ArrayList<String> addLine;
		if(getByLine == false) {
			while(readIn.hasNextLine()) {
				stLine = readIn.nextLine();
				line = stLine.split(" ");
				if(!stLine.equals("")) {
					foundLine = true;
				}
				if(foundLine == true) {
					if(stLine.equals("")) {
						break;
					}
					else {
						addLine = new ArrayList();
						for(String s : line) {
							addLine.add(s);
						}
						cryptogram.add(addLine);
					}
				}
			}
		}
		else {
			while(readIn.hasNextLine()) {
				stLine = readIn.nextLine();
				if(!stLine.equals("")) {
					int index = 0;
					while(charToInt.get(stLine.charAt(index)) == null) {
						crNum += stLine.charAt(index);
						index++;
					}
					line = stLine.substring(index).split(" ");
					addLine = new ArrayList();
					boolean num = true;
					for(String s : line) {
						//if(s.charAt(0) != '	') {
							addLine.add(s);
						//}
						//else {
							//addLine.add(s.substring(1));
						//}
					}
					cryptogram.add(addLine);
					break;
				}
			}
		}
	}
	public static void printGram(ArrayList<ArrayList<String>> cr) {
		if(crNum.length() > 0) {
			writeOut.print(crNum + ". ");
		}
		if(cr != null) {
			for(int i = 0; i < cr.size(); i++) {
				for(int j = 0; j < cr.get(i).size(); j++) {
					writeOut.print(cr.get(i).get(j) + " ");
					System.out.print(cr.get(i).get(j) + " ");
					
				}
				writeOut.println();
				System.out.println();
			}
			writeOut.println();
			System.out.println();
		}
		else {
			writeOut.println("COULD NOT FIND SOLUTION");
			writeOut.println();
			
			System.out.println("COULD NOT FIND SOLUTION");
			System.out.println();
		}
	}
	public static void buildCharToInt() {
		for(int i = 0; i < alphabet.length; i++) {
			charToInt.put(alphabet[i],  i);
		}
	}
}
