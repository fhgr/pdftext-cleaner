package ch.htwchur.document.embeddings.common;

import java.io.IOException;
import com.google.common.io.FileWriteMode;

public class ConcatinateTextFiles {

    private final static String INPUT_FOLDER = "/home/sandro/data/projects/03_integrity/Korpus/Faktiva/RAW/Korruption/preprocessed";
    private final static String OUTPUT_FILE = "/home/sandro/data/projects/03_integrity/Word2VecModels/Embeddings/concatinated_files_humarights_integrity_environment_german.txt";
    
    public static void main(String[] args) throws IOException {
        TextFileUtils.concatinateTextFiles(INPUT_FOLDER, OUTPUT_FILE, FileWriteMode.APPEND);
    }
}
