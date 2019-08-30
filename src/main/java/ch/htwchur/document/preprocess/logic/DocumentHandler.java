package ch.htwchur.document.preprocess.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.weblyzard.api.model.document.Document;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to read text files and split them according to specified regex and removes header
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class DocumentHandler {
    private static final String RGX_REMOVE_DOC_HEADER = "(?m)^.*?\\b(copyright|Copyright)\\b.*$";
    private static final String RGX_REMOVE_SIGN_DOC_HEADER = "(?m)^©.*$";
    private static final String RGX_SPLIT_DOC = "(?m)^(Dokument|Document) \\w+$";
    private static final String RGX_REMOVE_C_IN_BRACKETS = "(?m)^\\((c)\\).*$";
    private static final String RGX_REMOVE_NZZ_HEADER = "(?m)^Besuchen Sie die Website.*$";
    private static final String RGX_REMOVE_TIMES_HEADER =
                    "(?m).*Times Newspapers Ltd. All rights reserved.*$";
    private static final String RGX_REMOVE_NYTIMES_HEADER =
                    "(?m).*\\d +.*New York Times Company.*$";

    private static final String RGX_REMOVE_ARTIKEL_ANZEIGEN = "(?m)^Artikel anzeigen.*$";
    private static final String RGX_REMOVE_ERSTELLT = "(?m)^Erstellt: .*$";
    private static final String RGX_REMOVE_WHO_WROTE_WHEN = "(?m).*Uhr$";
    // private static final String RGX_REMOVE_INDEX_HEAD_FAKTIVA = "^\\w*.*$";
    public static Pattern documentSplitPattern;

    /**
     * Entrypoint
     * 
     * @param inputFolder  inputfolder
     * @param outputFolder outputfolder
     * @throws IOException
     */
    public static void processDocuments(String inputFolder, String outputFolder,
                    boolean splitHeader, boolean zipFile) throws IOException {
        int i = 0;
        Map<String, String> textFiles = null;
        if (!zipFile) {
            textFiles = readWholeFolder(inputFolder);
        } else {
            textFiles = readFileFromZip(inputFolder);
        }
        for (Map.Entry<String, String> entry : textFiles.entrySet()) {
            List<String> splittedDocument = splitDocuments(entry.getValue());
            int docIdx = 0;
            for (String splittedDoc : splittedDocument) {
                if (splitHeader) {
                    splittedDoc = removeDocumentHeader(splittedDoc);
                }
                i++;
                if (i % 10 == 0) {
                    log.info("Processed {} files", i);
                }
                writeFileToOutputFolder(outputFolder, entry.getKey() + "_" + docIdx, splittedDoc);
                docIdx++;
            }
        }
        log.info("Wrote {} files", i);
    }

    /**
     * Read txt files from a zip files
     * 
     * @param paths list of zip paths
     * @return map of filename and its text content
     */
    private static Map<String, String> readFileFromZip(String inputFolder) throws IOException {
        Map<String, String> contentMap = new HashMap<>();
        List<Path> filesInFolder = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(inputFolder))) {
            paths.filter(Files::isRegularFile).forEach(filesInFolder::add);
        }
        for (Path path : filesInFolder) {
            try (ZipFile zip = new ZipFile(path.toFile())) {
                Enumeration<? extends ZipEntry> e = zip.entries();
                log.info("Reading file {}", path.toFile());
                while (e.hasMoreElements()) {
                    ZipEntry entry = e.nextElement();
                    if (!entry.isDirectory()) {
                        if (FilenameUtils.getExtension(entry.getName()).equals("txt")) {
                            StringBuilder sb = getTxtFiles(zip.getInputStream(entry));
                            contentMap.put(entry.getName().split(".txt")[0], sb.toString());
                        }
                    }
                }
            }
        }
        return contentMap;
    }

    private static StringBuilder getTxtFiles(InputStream in) {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        } catch (IOException e) {
            // do something, probably not a text file
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Reads all files in a Folder.
     * 
     * @param inputFolder folder to read from
     * @return List of all files as Path
     * @throws IOException
     */
    public static List<Path> readAllFilesFromDirectory(String inputFolder) throws IOException {
        List<Path> filesInFolder = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(inputFolder))) {
            paths.filter(Files::isRegularFile).forEach(filesInFolder::add);
        }
        return filesInFolder;
    }

    /**
     * Reads whole inputfolder files
     * 
     * @param inputFolder
     * @return map of strings from found files. Key -> filename, Value -> filecontent
     * @throws IOException
     */
    public static Map<String, String> readWholeFolder(String inputFolder) throws IOException {
        List<Path> filesInFolder = new ArrayList<>();
        Map<String, String> fileMap = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(inputFolder))) {
            paths.filter(Files::isRegularFile).forEach(filesInFolder::add);
        }
        for (Path path : filesInFolder) {
            String textFile = FileUtils.readFileToString(path.toFile(), Charsets.UTF_8);
            fileMap.put(path.getFileName().toString(), textFile);
        }
        return fileMap;
    }

    /**
     * Splits documents with {@linkplain #RGX_SPLIT_DOC} regex
     * 
     * @param document
     * @return list of splitted docuuments
     */
    public static List<String> splitDocuments(String document) {
        List<String> splittedDocument = new ArrayList<>();
        String[] splitted = document.split(RGX_SPLIT_DOC);
        splittedDocument.addAll(Arrays.asList(splitted));
        return splittedDocument;
    }

    /**
     * Removes content above and including {@linkplain #RGX_REMOVE_DOC_HEADER},
     * {@linkplain #RGX_REMOVE_C_IN_BRACKETS}, {@linkplain #RGX_REMOVE_SIGN_DOC_HEADER}.
     * 
     * @param document document to remove header
     * @return document with removed header if a pattern matched
     */
    public static String removeDocumentHeader(String document) {
        String[] headerBodySplitted = document.split(RGX_REMOVE_DOC_HEADER);
        if (headerBodySplitted.length == 1) {
            headerBodySplitted = document.split(RGX_REMOVE_SIGN_DOC_HEADER);
        }
        if (headerBodySplitted.length == 1) {
            headerBodySplitted = document.split(RGX_REMOVE_C_IN_BRACKETS);
        }
        if (headerBodySplitted.length == 1) {
            headerBodySplitted = document.split(RGX_REMOVE_NZZ_HEADER);
        }
        if (headerBodySplitted.length == 1) {
            headerBodySplitted = document.split(RGX_REMOVE_TIMES_HEADER);
        }
        if (headerBodySplitted.length == 1) {
            headerBodySplitted = document.split(RGX_REMOVE_NYTIMES_HEADER);
        }
        if (headerBodySplitted.length == 0) {
            return "";
        }
        return removeDocumentNoisyParts(headerBodySplitted.length > 1 ? headerBodySplitted[1].trim()
                        : headerBodySplitted[0].trim());
    }

    /**
     * Removes commonly occuring parts in a document
     * 
     * @param document
     * @return
     */
    private static String removeDocumentNoisyParts(String document) {
        return document.replaceAll(RGX_REMOVE_ARTIKEL_ANZEIGEN, "")
                        .replaceAll(RGX_REMOVE_WHO_WROTE_WHEN, "")
                        .replaceAll(RGX_REMOVE_ERSTELLT, "");
    }

    /**
     * Writes splitted and cleaned files to outputfolder. The string content is hashed and used as
     * filename to avild duplications
     * 
     * @param outputFolder
     * @param documentBody
     */
    public static void writeFileToOutputFolder(String outputFolder, String fileName,
                    String documentBody) {
        outputFolder = outputFolder.endsWith("/") ? outputFolder : outputFolder + "/";
        try {
            FileUtils.writeStringToFile(new File(outputFolder + fileName + ".txt"), documentBody,
                            Charsets.UTF_8);
        } catch (IOException e) {
            log.error("Couldnt write file {} due to {}",
                            new File(outputFolder + fileName + ".txt").toString(), e.getMessage());
        }
    }

    /**
     * Loads json files of persisted WL-Document Objects, extracts the content part and serializes
     * to disk. The filename will be kept
     * 
     * @param inputFolder
     * @param outputFolder
     * @throws IOException
     */
    public static void writeContentPartOfDocument(String inputFolder, String outputFolder)
                    throws IOException {
        Map<String, String> filesAndNames = readWholeFolder(inputFolder);
        ObjectMapper mapper = new ObjectMapper();
        log.info("Found {} files in folder {}, starting content extraction...",
                        filesAndNames.size(), inputFolder);
        int i = 0;
        for (Entry<String, String> entry : filesAndNames.entrySet()) {
            i++;
            if (i % 100 == 0) {
                log.info("Processing files {} of {}", i, filesAndNames.size());
            }
            writeFileToOutputFolder(outputFolder, entry.getKey(),
                            mapper.readValue(entry.getValue(), Document.class).getContent());
        }
    }
}
