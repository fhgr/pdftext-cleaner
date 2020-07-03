package ch.htwchur.document.portal;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetDocumentFromPortal {

    private static final String POST_URL = "https://api.weblyzard.com/1.0/search";
    private static String token = null;

    /**
     * Download a document from integrity portal
     * 
     * @param contentId document identifier
     * @param beginDate query begin date
     * @param endDate   query end date
     * @return document as String
     * @throws JsonProcessingException
     */
    public static String loadDocumentFromPortal(String contentId, String beginDate, String endDate)
                    throws JsonProcessingException {
        token = token == null ? TokenHandler.getToken() : token;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(POST_URL);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        var query = PortalSearchInstance.of(contentId, beginDate, endDate);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(query);
        HttpEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            log.warn("Error quering document with conetntId {} due to {}", contentId,
                            e.getMessage());
        }
        String body = null;
        try {
            body = IOUtils.toString(response.getEntity().getContent(), Charsets.UTF_8);
        } catch (UnsupportedOperationException | IOException e) {
            log.error(e.getMessage());
        }
        return body;
    }
}
