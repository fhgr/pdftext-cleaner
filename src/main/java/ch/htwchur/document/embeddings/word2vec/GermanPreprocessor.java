package ch.htwchur.document.embeddings.word2vec;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import ch.htwchur.document.embeddings.GermanStopWords;

/**
 * Preprocessor for german corpus
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
public class GermanPreprocessor implements TokenPreProcess {

    @Override
    public String preProcess(String token) {
        token = token.toLowerCase().replaceAll("[\\p{Punct}\\d\\.:,\"\'\\(\\)\\[\\]|/?!;]+", "")
                        .replace("«", "").replace("»", "").replace("”", "").replace("„", "").trim();
        return !GermanStopWords.GERMAN_STOP_WORDS.contains(token) ? token : null;
    }
}
