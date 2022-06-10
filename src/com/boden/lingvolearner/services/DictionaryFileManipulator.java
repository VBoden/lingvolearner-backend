package com.boden.lingvolearner.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boden.lingvolearner.pojo.WordCard;

public class DictionaryFileManipulator {

	private static Pattern PATTERN_WITH_PERCENT = Pattern.compile("(.*?)\\|(\\[.*?\\])\\|(.*?)%");
	private static Pattern PATTERN_WITHOUT_PERCENT = Pattern.compile("(.*?)\\|(\\[.*?\\])\\|(.*?)$");

	public static List<WordCard> loadDictionary(InputStream inputStream) {
		List<WordCard> dictionary = new ArrayList<WordCard>();

		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Objects.requireNonNull(inputStream), "UTF8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			String dictString = stringBuilder.toString();

			addWordsToDictionary(dictionary, dictString, PATTERN_WITH_PERCENT);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dictionary;
	}

	public static List<WordCard> loadDictionaryByLines(InputStream inputStream) {
		List<WordCard> dictionary = new ArrayList<WordCard>();
		boolean linesWithPercent = false;
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Objects.requireNonNull(inputStream), "UTF8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("%") || linesWithPercent) {
					linesWithPercent = true;
					stringBuilder.append(line);
				} else {
					addWordsToDictionary(dictionary, line, PATTERN_WITHOUT_PERCENT);
				}
			}
			if (linesWithPercent) {
				addWordsToDictionary(dictionary, stringBuilder.toString(), PATTERN_WITH_PERCENT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dictionary;
	}

	private static void addWordsToDictionary(List<WordCard> dictionary, String line, Pattern pattern) {
		Matcher matcher = pattern.matcher(line);
		WordCard word;
		while (matcher.find()) {
			word = new WordCard(matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3).trim());
			dictionary.add(word);
		}
	}
}
