package ch.htwchur.document.preprocess.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import ch.htwchur.document.embeddings.word2vec.GermanPreprocessor;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to test outcome of text preprocessing
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class PreprocessTextWithGermanPreprocessor {
    /**
     * Preprocesses text files from
     * 
     * @param inputDir  given directory
     * @param outputDir
     * @return
     * @throws IOException
     */
    public static List<String> preprocessTextFiles(String inputDir, String outputDir)
                    throws IOException {
        final String outDir = outputDir.endsWith("/") ? outputDir : outputDir + "/";
        final TokenPreProcess preProcessor = new GermanPreprocessor();
        List<Path> paths = DocumentHandler.readAllFilesFromDirectory(inputDir);
        List<String> readFiles = paths.stream().map(path -> {
            String content = null;
            try {
                List<String> tokens = tokenizeString(
                                FileUtils.readFileToString(path.toFile(), Charsets.UTF_8));
                tokens = tokens.stream().map(token -> preProcessor.preProcess(token.trim()))
                                .collect(Collectors.toList()).stream()
                                .filter(item -> !item.isBlank()).collect(Collectors.toList());
                content = Strings.join(tokens, ' ');
            } catch (IOException e) {
                log.warn("Couldn't load file {}", path.getFileName());
            }
            try {
                FileUtils.writeStringToFile(new File(outDir + path.getFileName()), content,
                                Charsets.UTF_8);
            } catch (IOException e) {
                log.warn("Couldn't write file {} due to {}", path.getFileName(), e.getMessage());
            }
            log.info("Writing file {}", path.getFileName());
            return content;
        }).collect(Collectors.toList());
        return readFiles;
    }

    private static List<String> tokenizeString(String content) {
        return Arrays.asList(content.toLowerCase()
                        .replaceAll("[\\p{Punct}\\d\\.:,\"\'\\(\\)\\[\\]|/?!;]+", "")
                        .replace("«", "").replace("»", "").replace("”", "").replace("„", "")
                        .replace("“", "").trim().split("\\s+"));
    }

}
