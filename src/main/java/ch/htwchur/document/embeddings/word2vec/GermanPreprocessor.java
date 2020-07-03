package ch.htwchur.document.embeddings.word2vec;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import ch.htwchur.document.embeddings.StopWords;
import lombok.extern.slf4j.Slf4j;

/**
 * Preprocessor for german corpus
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class GermanPreprocessor implements TokenPreProcess {

    @Override
    public String preProcess(String token) {
        token = token.toLowerCase().replace("ß", "ss").replaceAll("[^a-zA-ZäöüÄÖÜ ]+", "").trim();
//        token = token.toLowerCase().replaceAll("[\\p{Punct}\\d\\.:,\"\'\\(\\)\\[\\]|/?!;]+", "")
//                        .replace("«", "").replace("»", "").replace("”", "").replace("„", "")
//                        .replace("“", "").replace("ß", "ss").trim();
        log.debug(token);
        return (!StopWords.GERMAN_STOP_WORDS.contains(token) && token.length() > 2)  ? token : null;
    }

    /**
     * private static String preProcess(String filePath) throws IOException { String content =
     * FileUtils.readFileToString(new File(filePath), Charsets.UTF_8); List<String> tokens =
     * Arrays.asList(content.toLowerCase() .replaceAll("[\\p{Punct}\\d\\.:,\"\'\\(\\)\\[\\]|/?!;]+",
     * "") .replace("«", "").replace("»", "").replace("”", "").replace("„", "") .replace("“",
     * "").trim().split("\\s+"));
     * 
     * tokens = tokens.stream().map(token -> token.trim()) .filter(token ->
     * !GermanStopWords.GERMAN_STOP_WORDS.contains(token)) .filter(token -> token.length() >
     * 2).map(token -> token.trim()) .collect(Collectors.toList()); return Strings.join(tokens, '
     * ');
     */
}
