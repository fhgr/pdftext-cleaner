package ch.htwchur.pdf.healer.validation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class CsvFilenameExporter {
    private static final String[] HEADERS = {"filename", "integrity-relevant"};
    /**
     * Writes filenames into a csv file
     * @param filenames
     * @param outputFolder
     * @param csvFilename
     * @throws IOException
     */
    protected static void writenamesToCsv(Set<String> filenames, String outputFolder, String csvFilename)
                    throws IOException {
        FileWriter out = new FileWriter(outputFolder + csvFilename);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            filenames.forEach(filename -> {
                try {
                    printer.printRecord(filename, "");
                } catch (IOException e) {
                   log.info("IOException {}", e.getMessage());
                }
            });
        }
        log.info("Wrote {} ", csvFilename);
    }
}
