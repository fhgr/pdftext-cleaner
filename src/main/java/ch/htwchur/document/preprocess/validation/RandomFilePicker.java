package ch.htwchur.document.preprocess.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import ch.htwchur.document.preprocess.logic.DocumentHandler;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class RandomFilePicker {

    public static Set<String> pickedFilenames = new HashSet<>();

    /**
     * Pics a specific amount of documents out of a directory in ranomized manner.
     * 
     * @param amount       of requested documents
     * @param inputFolder  folder to read from
     * @param outputFolder fodler to write into
     * @throws IOException
     */
    public static void pickAmountOfFiles(int amount, String inputFolder, String outputFolder, String csvFilename)
                    throws IOException {
        Map<String, String> documentFilenameMap = DocumentHandler.readWholeFolder(inputFolder);
        Random rnd = new Random();
        int progressCount = 0;
        for (int i = 0; i <= amount; i++) {
            Set<String> entries = documentFilenameMap.keySet();
            List<String> keys = new ArrayList<String>(entries);
            String key = keys.get(rnd.nextInt(keys.size()));
            String text = documentFilenameMap.remove(key);
            DocumentHandler.writeFileToOutputFolder(outputFolder, key, text);
            pickedFilenames.add(key);
            if (progressCount % 100 == 0) {
                log.info("Wrote {} files...", progressCount);
            }
            progressCount++;
        }
        CsvFilenameExporter.writenamesToCsv(pickedFilenames, outputFolder, csvFilename);
    }
}