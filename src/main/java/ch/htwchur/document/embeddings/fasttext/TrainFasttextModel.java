package ch.htwchur.document.embeddings.fasttext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import ch.htwchur.document.embeddings.StopWords;
import com.github.jfasttext.JFastText;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrainFasttextModel {

    private static final String FAKTIVA_CONCATINATED =
                    "/home/sandro/data/projects/03_integrity/Word2VecModels/Embeddings/concatinated_files_humarights_integrity_environment.txt";

    public static void main(String[] args) throws IOException {
        String processed = preProcess(FAKTIVA_CONCATINATED);
        log.info("Finished preprocessing and tokenization.");
        Path tmpPath = Files.createTempFile("concat", "tmp");
        FileUtils.writeStringToFile(new File(tmpPath.toString()), processed, Charsets.UTF_8);
        JFastText jft = new JFastText();
        jft.runCmd(new String[] {"skipgram", "-input", tmpPath.toFile().getAbsolutePath(),
                        "-output", "skipgram-300.model", "-bucket", "1000000", "-minCount", "5",
                        "-dim", "300", "-wordNgrams", "5", "-epoch", "5", "-thread", "4"});
        log.info("Finished model training, removing tempfile...");
        Files.delete(tmpPath);
    }
    
    /**
     * preprocess input files
     * @param filePath
     * @return
     * @throws IOException
     */
    private static String preProcess(String filePath) throws IOException {
        String content = FileUtils.readFileToString(new File(filePath), Charsets.UTF_8);
        List<String> tokens = Arrays.asList(content.toLowerCase()
                        .replaceAll("[\\p{Punct}\\d\\.:,\"\'\\(\\)\\[\\]|/?!;]+", "")
                        .replace("«", "").replace("»", "").replace("”", "").replace("„", "")
                        .replace("“", "").trim().split("\\s+"));

        tokens = tokens.stream().map(token -> token.trim())
                        .filter(token -> !StopWords.GERMAN_STOP_WORDS.contains(token))
                        .filter(token -> token.length() > 2).map(token -> token.trim())
                        .collect(Collectors.toList());
        return Strings.join(tokens, ' ');
    }
}


