package ch.htwchur.document.embeddings.word2vec;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import ch.htwchur.document.embeddings.StopWords;

/**
 * Preprocessor for english corpus
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
public class EnglishPreprocessor implements TokenPreProcess {

    @Override
    public String preProcess(String token) {
        token = token.toLowerCase().replaceAll("[\\p{Punct}\\d\\.:,\"\'\\(\\)\\[\\]|/?!;]+", "")
                        .replace("«", "").replace("»", "").replace("”", "").replace("„", "")
                        .replace("“", "").trim();
        return (!StopWords.ENGLISH_STOP_WORDS.contains(token) && token.length() > 2)  ? token : null;
    }
}
