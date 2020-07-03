package ch.htwchur.document.portal;

import org.junit.jupiter.api.Test;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PortalDownloadTest {

    @Test
    public void testDownload() throws JsonProcessingException {
        var document = GetDocumentFromPortal.loadDocumentFromPortal("4854188486039945576",
                        "2020-04-01", "2020-04-07");
        log.info("document:\n{}", document);
    }
}
