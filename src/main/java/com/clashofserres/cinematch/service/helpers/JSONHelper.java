package com.clashofserres.cinematch.service.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONHelper {

	private static final Pattern JSON_PATTERN =
			Pattern.compile("\\{[\\s\\S]*}", Pattern.MULTILINE);

	public static String extractJsonObject(String raw) {
		if (raw == null || raw.isBlank()) {
			throw new RuntimeException("Model output is empty or null.");
		}

		raw = raw
				.replace("```json", "")
				.replace("```", "")
				.trim();

		if (!raw.endsWith("}")) {
			raw = raw + "}";
		}

		Matcher matcher = JSON_PATTERN.matcher(raw);

		if (!matcher.find()) {
			throw new RuntimeException("No JSON object found in output.");
		}


		return matcher.group(0).trim();
	}

	public static String normalize(String json) {
		// Now normalization is trivial:
		// Only remove trailing commas & clean markdown.
		json = json.replaceAll(",\\s*}", "}");
		json = json.replaceAll(",\\s*]", "]");
		return json.trim();
	}
}
