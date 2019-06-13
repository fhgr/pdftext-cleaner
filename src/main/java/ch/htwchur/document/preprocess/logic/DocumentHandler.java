package ch.htwchur.document.preprocess.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import com.google.common.base.Charsets;
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
    private static final String RGX_SPLIT_DOC = "(?m)^Dokument \\w+$";
    private static final String RGX_REMOVE_C_IN_BRACKETS = "(?m)^\\((c)\\).*$";
    private static final String RGX_REMOVE_NZZ_HEADER = "(?m)^Besuchen Sie die Website.*$";
    public static Pattern documentSplitPattern;

    /**
     * Entrypoint
     * 
     * @param inputFolder  inputfolder
     * @param outputFolder outputfolder
     * @throws IOException
     */
    public static void processDocuments(String inputFolder, String outputFolder,
                    boolean splitHeader) throws IOException {
        int i = 0;
        Map<String, String> textFiles = readWholeFolder(inputFolder);
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
        Map<String, String> fileMap = new HashMap<>();
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
        return headerBodySplitted.length > 1 ? headerBodySplitted[1].trim()
                        : headerBodySplitted[0].trim();
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
}
