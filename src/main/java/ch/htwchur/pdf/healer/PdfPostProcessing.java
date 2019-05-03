package ch.htwchur.pdf.healer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import lombok.extern.slf4j.Slf4j;

/**
 * Post processes a extracted PDF
 * <ul>
 * <li>Merges splitted words due to newlines. like Handels-\nabkommen'</li>
 * <li>Merges newlines if no puncation at the end of the line</li>
 * <li>Splits words with uppercase letters in a word</li>
 * <li>Replaces commonly unrecognized signs like bulletpoints to '-' ...</li>
 * <li>Removes pages keywords like 'Seite x von y' or 'Page x of y'</li>
 * <li>Reduces reoccuring newlines to a maxiumum of 2</li>
 * <li>Merges white-spaces words like 'W e d n e s d a y' to 'Wednesday'</li>
 * </ul>
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class PdfPostProcessing {
    private static final String RGX_MERGE_WHITESPACE = "([A-Za-zäöü-](\\s|\\p{Punct}|$)){2,}";
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(RGX_MERGE_WHITESPACE);
    private static final String RGX_FIND_PUNCTATIONS = "\\p{Punct}";
    private static final String RGX_FIND_STRANGE_END_DIGIT = "([�]{2,}+\\W+[0-9]*)";

    /**
     * Merges splitted words in case of
     * <ul>
     * <li>-\n</li>
     * <li>- \n</li>
     * <li>\n</li>
     * <li>-\r</li>
     * <li>- \r</li>
     * <li>-\r</li>
     * <li>-</li>
     * </ul>
     * 
     * @param text
     * @return cleaned String with merged words
     */
    protected static String mergeSplittedWords(String text) {
        return text.replace("-\n ", "").replace("- \n", "").replace("-\n", "").replace("-\r ", "")
                        .replace("- \r", "").replace("-\r", "").replace("- ", "");
    }

    private static Pattern newLineWithNoPunctationPattern =
                    Pattern.compile(".$(?<!\\p{Punct})(\\n)", Pattern.MULTILINE);

    /**
     * Removes newlines if no punctation occurs at the end of the line.
     * 
     * @param text
     * @return cleaned text
     */
    protected static String mergeNewlinesIfNoPunctationOccurs(String text) {
        Matcher matcher = newLineWithNoPunctationPattern.matcher(text);
        List<Pair<Integer, Integer>> matchedStartEndIdx = new ArrayList<>();
        while (matcher.find()) {
            matchedStartEndIdx.add(Pair.of(matcher.start(1), matcher.end(1)));
            log.debug("Matched start at {} and end at {} of match {}", matcher.start(1),
                            matcher.end(1), matcher.group(1));
        }
        /* add char[] to list to easely call remove */
        List<Character> textAsCharList = new ArrayList<>();
        for (char c : text.toCharArray()) {
            textAsCharList.add(c);
        }
        log.info("Found {} matches, removing newlines...", matchedStartEndIdx.size());
        /* reverse order to easely delete from end to start */
        Collections.reverse(matchedStartEndIdx);
        matchedStartEndIdx.forEach(pair -> {
            textAsCharList.set((int) pair.getLeft(), " ".toCharArray()[0]);
        });
        StringBuilder sb = new StringBuilder();
        textAsCharList.forEach(character -> sb.append(character));
        return sb.toString();
    }

    /**
     * Merges splitted words if a upper case letter occurs in word.
     * 
     * @param text
     * @return text with merged words
     */
    protected static String splitAccitentiallyMergedWordsWhenInwordUppercase(String text) {
        String[] splittedWords = text.split("(?<=\\p{javaLowerCase})(?=\\p{Lu})");
        StringBuilder sb = new StringBuilder();
        for (String splitts : splittedWords) {
            sb.append(splitts);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Removes not representable bulltenpoints and
     * <ul>
     * <li>\,\ to ,</li>
     * <li>\ to ""</li>
     * <li>{ and } to ""</li>
     * <li>&amp; to &</li>
     * <li>\\ to " "</li>
     * <li>■, ,, to -</li>
     * <li>� to .</li>
     * </ul>
     * 
     * @param text
     * @return cleaned String
     */
    protected static String removeUnreconziedSignsAndUnnecessaryCharacters(String text) {
        return text.replace("\",\"", " ").replace("\"", "").replace("{", "").replace("}", "")
                        .replace("&amp;", "&").replace("^", "").replace("\\", " ").replace("■", "-")
                        .replace("", "-").replace("", "-").replace("", "-").replace("�", ".");
    }

    /**
     * Splits text to lines
     * 
     * @param text
     * @return Array of Strings splitted by line feeds
     */
    public static String[] splitLines(String text) {
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }
        return lines;
    }

    /**
     * Removes page number descriptions if not longer than 20 chars and if <strong>seite or
     * page</strong> occurs in the same line
     * 
     * @param lines
     * @return cleaned lines
     */
    protected static String removePageKeywords(String line) {
        final List<String> keywords = Arrays.asList(new String[] {"seite", "page"});
        final int maxLineLength = 20;
        if (line.length() < maxLineLength) {
            for (String word : line.toLowerCase().split(" ")) {
                if (keywords.contains(word)) {
                    line = "";
                    break;
                }
            }
        }
        return line;
    }

    /**
     * Removes consecutive occuring empty lines
     * 
     * @param lines
     * @param thresholdReoccuringEmptyLines number of consecutivly occuring new lines before start
     *        to remove them.
     * @param thresholdMinLineLength. Removes lines with length lower or equal the threshold. If set
     *        to 0 nothing is done
     * @return List of lines with cleaned out empty lines and cleaned out lines with length lower
     *         then thresholdMinLineLength
     */
    protected static List<String> removeReoccuringNewlines(String[] lines,
                    int thresholdReoccuringEmptyLines, int thresholdMinLineLength) {
        final int thresholdToRemoveLF = 2;
        int consecutiveNewLines = 0;
        boolean firstElement = true;
        List<Integer> linesToRemove = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals("")) {
                consecutiveNewLines++;
                if (!firstElement && consecutiveNewLines >= thresholdToRemoveLF) {
                    linesToRemove.add(i);
                }
                firstElement = false;
            } else {
                firstElement = true;
                consecutiveNewLines = 0;
                if (thresholdMinLineLength > 0 && lines[i].length() <= thresholdMinLineLength) {
                    linesToRemove.add(i);
                }
            }
        }
        List<String> cleanedString = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (!linesToRemove.contains(i)) {
                cleanedString.add(lines[i]);
            }
        }
        return cleanedString;
    }

    /**
     * Starts line processing of a String
     * 
     * @param lines
     * @return processed lines
     */
    protected static String[] processLines(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = cleanUnmappableSignsIfOccuringWithNumberAtEndOfLine(lines[i]);
            lines[i] = removeUnreconziedSignsAndUnnecessaryCharacters(lines[i]);
            lines[i] = removePageKeywords(lines[i]);
            lines[i] = mergeWhitespacesLettersOfAWord(lines[i]);
            lines[i] = lines[i].replaceAll("\\s{2,}", " ");
            lines[i] = lines[i].replaceAll("\\((?=\\s{1,}).", "(");
        }
        return lines;
    }

    /**
     * Replaces merged word in String
     * 
     * @param Strings to check and merge
     * @return cleaned and merged strings
     */
    protected static String mergeWhitespacesLettersOfAWord(String line) {
        Matcher matcher = WHITESPACE_PATTERN.matcher(line);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            line = line.replace(matcher.group(), mergeAndCleanWhitespacedString(matcher.group()) + " ");
        }
        String[] processedLines = line.split("(?<=\\s)|((?<=\\p{Punct})(?!\\p{Digit}))");
        for (int i = 0; i < processedLines.length; i++) {
            sb.append(mergeAndCleanWhitespacedString(processedLines[i]));
            sb.append(" ");
        }
        return sb.toString().replaceAll("\\s{2,}"," ").trim();
    }

    /**
     * Merge word with a space between letters.
     * 
     * @param dirty
     * @return cleaned String
     */
    private static String mergeAndCleanWhitespacedString(String dirty) {
        dirty = dirty.replace(" ", "");
        String punctation = "";
        if (dirty.length() > 0 && Pattern.matches(RGX_FIND_PUNCTATIONS, dirty.substring(0, 1))) {
            punctation = dirty.substring(0, 1);
            dirty = dirty.substring(1, dirty.length());
        }
        dirty = cleanUpperCaseInWordofLineToStartWithUpperCase(dirty);
        return punctation + dirty;
    }
    
    private static String cleanUpperCaseInWordofLineToStartWithUpperCase(String dirty) {
        if (dirty.length() > 0) {
            if (Character.isUpperCase(dirty.charAt(0))) {
                String upperCasePart = dirty.substring(0, 1);
                dirty = dirty.toLowerCase();
                String lowerCasepart = dirty.substring(1, dirty.length());
                dirty = upperCasePart + lowerCasepart;
            } else {
                dirty = dirty.toLowerCase();
            }
        }
        return dirty;
    }

    /**
     * Cleans string by {@linkplain PdfPostProcessing#RGX_FIND_STRANGE_END_DIGIT} regex
     * 
     * @param dirty
     * @return cleaned String
     */
    private static String cleanUnmappableSignsIfOccuringWithNumberAtEndOfLine(String dirty) {
        return dirty.replaceAll(RGX_FIND_STRANGE_END_DIGIT, "");
    }
}
