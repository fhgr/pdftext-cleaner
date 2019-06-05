package ch.htwchur.document.preprocess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import ch.htwchur.document.preprocess.logic.CSVtoTextExtractor;
import ch.htwchur.document.preprocess.logic.DocumentHandler;
import ch.htwchur.document.preprocess.logic.DocxToTextExtractor;
import ch.htwchur.document.preprocess.logic.Pdf2TextExtractor;
import ch.htwchur.document.preprocess.logic.PdfPostProcessing;
import ch.htwchur.document.preprocess.validation.RandomFilePicker;
import lombok.extern.slf4j.Slf4j;

/**
 * Pdf Healer, PDF to text extractor, Weblyzard PDF extractor
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class PreProcessor {
    /**
     * Generates options
     * 
     * @return options
     */
    private static Options generateOptions() {
        final Option inputFile = Option.builder("inputDir").required(true).hasArg(true)
                        .longOpt("Input directory with text files or if csv input csv file")
                        .build();
        final Option outputFile = Option.builder("outputDir").required(true).hasArg(true)
                        .longOpt("Output directory").build();
        final Option extraction = Option.builder("e").required(false).hasArg(false)
                        .longOpt("Extract PDF to text").build();
        final Option limit = Option.builder("limit").required(false).hasArg(false)
                        .longOpt("Limit of pdf files to extract").build();
        final Option start = Option.builder("start").required(false).hasArg(true)
                        .longOpt("Start extraction at page number").build();
        final Option csv = Option.builder("csv").required(false).hasArg(false)
                        .longOpt("Extracts csv file to text files").build();
        final Option prepareDocs = Option.builder("prepare").required(false).hasArg(false)
                        .longOpt("prepares faktiva documents").build();
        final Option removeHeader = Option.builder("header").required(false).hasArg(false)
                        .longOpt("Removes header of a splitted document").build();
        final Option pickAmount = Option.builder("pick").required(false).hasArg(true).longOpt(
                        "Picks documents from directory with specified amount. A csv with all picked filenames is created")
                        .build();
        final Option csvFileName = Option.builder("csvfile").required(false).hasArg(true)
                        .longOpt("defines csv filename in combination with pick argument").build();
        final Option extractDoc = Option.builder("doc").required(false).hasArg(false)
                        .longOpt("extracts docx file to text").build();
        final Options options = new Options();
        options.addOption(inputFile);
        options.addOption(outputFile);
        options.addOption(extraction);
        options.addOption(limit);
        options.addOption(start);
        options.addOption(csv);
        options.addOption(prepareDocs);
        options.addOption(removeHeader);
        options.addOption(csvFileName);
        options.addOption(pickAmount);
        options.addOption(extractDoc);
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
        int startPage = 0;
        if (cmd.hasOption("csv")) {
            extractCSVtoTextFiles(cmd.getOptionValue("inputDir"), cmd.getOptionValue("outputDir"));
            return;
        }
        if (cmd.hasOption("prepare")) {
            DocumentHandler.processDocuments(cmd.getOptionValue("inputDir"),
                            cmd.getOptionValue("outputDir"), cmd.hasOption("header"));
            return;
        }
        if (cmd.hasOption("pick")) {
            int amountToPick = Integer.parseInt(cmd.getOptionValue("pick"));
            String csvFilename = cmd.hasOption("csvfile") ? cmd.getOptionValue("csvfile")
                            : "picker_file.csv";
            RandomFilePicker.pickAmountOfFiles(amountToPick, cmd.getOptionValue("inputDir"),
                            cmd.getOptionValue("outputDir"), csvFilename);
            return;
        }
        if (cmd.hasOption("e")) {
            if (cmd.hasOption("doc")) {
                DocxToTextExtractor.extractDocxFilesToText(cmd.getOptionValue("inputDir"),
                                cmd.getOptionValue("outputDir"));
                return;
            }
            if (cmd.hasOption("start")) {
                startPage = Integer.parseInt(cmd.getOptionValue("start"));
            }
            log.info("Starting pdf to text extraction...");
            List<File> files = Pdf2TextExtractor.readDirectoryFiles(cmd.getOptionValue("inputDir"),
                            cmd.getOptionValue("limit") == null ? 0
                                            : Integer.parseInt(cmd.getOptionValue("limit")));
            if (files.size() == 0) {
                log.info("No files found to process in {}",
                                Paths.get(cmd.getOptionValue("inputDir")));
            }
            int i = 0;
            for (File file : files) {
                i++;
                Pair<String, String> extractedPdf =
                                Pdf2TextExtractor.extractPdfToText(file, startPage);
                Pdf2TextExtractor.writeFileToSytem(
                                new File(cmd.getOptionValue("outputDir")).toPath(),
                                extractedPdf.getRight(), extractedPdf.getLeft());
                if (i % 10 == 0) {
                    log.info("Extracted and Wrote file number {}", i);
                } ;
            }
            log.info("Pdf extraction done, extracted {} file(s)", i);
            return;
        }
        startProcessingOfWholeFolder(cmd.getOptionValue("inputDir"),
                        cmd.getOptionValue("outputDir"));
    }

    /**
     * Processes whole folder
     * 
     * @param inputDir  reading directory
     * @param outputDir writing directory
     * @throws IOException
     */
    private static void startProcessingOfWholeFolder(String inputDir, String outputDir)
                    throws IOException {
        File folder = new File(inputDir + "/");
        File[] listOfFiles = folder.getAbsoluteFile().listFiles();
        if (listOfFiles == null || listOfFiles.length == 0) {
            log.info("Folder {} is empty or invalid...", folder.getAbsoluteFile());
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile() && FilenameUtils.getExtension(file.getName().toLowerCase())
                            .contains("txt")) {
                log.info("Healing file: {}", file.getName());
                String cleanedText =
                                pdfHealProcessing(new String(Files.readAllBytes(file.toPath())));
                FileUtils.writeStringToFile(new File(outputDir + "/" + file.getName() + ".txt"),
                                cleanedText, "UTF-8");
                log.info("Writing healed file: {}", file.getAbsoluteFile().getName());
            }
        }
    }

    /**
     * Starts Healing pipeline
     * 
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

    /**
     * Extracts CSV to text file
     * 
     * @param inputPath
     * @param outputPath
     */
    public static void extractCSVtoTextFiles(String inputPath, String outputPath) {
        try {
            CSVtoTextExtractor.extractWeblyzardExportFileToTextFiles(inputPath, outputPath);
        } catch (IOException e) {
            log.error("Could not process {} to {} due to {}", inputPath, outputPath,
                            e.getMessage());
        }
    }

}
