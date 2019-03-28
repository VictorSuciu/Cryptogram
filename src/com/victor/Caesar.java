package com.victor;

import java.util.ArrayList;
import java.util.HashMap;


public class Caesar {
	
	static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
			   'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	static int bestKey = 0;
	static int maxFoundWords = 0;
	
	
	static HashMap<String, String> dictionaryMap = new HashMap();
	static HashMap<Character, Integer> charToInt = new HashMap();
	static HashMap<Integer, Character> intToChar = new HashMap();
	
	static ArrayList<ArrayList<String>> cryptogram = new ArrayList();
	static ArrayList<ArrayList<String>> testCryptogram = new ArrayList();
	
	public static void tryCaesars(ArrayList<ArrayList<String>> list) {
		bestKey = 0;
		maxFoundWords = 0;
		cryptogram = list;
		buildCharToInt();
		buildIntToChar();
		int count = 0;
		
		for(int i = 0; i <= 25; i++) {
			cryptogram = tryKey(1, cryptogram);
			count++;
			checkKey(count);
		}
		cryptogram = tryKey(bestKey, cryptogram);
	}
	public static void buildCharToInt() {
		for(int i = 0; i < alphabet.length; i++) {
			charToInt.put(alphabet[i],  i);
		}
	}
	public static void buildIntToChar() {
		for(int i = 0; i < alphabet.length; i++) {
			intToChar.put(i,  alphabet[i]);
		}
	}
	public static void giveDictionary(HashMap<String, String> map) {
		dictionaryMap = map;
	}
	
	public static ArrayList<ArrayList<String>> tryKey(int key, ArrayList<ArrayList<String>> gram) {
		String word = "";
		int charIndex = 0;
		for(int i = 0; i < gram.size(); i++) {
			for(int j = 0; j < gram.get(i).size(); j++) {
				for(int n = 0; n < gram.get(i).get(j).length(); n++) {
					
					if(charToInt.containsKey(gram.get(i).get(j).charAt(n))) {
						
						charIndex = charToInt.get(gram.get(i).get(j).charAt(n));
						if(charIndex <= 25) {
							word = word + intToChar.get((charIndex + key) % 26);
						}
						else {
							word = word + intToChar.get(((charIndex + key) % 26) + 26);
						}
					}
					else {
						word = word + gram.get(i).get(j).charAt(n);
					}
				}
				gram.get(i).remove(j);
				gram.get(i).add(j, word);
				word = "";
			}
		}
		return gram;
	}
	public static void checkKey(int key) {
		String word = "";
		int foundWordCount = 0;
		for(int i = 0; i < cryptogram.size(); i++) {
			for(int j = 0; j < cryptogram.get(i).size(); j++) {
				try {
					word = dictionaryMap.get(cryptogram.get(i).get(j).toLowerCase());
					if(word != null) {
						foundWordCount++;
					}
					
				}
				catch(Exception e) {
					
				}
			}
		}
		if(foundWordCount > maxFoundWords) {
			maxFoundWords = foundWordCount;
			bestKey = key;
		}
	}
}
