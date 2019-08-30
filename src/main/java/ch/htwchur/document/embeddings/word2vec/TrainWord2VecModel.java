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
//        String file = "/home/sandro/data/projects/03_integrity/Korpus/Faktiva/validated_gDrive/concatinated.txt";
        String file = "/home/sandro/data/projects/03_integrity/Korpus_EN/concatinated_english_to_train_embeddings.txt";
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
        // String rawSentence = FileUtils.readFileToString(new File(rawSentenceFile),
        // Charsets.UTF_8);
        SentenceIterator itr = new BasicLineIterator(new File(rawSentenceFile).getAbsolutePath());
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new EnglishPreprocessor());

        Word2Vec vec = new Word2Vec.Builder().minWordFrequency(5).iterations(5).layerSize(300)
                        .seed(42).windowSize(5).iterate(itr).tokenizerFactory(t).build();

        vec.fit();

        WordVectorSerializer.writeWordVectors(vec,
                        "english_serialied-" + vec.getLayerSize() + "-vector-5-5.txt");
        Collection<String> lst = vec.wordsNearest("bestechung", 10);
        System.out.println(lst);
    }

}
