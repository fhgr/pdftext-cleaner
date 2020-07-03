package ch.htwchur.document.preprocess.logic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts a PDF to Text
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
@Slf4j
public class Pdf2TextExtractor {

    private static PDFTextStripper stripper;

    static {
        try {
            stripper = new PDFTextStripper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts plain text from pdf file
     * 
     * @param file
     * @return
     * @throws InvalidPasswordException
     * @throws IOException
     */
    public static Pair<String, String> extractPdfToText(File file, int startPage)
                    throws InvalidPasswordException, IOException {
        try (PDDocument doc = PDDocument.load(file)) {
            log.info("Loaded file: {}", file.getName());
            stripper.setStartPage(startPage);
            Pair<String, String> pdfFilePair = Pair.of(file.getName(), stripper.getText(doc));
            return pdfFilePair;
        }
    }

    /**
     * List all files in directory and subdirectories
     * 
     * @param directoryName     root directory
     * @param files             list to hold files recursively
     * @param fileSuffix        file endings to be included <code>txt, pdf, ...</code>
     * @param inlcudeSubfolders scans also subfolders for files
     */
    public static void listAllFilesInDirectoryAndSubdirectories(String directoryName,
                    @NonNull List<File> files, String fileSuffix, boolean includeSubfolders) {
        File directory = new File(directoryName);
        /* list files in current directory */
        File[] fList = directory.listFiles();
        if (fList != null)
            for (File file : fList) {
                if (file.isFile() && FilenameUtils.getExtension(file.toString())
                                .contains(fileSuffix)) {
                    files.add(file);
                } else if (file.isDirectory() && includeSubfolders) {
                    listAllFilesInDirectoryAndSubdirectories(file.getAbsolutePath(), files,
                                    fileSuffix, includeSubfolders);
                }
            }
    }

    /**
     * Read string from file
     * 
     * @param file
     * @param charset
     * @return String content
     */
    public static String readStringFromFile(File file, Charset charset) {
        charset = charset == null ? StandardCharsets.UTF_8 : charset;
        try {
            return FileUtils.readFileToString(file, charset);
        } catch (IOException e) {
            log.warn("File {} could not be loaded due to {}", file.getName(), e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Persists file to file system
     * 
     * @param path
     * @param content
     * @param fileName
     * @throws IOException
     */
    public static void writeFileToSytem(Path path, String content, String fileName)
                    throws IOException {
        FileUtils.writeStringToFile(new File(path.toFile().toString() + "/" + fileName + ".txt"),
                        content, Charset.forName("UTF-8"));
    }
}
