package ch.htwchur.document.preprocess.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStreamSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtfToTextExtractor {

    public static void convertRtfToPlainText(String inputFolder, String outputFolder)
                    throws IOException {
        List<Path> files = DocumentHandler.readAllFilesFromDirectory(inputFolder);
        Map<String, String> fileNameContentMap = readRtfDocuments(files);
        int i = 0;
        for (Map.Entry<String, String> entry : fileNameContentMap.entrySet()) {
            DocumentHandler.writeFileToOutputFolder(outputFolder, entry.getKey(), entry.getValue());
            i++;
            if (i % 100 == 0) {
                log.info("Wrote {} files to {}", i, outputFolder);
            }
        }
        log.info("Finished docx to text conversion...");
    }

    protected static Map<String, String> readRtfDocuments(List<Path> paths) {
        Map<String, String> fileNameContentMap = new HashMap<>();
        for (Path path : paths) {
            if (path.toFile().getName().toLowerCase().endsWith(".rtf")) {
                try {
                    StringTextConverter converter = new StringTextConverter();
                    InputStream is = new FileInputStream(path.toString());
                    converter.convert(new RtfStreamSource(is));
                    String extractedText = converter.getText();
                    fileNameContentMap.put(path.toFile().getName().toLowerCase().split(".rtf")[0],
                                    extractedText);
                } catch (IOException e) {
                    log.warn("Couldn't convert rtf file {} due to {}", path.toAbsolutePath(),
                                    e.getMessage());
                }
            }
        }
        return fileNameContentMap;
    }
}
