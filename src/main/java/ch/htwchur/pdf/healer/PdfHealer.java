package ch.htwchur.pdf.healer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import lombok.extern.slf4j.Slf4j;
/**
 * Main class to start PdfHealer
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class PdfHealer {
    /**
     * Generates options
     * 
     * @return options
     */
    private static Options generateOptions() {
        final Option inputFile = Option.builder("inputDir").required(true).hasArg(true)
                        .longOpt("Input directory with text files").build();
        final Option outputFile = Option.builder("outputDir").required(true).hasArg(true)
                        .longOpt("Output directory").build();
        final Options options = new Options();
        options.addOption(inputFile);
        options.addOption(outputFile);
        return options;
    }

    /**
     * Starts PdfHealing process
     * 
     * @param args
     * @throws ParseException
     * @throws IOException
     */
    public static void main(String[] args) throws ParseException, IOException {
        Options options = generateOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        startProcessingOfWholeFolder(cmd.getOptionValue("i"), cmd.getOptionValue("o"));
    }

    /**
     * Processes whole folder
     * @param inputDir reading directory
     * @param outputDir writing directory
     * @throws IOException
     */
    private static void startProcessingOfWholeFolder(String inputDir, String outputDir)
                    throws IOException {
        File folder = new File(inputDir + "/");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                log.info("Healing file: {}", file.getName());
                String cleanedText =
                                pdfHealProcessing(new String(Files.readAllBytes(file.toPath())));
                FileUtils.writeStringToFile(new File(outputDir + "/" + file.getName()), cleanedText,
                                Charset.forName("UTF-8"));
                log.info("Writing healed file: {}", file.getName());
            }
        }
    }

    /**
     * Starts Healing pipeline
     * @param extractedPdf
     * @return healed String
     * @throws IOException
     */
    public static String pdfHealProcessing(String extractedPdf) throws IOException {
        String result = PdfPostProcessing.mergeSplittedWords(extractedPdf);
        result = PdfPostProcessing.mergeNewlinesIfNoPunctationOccurs(result);
        String[] lines = PdfPostProcessing.splitLines(result);
        lines = PdfPostProcessing.processLines(lines);
        List<String> cleanedLines = PdfPostProcessing.removeReoccuringNewlines(lines, 2, 4);
        StringBuilder sb = new StringBuilder();
        cleanedLines.forEach(element -> {
            sb.append(element);
            sb.append("\n");
        });
        return sb.toString();
    }

}
