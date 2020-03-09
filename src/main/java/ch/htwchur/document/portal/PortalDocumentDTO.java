package ch.htwchur.document.portal;

import java.util.Map;
import lombok.Data;

@Data
public class PortalDocumentDTO {

    public PortalDocumentDTO(String content, String uri, String title,
                    Map<String, String> metaData) {
        super();
        this.content = content;
        this.uri = uri;
        this.title = title;
        this.meta_data = metaData;
    }

    private String content;
    private String content_type = "text/plain";
    private String repository_id = "integrity.semanticlab.net/api";
    private String uri;
    private String title;
    private Map<String, String> meta_data;


}
