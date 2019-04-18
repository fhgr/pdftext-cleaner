package ch.htwchur.pdf.healer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
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
     * Traverses over directory and gets all files
     * 
     * @param pathString directory
     * @param limit limit of files returned
     * @return
     * @throws IOException
     */
    public static List<File> readDirectoryFiles(String pathString, int limit) throws IOException {
        List<File> filesInDirectory = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(pathString))) {
            paths.filter(Files::isRegularFile)
                            .filter(file -> FilenameUtils.getExtension(file.toString())
                                            .toLowerCase().contains("pdf"))
                            .forEach(path -> filesInDirectory.add(path.toFile()));
        }
        if (limit > 0) {
            return filesInDirectory.subList(0, limit);
        }
        return filesInDirectory;

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
