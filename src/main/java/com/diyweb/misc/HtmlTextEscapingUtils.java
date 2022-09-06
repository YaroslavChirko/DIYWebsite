package com.diyweb.misc;

/**
 * Class to escape some of the characters to avoid injection or scripting attacks
 * @author erick
 *
 */
public class HtmlTextEscapingUtils {
	private static String[] nonEscaped = {"<", ">", "/", "\'", "\"", "`"};
	private static String[] escaped = {"&gt;", "&lt;", "&47;", "&#39;", "&quot;", "&#44;"};

	private static String escapeCharacter(String textToEscape, String nonEscapedCharacter, String escapedCharacter) {
		return textToEscape.replaceAll(nonEscapedCharacter, escapedCharacter);
	}
	
	/**
	 * Escapes <, >, /, ', ", ` characters in the passed text
	 * @param textToEscape
	 * @return escaped text
	 */
	public static String escapeHtmlText(String textToEscape) {
		for(int i = 0; i < nonEscaped.length; i++) {
			textToEscape = escapeCharacter(textToEscape, nonEscaped[i], escaped[i]);
		}
		return textToEscape;
	}
}
