package ch.htwchur.document.preprocess.logic;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtfToTextExtractorTest {

    @Test
    public void testDocxToTextExtraction() throws IOException {
        var classLoader = new DocxToTextExtractor().getClass().getClassLoader();
        var file = new File(
                        classLoader.getResource("docx/2015_Factiva-20191205-1750.rtf").getPath());
        List<Path> pathList = List.of(file.toPath());
        Map<String, String> result = RtfToTextExtractor.readRtfDocuments(pathList);
        String convertedText = result.get("2015_factiva-20191205-1750");
        assertTrue(convertedText.contains("kampagnenfähig"));
        assertTrue(convertedText.contains("Wörtern"));
        assertTrue(convertedText.contains("Böse"));
        log.info(convertedText);
    }
}
