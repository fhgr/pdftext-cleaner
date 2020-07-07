package ch.htwchur.document.preprocess.logic;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

/**
 * CSV file must have following structure:
 * 1st row names of colums 1st column with filename, withoutcolumn names!
 * 
 * Files will be moved into the depending category folders. They will be created if not existing
 * yet. InputDir describes directory where all the files lie OutputDir describes directory where
 * category folders are created and the files above are moved into
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class ReadCsvAndMoveFilesToCategories {
    /**
     * Copy Files
     * 
     * @param inputDir
     * @param outputDir
     * @param filename
     * @throws IOException
     */
    public static void copyFilesFromCsv(String inputDir, String outputDir, String filename)
                    throws IOException {
        copyFiles(inputDir, outputDir, readCsvFileAndCreateMap(filename));
    }

    /**
     * Reads CSV file and creates Map with category as key and Set of files as value
     * 
     * @param filename  csv file with filenames and categories
     * @param inputDir  directory with files
     * @param outputDir directory where files should be moved categorized
     * @return Map with category as key and Set of files as value
     * @throws IOException
     */
    private static Map<String, Set<String>> readCsvFileAndCreateMap(String filename)
                    throws IOException {
        Map<String, Set<String>> filenameCategoryMap = Maps.newHashMap();
        try (Reader reader = Files.newBufferedReader(Paths.get(filename));
                        CSVParser csvParser = new CSVParser(reader,
                                        CSVFormat.EXCEL.withDelimiter(','));) {
            for (CSVRecord csvRecord : csvParser) {
                String source = csvRecord.get(0);
                String category = csvRecord.get(1);
                if (filenameCategoryMap.containsKey(category)) {
                    filenameCategoryMap.get(category).add(source);
                } else {
                    Set<String> valueSet = new HashSet<>();
                    valueSet.add(source);
                    filenameCategoryMap.put(category, valueSet);
                }
            }
        }
        return filenameCategoryMap;
    }

    /**
     * Copies files
     * 
     * @param inputDir
     * @param outputDir
     * @param filenameCategoryMap
     */
    private static void copyFiles(String inputDir, String outputDir,
                    Map<String, Set<String>> filenameCategoryMap) {
        filenameCategoryMap.keySet().forEach(item -> {
            File dir = new File(
                            outputDir.endsWith("/") ? outputDir + item : outputDir + "/" + item);
            boolean dirCreation = dir.mkdir();
            if (dirCreation) {
                log.info("created directory {}", dir.getAbsolutePath());
            }
        });
        filenameCategoryMap.keySet().forEach(key -> {
            Set<String> filenames = filenameCategoryMap.get(key);
            filenames.forEach(item -> {
                int i = 0;
                try {
                    FileUtils.copyFile(
                                    new File(inputDir.endsWith("/") ? inputDir + "/" + item
                                                    : inputDir + "/" + item),
                                    new File(outputDir.endsWith("/") ? outputDir + key + "/" + item
                                                    : outputDir + "/" + key + "/" + item));
                } catch (IOException e) {
                    log.info("Exception occured: {}", e.getMessage(), e.getCause());
                }
                log.info("Copied {} files", ++i);
            });
        });
    }


}
