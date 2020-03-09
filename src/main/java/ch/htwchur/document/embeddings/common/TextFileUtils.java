package ch.htwchur.document.embeddings.common;

import java.io.File;
import java.io.IOException;
import ch.htwchur.document.preprocess.logic.DocumentHandler;
import com.drew.lang.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextFileUtils {
    /**
     * Concatinates text files from a folder into one file
     * 
     * @param inputPath  directory to read from
     * @param outputFile file to write
     * @param writeMode  Write mode append to append new content to existing file, default (null) is
     *                   truncating file first
     * @throws IOException
     */
    public static void concatinateTextFiles(String inputPath, String outputFile,
                    FileWriteMode writeMode) throws IOException {
        var filesMap = DocumentHandler.readWholeFolder(inputPath, Charsets.UTF_8);
        File file = new File(outputFile);
        CharSink chs = Files.asCharSink(file, Charsets.UTF_8, writeMode);
        int i = 0;
        for (String text : filesMap.values()) {
            chs.write(text);
            i++;
            if (i % 100 == 0) {
                log.info("Wrote {} files.", i);
            }
        }
        log.info("Finished, wrote {} files into {}", i, inputPath);
    }
}
