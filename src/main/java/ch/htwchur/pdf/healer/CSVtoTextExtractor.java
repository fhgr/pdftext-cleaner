package ch.htwchur.pdf.healer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVtoTextExtractor {

    private static HashFunction hashFunction = Hashing.murmur3_128();

    /**
     * extracts CSV file to txt file
     * 
     * @param inputPath
     * @param outputPath
     * @throws IOException
     */
    public static void extractWeblyzardExportFileToTextFiles(String inputPath, String outputPath)
                    throws IOException {
        Map<String, String> hashTextMap = readCSVIntoPairs(inputPath, CSVFormat.DEFAULT);
        outputPath = outputPath.endsWith("/") ? outputPath : outputPath + "/";
        int i = 0;
        for (Map.Entry<String, String> entry : hashTextMap.entrySet()) {
            i++;
            writeToFileSystem(outputPath + entry.getKey() + ".txt", entry.getValue());
            if (i % 100 == 0) {
                log.info("Wrote {} files", i);
            }
        }

    }

    /**
     * Read csv export File from Weblyzard portal
     * 
     * @param path   Path
     * @param format CSV Format
     * @return Map with key as hashed content and text as value
     * @throws IOException
     */
    private static Map<String, String> readCSVIntoPairs(String path, CSVFormat format)
                    throws IOException {
        Map<String, String> hashCodeTextList = Maps.newHashMap();
        try (Reader reader = Files.newBufferedReader(Paths.get(path));
                        CSVParser csvParser = new CSVParser(reader, format);) {
            for (CSVRecord csvRecord : csvParser) {
                String source = csvRecord.get(11);
                source = source.replace("…", "").trim();
                hashFunction.newHasher().putString(source, Charsets.UTF_8).hash().toString();
                hashCodeTextList.put(hashFunction.newHasher().putString(source, Charsets.UTF_8)
                                .hash().toString(), source);
            }
        }
        return hashCodeTextList;
    }

    /**
     * Writes file to system
     * 
     * @param path use absolute path
     * @param text content to write
     */
    private static void writeToFileSystem(String path, String text) {
        try {
            FileUtils.writeStringToFile(new File(path), text, Charsets.UTF_8);
        } catch (IOException e) {
            log.warn("Could not write to file: {}: {}", e.getCause(), e.getMessage());
        }
    }
}
