package ch.htwchur.document.preprocess.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import lombok.extern.slf4j.Slf4j;

/**
 * Docx to plain text extractor
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class DocxToTextExtractor {

    /**
     * Extracts docx files to plain text from inputFolder to outputFolder
     * 
     * @param inputFolder  Folder with docx files to extract
     * @param outputFolder Folder to persist extracted plain text files
     * @throws IOException
     */
    public static void extractDocxFilesToText(String inputFolder, String outputFolder)
                    throws IOException {
        List<Path> files = DocumentHandler.readAllFilesFromDirectory(inputFolder);
        Map<String, String> fileNameContentMap = readDocXFile(files);
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

    /**
     * Reads all docx files and extracts them. Checks if file ending is .docx
     * 
     * @param paths List of Paths to extract
     * @return Map of filenames and their extracted content. key -> filename, value -> extracted
     *         content
     */
    public static Map<String, String> readDocXFile(List<Path> paths) {
        Map<String, String> fileNameContentMap = new HashMap<>();
        for (Path path : paths) {
            if (path.toFile().getName().toLowerCase().endsWith(".docx")) {
                InputStream fis;
                try {
                    fis = new FileInputStream(path.toFile());
                    XWPFDocument doc = new XWPFDocument(fis);
                    try (POITextExtractor extractor = new XWPFWordExtractor(doc)) {
                        fileNameContentMap.put(
                                        path.toFile().getName().toLowerCase().split(".docx")[0],
                                        extractor.getText());
                    }
                } catch (IOException e) {
                    log.info("Coulnt read file {} due to {}", path.toAbsolutePath(),
                                    e.getMessage());
                }
            }
        }
        return fileNameContentMap;
    }
    
}
