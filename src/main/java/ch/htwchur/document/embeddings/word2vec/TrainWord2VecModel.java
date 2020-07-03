package ch.htwchur.document.embeddings.word2vec;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import ch.htwchur.document.embeddings.common.TextFileUtils;
import com.google.common.io.FileWriteMode;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to train WordEmbedding with Word2Vec
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class TrainWord2VecModel {

    public static void main(String[] args) throws IOException {
        TextFileUtils.concatinateTextFiles(
                        "/home/sandro/data/projects/03_integrity/Korpus/extracted_withdate_2020",
                        "/home/sandro/data/projects/03_integrity/Word2VecModels/Embeddings/concatinated_files_humarights_integrity_environment.txt",
                        FileWriteMode.APPEND);//


        String file = "/home/sandro/data/projects/03_integrity/Word2VecModels/Embeddings/concatinated_files_humarights_integrity_environment_german.txt";
        trainEmbeddings(file);
    }

    /**
     * trains wordembedding
     * 
     * @param rawSentenceFile
     * @throws IOException
     */
    public static void trainEmbeddings(String rawSentenceFile) throws IOException {
        log.info("Loading sentenceFile {} and prepare training...", rawSentenceFile);
        SentenceIterator itr = new BasicLineIterator(new File(rawSentenceFile).getAbsolutePath());
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new GermanPreprocessor());

        Word2Vec vec = new Word2Vec.Builder().minWordFrequency(5).iterations(5).epochs(5)
                        .layerSize(300).seed(42).windowSize(5).iterate(itr).tokenizerFactory(t)
                        .build();

        vec.fit();

        WordVectorSerializer.writeWordVectors(vec,
                        "german_serialied-" + vec.getLayerSize() + "-vector-5-5.txt");
        Collection<String> lst = vec.wordsNearest("bestechung", 10);
        System.out.println(lst);
    }

}
