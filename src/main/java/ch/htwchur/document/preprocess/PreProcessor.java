package ch.htwchur.document.preprocess;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
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
import ch.htwchur.document.preprocess.logic.ReadCsvAndMoveFilesToCategories;
import ch.htwchur.document.preprocess.logic.RtfToTextExtractor;
import ch.htwchur.document.preprocess.validation.RandomFilePicker;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;

/**
 * Pdf Healer, PDF to text extractor, Weblyzard PDF extractor
 * 
 * @author sandro.hoerler@fhgr.ch
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

        final Option createSet = Option.builder("c").required(true).hasArg(true)
                        .desc("creates a dataset from a csv template").build();
        final Option inputFile = Option.builder("i").required(true).hasArg(true)
                        .desc("input directory or input file").build();
        final Option outputFile = Option.builder("o").required(true).hasArg(true)
                        .desc("output directory or output file").build();
        final Option extraction = Option.builder("e").required(false).hasArg(false)
                        .desc("extracts pdfs to plain text including healing steps").build();
        final Option start = Option.builder("s").required(false).hasArg(true)
                        .desc("start pdf extraction at pdf page number").build();
        final Option csv = Option.builder("csv").required(false).hasArg(false)
                        .desc("extracts text from weblyzard-portal csv download").build();
        final Option prepareDocs = Option.builder("p").required(false).hasArg(false)
                        .desc("preprocess faktiva data").build();
        final Option removeHeader = Option.builder("h").required(false).hasArg(false)
                        .desc("adds header removal step to preprocessing").build();
        final Option pickAmount = Option.builder("pick").required(false).hasArg(true).desc(
                        "picks documents from directory with specified amount. A csv with all picked filenames is created")
                        .build();
        final Option csvFileName = Option.builder("csvfile").required(false).hasArg(true)
                        .desc("defines csv filename in combination with pick argument").build();
        final Option extractDoc = Option.builder("doc").required(false).hasArg(false)
                        .desc("extracts doc* files to plain text").build();
        final Option rtf = Option.builder("rtf").required(false).hasArg(false)
                        .desc("extracts rtf files to plain text").build();
        final Option zipFile = Option.builder("zip").required(false).hasArg(false)
                        .desc("choose zip file from input directory").build();
        final Option documentContent = Option.builder("document").required(false).hasArg(false)
                        .desc("extracts content part of a json wl-document").build();
        final Option fileName = Option.builder("filename").required(false).hasArg(true)
                        .desc("filename to read csv from").build();
        final Option charset = Option.builder("charset").required(false).hasArg(true)
                        .desc("set charset of input files").build();
        final Option includeSubfoldersCmd = Option.builder("subfolder").required(false)
                        .hasArg(false).desc("scans also subfolders for files").build();
        final Option fileSuffix = Option.builder("suffix").required(false).hasArg(true)
                        .desc("file ending to be scanned for in folders").build();

        final Options options = new Options();
        options.addOption(createSet);
        options.addOption(inputFile);
        options.addOption(outputFile);
        options.addOption(extraction);
        options.addOption(start);
        options.addOption(csv);
        options.addOption(fileName);
        options.addOption(prepareDocs);
        options.addOption(removeHeader);
        options.addOption(csvFileName);
        options.addOption(pickAmount);
        options.addOption(extractDoc);
        options.addOption(rtf);
        options.addOption(zipFile);
        options.addOption(documentContent);
        options.addOption(charset);
        options.addOption(includeSubfoldersCmd);
        options.addOption(fileSuffix);
        return options;
    }

    /**
     * Starts PdfHealing process
     * 
     * @param args
     * @throws ParseException
     * @throws IOException
     * @throws                ch.htwchur.document.preprocess.NotImplementedException
     */
    public static void main(String[] args) throws ParseException, IOException {
        Options options = generateOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("preprocessor", options);
            System.exit(1);
        }
        int startPage = 0;
        /* creates a dataset from a csv template */
        if (cmd.hasOption("c")) {
            String filename = cmd.getOptionValue("filename");
            log.info("Reading csv file {}, creating category folders and moving items into...",
                            filename);
            ReadCsvAndMoveFilesToCategories.copyFilesFromCsv(cmd.getOptionValue("i"),
                            cmd.getOptionValue("o"), filename);
            return;
        }
        if (cmd.hasOption("document")) {
            DocumentHandler.writeContentPartOfDocument(cmd.getOptionValue("i"),
                            cmd.getOptionValue("o"),
                            cmd.hasOption("charset")
                                            ? Charset.forName(cmd.getOptionValue("charset"))
                                            : Charsets.UTF_8);
        }
        if (cmd.hasOption("csv")) {
            extractCSVtoTextFiles(cmd.getOptionValue("i"), cmd.getOptionValue("o"));
            return;
        }
        if (cmd.hasOption("p")) {
            DocumentHandler.processDocuments(cmd.getOptionValue("i"), cmd.getOptionValue("o"),
                            cmd.hasOption("h"), cmd.hasOption("zip"),
                            cmd.hasOption("charset")
                                            ? Charset.forName(cmd.getOptionValue("charset"))
                                            : Charsets.UTF_8);
            return;
        }
        if (cmd.hasOption("pick")) {
            int amountToPick = Integer.parseInt(cmd.getOptionValue("pick"));
            String csvFilename = cmd.hasOption("csvfile") ? cmd.getOptionValue("csvfile")
                            : "picker_file.csv";
            RandomFilePicker.pickAmountOfFiles(amountToPick, cmd.getOptionValue("i"),
                            cmd.getOptionValue("o"), csvFilename,
                            cmd.hasOption("charset")
                                            ? Charset.forName(cmd.getOptionValue("charset"))
                                            : Charsets.UTF_8);
            return;
        }
        if (cmd.hasOption("e")) {
            if (cmd.hasOption("doc")) {
                DocxToTextExtractor.extractDocxFilesToText(cmd.getOptionValue("i"),
                                cmd.getOptionValue("o"));
                return;
            }
            if (cmd.hasOption("rtf")) {
                RtfToTextExtractor.convertRtfToPlainText(cmd.getOptionValue("i"),
                                cmd.getOptionValue("o"));
                return;
            }

            if (cmd.hasOption("start")) {
                startPage = Integer.parseInt(cmd.getOptionValue("start"));
            }
            log.info("Starting pdf to text extraction...");
            List<File> files = new ArrayList<>();
            Pdf2TextExtractor.listAllFilesInDirectoryAndSubdirectories(cmd.getOptionValue("i"),
                            files, cmd.getOptionValue("suffix"), cmd.hasOption("subfolder"));

            if (!cmd.getOptionValue("suffix").toLowerCase().equals("pdf")) {
                for (File file : files) {
                    String healedContent = pdfHealProcessing(Pdf2TextExtractor.readStringFromFile(
                                    file,
                                    cmd.getOptionValue("charset") == null ? StandardCharsets.UTF_8
                                                    : Charset.forName(cmd
                                                                    .getOptionValue("charset"))));
                    Pdf2TextExtractor.writeFileToSytem(Paths.get(cmd.getOptionValue("o")),
                                    healedContent, file.getName());

                }
                return;
            }

            if (files.size() == 0) {
                log.info("No files found to process in {}", Paths.get(cmd.getOptionValue("i")));
            }
            int i = 0;
            for (File file : files) {
                i++;
                Pair<String, String> extractedPdf =
                                Pdf2TextExtractor.extractPdfToText(file, startPage);
                Pdf2TextExtractor.writeFileToSytem(new File(cmd.getOptionValue("o")).toPath(),
                                pdfHealProcessing(extractedPdf.getRight()), extractedPdf.getLeft());
                if (i % 10 == 0) {
                    log.info("Extracted and Wrote file number {}", i);
                } ;
            }
            log.info("Pdf extraction done, extracted {} file(s)", i);
            return;
        }
        startProcessingOfWholeFolder(cmd.getOptionValue("i"), cmd.getOptionValue("o"));
    }

    /**
     * Processes whole folder
     * 
     * @param i reading directory
     * @param o writing directory
     * @throws IOException
     */
    private static void startProcessingOfWholeFolder(String i, String o) throws IOException {
        File folder = new File(i + "/");
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
                FileUtils.writeStringToFile(new File(o + "/" + file.getName() + ".txt"),
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
