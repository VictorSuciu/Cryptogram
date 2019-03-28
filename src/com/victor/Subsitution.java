package com.victor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Subsitution {
	static int[] ticks;
	static boolean done = false;
	static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
			   'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	static char[][] gramProgress;
	static HashMap<Character, Character> keyAlphabet = new HashMap();
	static HashMap<Character, Character> reverseAlphabet = new HashMap();

	static ArrayList<ArrayList<Character>> foundChars = new ArrayList();
	static ArrayList<ArrayList<Character>> reverseFoundChars = new ArrayList();
	static ArrayList<ArrayList<String>> ret = new ArrayList();

	//static ArrayList<ArrayList<Integer>> key = new ArrayList();//
	static ArrayList<ArrayList<Integer>> duplicates = new ArrayList();//
	//static ArrayList<Integer> temp = new ArrayList();//
	static ArrayList<Character> difChars = new ArrayList();//
	
	static HashMap<Character, Integer> charToInt = new HashMap();
	static HashMap<Integer, Character> intToChar = new HashMap();
	static HashMap<String, String[]>[] mapList = new HashMap[100];
	static HashMap<String, String> dictionaryMap = new HashMap();
	
	static String[] sortedGram;
	static int[] originalIndexes;
	static ArrayList<ArrayList<String>> cryptogram;

	static File serFile = new File("Serialized_WordMap.txt");
	static Scanner readSerFile;
	
	public static void giveDictionary(HashMap<String, String> map) {
		dictionaryMap = map;
	}
	public static void buildWordMap() throws IOException{
		for(int i = 0; i < mapList.length; i++) {
			//wordMap = new HashMap();
			mapList[i] = new HashMap<String, String[]>();
		}
		
		String word = "";
		String line = "";
		int count = 0;
		int maxLength = 50;
		readSerFile = new Scanner(serFile);

		//Read through serialized file and adds hashmap
		//key and value for each word entry
		while(readSerFile.hasNext()) {
			//line: The current line in the file - either a word (hashmap value entry),
			//hashmap key, or entry separator character (&)
			line = readSerFile.nextLine();

			//If reached entry separator character, it is now at a new
			//word. Sets word to the next word
			if(line.equals("&")) {
				word = readSerFile.nextLine();
			}
			//Only adds words up to a certain length due to
			//memory limitations. Longer words have
			//quadratically more keys
			if(word.length() <= maxLength) {
				if(mapList[word.length()].get(line) == null) {
					String[] value = {word};
					mapList[word.length()].put(line, value);
					value = null;
				}
				else {
					String[] temp = mapList[word.length()].get(line);
					String[] value = new String[temp.length + 1];
					for(int i = 0; i < temp.length; i++) {
						value[i] = temp[i];
					}
					value[temp.length] = word;
					//System.out.println("Add " + value);
					mapList[word.length()].put(line, value);
					value = null;
					temp = null;
				}
			}
			
			if(count % 200000 == 0) {
				System.out.println(count);
			}
			count++;
		}
		System.out.println(count);
	}
	public static void findChars(ArrayList<ArrayList<String>> cg) {
		
	}
	public static void trySubsitution(ArrayList<ArrayList<String>> cg) {
		gramProgress = null;
		sortedGram = null;
		done = false;
		cryptogram = cg;
		ret.clear();
		keyAlphabet.clear();
		reverseAlphabet.clear();
		foundChars.clear();
		reverseFoundChars.clear();
		sort();
		ticks = new int[sortedGram.length];
		gramProgress = new char[sortedGram.length][];
		int count = 0;
		for(String s : sortedGram) {
			boolean foundTick = false;
			for(int i = 0; i < s.length() - 1; i++) {
				if(charToInt.get(s.charAt(i)) == null) {
					foundTick = true;
				}
			}
			/*
			if(s.length() >= 3 && s.charAt(s.length() - 3) == '.') {
				gramProgress[count] = sortedGram[count].substring(0, sortedGram[count].length() - 3).toCharArray();
				sortedGram[count] = s.substring(0, s.length() - 3);
			}
			else {
			*/
				gramProgress[count] = (charToInt.get(s.charAt(s.length() - 1)) == null) ? 
						sortedGram[count].substring(0, sortedGram[count].length() - 1).toCharArray() : 
						sortedGram[count].toCharArray();
			//}
			if(foundTick == true) {
				gramProgress[count] = new char[gramProgress[count].length - 1];
				int tIndex = 0;
				int add = 0;
				for(int i = 0; i < gramProgress[count].length; i++) {
					if(charToInt.get(s.charAt(i)) != null) {
						gramProgress[count][i - add] = s.charAt(i);
					}
					else {
						add++;
						ticks[count] = i;
						tIndex = i;
					}
				}
				sortedGram[count] = s.substring(0, tIndex) + s.substring(tIndex + 1, s.length());
			}
			count++;
			//System.out.print(s + " ");
		}
		//System.out.println();
		decrypt(0);
		if(done == true) {
			//System.out.println("Done!");
			reorderGram();
		}
		ticks = null;
	}
	public static void decrypt(int index) {
		String key = generateKey(sortedGram[index], index);
		if(foundChars.size() < sortedGram.length) {
			foundChars.add(new ArrayList<Character>());
			reverseFoundChars.add(new ArrayList<Character>());
		}
		if(mapList[gramProgress[index].length].get(key) == null) {
			if(index < sortedGram.length - 1) {
				decrypt(index + 1);
			}
		}
		else {
			String[] wordList = mapList[gramProgress[index].length].get(key);
			
			int count = 0;
			for(String word : wordList) {
				count++;
				resetFoundLetters(index);
				boolean skip = false;
				for(int i = 0; i < word.length(); i++) {
					if((reverseAlphabet.get(word.charAt(i)) != null && reverseAlphabet.get(word.charAt(i)) != sortedGram[index].charAt(i)) 
							|| (keyAlphabet.get(sortedGram[index].charAt(i)) != null && keyAlphabet.get(sortedGram[index].charAt(i)) != word.charAt(i))) 
					{
						skip = true;
						break;
					}
				}
				if(skip == false) {
					addFoundLetters(index, word);
					applyFoundLetters(index);
					
					if(index < sortedGram.length - 1) {
						decrypt(index + 1);
						
					}
					else if(isComplete() == true) {
						done = true;
						break;
					}
					
				}
				if(done == true) {
					break;
				}
			}
		}
		key = null;
	}
	public static void resetFoundLetters(int index) {
		for(int i = index; i < foundChars.size(); i++) {
			for(int j = 0; j < foundChars.get(i).size(); j++) {
				keyAlphabet.remove(reverseFoundChars.get(i).get(j));
				reverseAlphabet.remove(foundChars.get(i).get(j));
			}
			
			foundChars.get(i).clear();
			reverseFoundChars.get(i).clear();
		}
	}
	public static void applyFoundLetters(int index) {
		for(int i = (index < sortedGram.length - 1) ? index : 0; i < gramProgress.length; i++) {
			for(int j = 0; j < gramProgress[i].length; j++) {
				if(keyAlphabet.get(sortedGram[i].charAt(j)) != null) {
					gramProgress[i][j] = keyAlphabet.get(sortedGram[i].charAt(j));
				}
			}
		}
	}
	public static void addFoundLetters(int index, String word) {
		for(int i = 0; i < word.length(); i++) {
			if(keyAlphabet.get(sortedGram[index].charAt(i)) == null) {
				keyAlphabet.put(sortedGram[index].charAt(i), word.charAt(i));
				foundChars.get(index).add(word.charAt(i));
				
			}
			if(reverseAlphabet.get(word.charAt(i)) == null) {
				reverseAlphabet.put(word.charAt(i), sortedGram[index].charAt(i));
				reverseFoundChars.get(index).add(sortedGram[index].charAt(i));
			}
		}
		
	}
	public static String generateKey(String word, int index) {
		String key = "";
		duplicates = getRepeatChars(word);
		difChars.clear();
		boolean repeatedChar = false;
		for(int i = 0; i < word.length(); i++) {
			
			for(char c : difChars) {
				if(c == word.charAt(i)) {
					repeatedChar = true;
				}
			}
			if(repeatedChar == false) {
				difChars.add(word.charAt(i));
			}
			repeatedChar = false;
		}
		for(char c : difChars) {
			if(keyAlphabet.get(c) != null) {
				key += "-" + charToInt.get(keyAlphabet.get(c));
				for(int z = 0; z < word.length(); z++) {
					if(word.charAt(z) == c) {
						key += "." + z;
					}
				}
			}
		}
		if(duplicates.size() > 0) {
			key += "|";
			for(ArrayList<Integer> d : duplicates) {
				key += "-";
				for(int w = 0; w < d.size(); w++) {
					key += (w > 0) ? "." + d.get(w): d.get(w);
				}
			}
		}
		if(key.equals("")) {
			return "-all" + ((ticks[index] > 0) ? "|-c" + ticks[index] : "");
		}
		if(ticks[index] != 0) {
			key += "|-c" + ticks[index];
		}
		return key;
	}
	public static boolean isComplete() {
		double count = sortedGram.length;
		boolean known = true;
		int index = 0;
		int index2 = 0;
		for(char[] ch : gramProgress) {
			String s = "";
			for(char c : ch) {
				s += c;
				if(keyAlphabet.get(sortedGram[index2].charAt(index)) == null) {
					known = false;
				}
				index++;
			}
			index = 0;
			index2++;
			if(dictionaryMap.get(s) == null) {
				if(known == false) {
					return false;
				}
				else {
					count -= 1.0;
				}
			}
			
			s = null;
		}
		if(count / (double)sortedGram.length >= ((double)sortedGram.length - 1.0) / (double)sortedGram.length) {
			return true;	
		}
		return false;
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
	public static void sort() {
		int totalLength = 0;
		int tempNum = 0;
		int i2 = 0;
		for(int i = 0; i < cryptogram.size(); i++) {
			int j = cryptogram.get(i).size() - 1;
			while(j >= 0) {
				String s = cryptogram.get(i).get(j);
				if(s.length() != 1 || (s.length() == 1 && charToInt.get(s.charAt(0)) != null)) {
					totalLength += 1;
				}
				else {
					//System.out.println(s);
					cryptogram.get(i).remove(j);
				}
				j--;
			}
			
		}
		//System.out.println();
		//System.out.println(cryptogram);
		
		sortedGram = new String[totalLength];
		originalIndexes = new int[totalLength];
		
		for(int i = 0; i < originalIndexes.length; i++) {
			originalIndexes[i] = i;
		}
		int index = 0;
		for(ArrayList<String> list : cryptogram) {
			for(String s : list) {
				sortedGram[index] = s.toLowerCase();
				index++;
			}
		}
		for(int i = 0; i < sortedGram.length - 1; i++) {
			int minLength = Integer.MAX_VALUE;
			int minIndex = 0;
			String temp = "";
			for(int j = i; j < sortedGram.length; j++) {
				if(sortedGram[j].length() < minLength) {
					minLength = sortedGram[j].length();
					minIndex = j;
				}
			}
			
			temp = sortedGram[i];
			tempNum = originalIndexes[i];
			sortedGram[i] = sortedGram[minIndex];
			originalIndexes[i] = originalIndexes[minIndex];
			sortedGram[minIndex] = temp;
			originalIndexes[minIndex] = tempNum;
		}
		
		for(int i = 0; i < sortedGram.length - 1; i++) {
			int maxLetters = Integer.MIN_VALUE;
			int maxIndex = 0;
			int count = 0;
			String word = sortedGram[i];
			boolean repeatedChar = false;
			ArrayList<Character> tempChars = new ArrayList();
			for(int j = 0; j < word.length(); j++) {
				
				for(char c : tempChars) {
					if(c == word.charAt(j)) {
						repeatedChar = true;
					}
				}
				if(repeatedChar == false) {
					tempChars.add(word.charAt(j));
				}
				repeatedChar = false;
			}
			
			for(int j = i + 1; j < sortedGram.length; j++) {
				for(int z = 0; z < sortedGram[j].length(); z++) {
					for(char c : tempChars) {
						if(c == sortedGram[j].charAt(z) && charToInt.get(c) != null) {
							count++;
						}
					}
				}
				if(count > maxLetters) {
					maxLetters = count;
					maxIndex = j;
				}
				count = 0;
			}
			
			word = sortedGram[i + 1];
			tempNum = originalIndexes[i + 1];
			sortedGram[i + 1] = sortedGram[maxIndex];
			originalIndexes[i + 1] = originalIndexes[maxIndex];
			sortedGram[maxIndex] = word;
			originalIndexes[maxIndex] = tempNum;
			tempChars = null;
		}
	}
	
	public static void reorderGram() {
		int count = 0;
		ret.clear();
		String[] reorderedGramArr = new String[sortedGram.length];
		
		for(int i = 0; i < sortedGram.length; i++) {
			String s = "";
			for(char c : gramProgress[i]) {
				s += c;
			}
			if(gramProgress[i].length < sortedGram[i].length()) {
				s += sortedGram[i].charAt(sortedGram[i].length() - 1);
			}
			if(ticks[i] > 0) {
				s = s.substring(0, ticks[i]) + "'" + s.substring(ticks[i], s.length());
			}
			reorderedGramArr[originalIndexes[i]] = s;
			
			s = null;
		}
		for(int i = 0; i < cryptogram.size(); i++) {
			ret.add(new ArrayList<String>());
			for(int j = 0; j < cryptogram.get(i).size(); j++) {
				ret.get(i).add(reorderedGramArr[count]);
				//System.out.print(reorderedGramArr[count] + " ");
				count++;
			}
		}
		//System.out.println();
		//System.out.println();
	}
		
	public static ArrayList<ArrayList<Integer>> getRepeatChars(String word) {
		HashMap<Character, ArrayList<Integer>> hm = new HashMap();
		ArrayList<Integer> list;
		ArrayList<Character> retChars = new ArrayList();
		for(int i = 0; i < word.length(); i++) {
			if(hm.get(word.charAt(i)) == null) {
				list = new ArrayList();
				list.add(i);
			}
			else {
				list = hm.get(word.charAt(i));
				list.add(i);
				if(list.size() == 2) {
					retChars.add(word.charAt(i));
				}
			}
			hm.put(word.charAt(i), list);
		}
		ArrayList<ArrayList<Integer>> ret = new ArrayList();
		for(char c : retChars) {
			ret.add(hm.get(c));
		}
		return ret;
	}
	
}
