package ch.htwchur.document.portal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ch.htwchur.document.preprocess.PreProcessor;
import ch.htwchur.document.preprocess.logic.DocumentHandler;
import ch.htwchur.jobcockpit.core.db.TeamsiteMysqlDB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadDocumentsIntoPortal {

    private static final String REPOSITORY = "integrity.semanticlab.net/api";
    private static final String USERNAME = "api@integrity.semanticlab.net";
    private static final String PASSWORD = "wQbBlstbOeIvkEXzXhT44FaU4c";
    private static final String VERSION = "1.0";
    private static final String BASE_URL = "https://api.weblyzard.com/";
    private static final String ENDPOINT_TOKEN = "/token";
    private static final String ENDPOINT_DOCUMENTS = "/documents/";
    private static final String EDA_TITLE = "EDA-Newsletter";
    private static final String FAKTIVA_TITLE = "Faktiva";
    private static final int UPLOAD_THREAD_COUNT = 40;
    private static final int MIN_DOCUMENT_LENGTH = 100;

    private static HashFunction documentIdHash = Hashing.murmur3_128();
    private static final boolean LOAD_FROM_DISK = true;

    private static ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) throws IOException, SQLException {
        uploadDocumentToPortal();
    }

    /**
     * Create document id.
     * 
     * @param document Document
     * @return document id
     */
    private static String createDocumentId(String document) {
        return documentIdHash.newHasher().putString(document, Charsets.UTF_8).hash().toString();
    }

    private static boolean uploadDocumentToPortal() throws IOException, SQLException {
        List<PortalDocumentDTO> portalDocuments;
        if (!LOAD_FROM_DISK) {
            portalDocuments = loadDataFromDatabase();
        } else {
            portalDocuments = loadDataFromDisk(CHARSET);
        }

        log.info("Loaded {} documents...", portalDocuments.size());
        portalDocuments = portalDocuments.stream()
                        .filter(doc -> doc.getContent().length() >= MIN_DOCUMENT_LENGTH)
                        .collect(Collectors.toList());
        log.info("After applying MIN_DOC_LENGTH_FILTER of {} ->  {} documents are present.",
                        MIN_DOCUMENT_LENGTH, portalDocuments.size());
        HttpGet request = new HttpGet(BASE_URL + VERSION + ENDPOINT_TOKEN);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
        String token = "";
        try (CloseableHttpClient httpClient =
                        HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
                        CloseableHttpResponse response = httpClient.execute(request)) {
            log.info("Getting token: response code {}", response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                token = EntityUtils.toString(entity);
                log.info("Token: {}", token);
            }
        }
        multiThreadedUpload(portalDocuments, token);
        return true;
    }

    private static Queue<PortalDocumentDTO> uploadQueue;
    private static AtomicInteger documentCounter = new AtomicInteger(0);

    /**
     * Multithreaded Upload
     * 
     * @param documents
     * @param token
     */
    private static void multiThreadedUpload(List<PortalDocumentDTO> documents, String token) {
        uploadQueue = new ConcurrentLinkedQueue<>(documents);
        for (int i = 0; i < UPLOAD_THREAD_COUNT; i++) {
            new Thread(() -> {
                while (!uploadQueue.isEmpty()) {
                    PortalDocumentDTO doc = uploadQueue.poll();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(doc);
                    } catch (JsonProcessingException e) {
                        log.info("couldn't map...");
                    }
                    CloseableHttpClient httpclient = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost(
                                    BASE_URL + VERSION + ENDPOINT_DOCUMENTS + REPOSITORY);
                    httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    HttpEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
                    httpPost.setEntity(stringEntity);
                    CloseableHttpResponse response = null;
                    try {
                        response = httpclient.execute(httpPost);
                    } catch (IOException e) {
                        log.warn("Error while uploading document {} due to {}", doc.getUri(),
                                        e.getMessage());
                    }
                    String body = null;
                    try {
                        body = IOUtils.toString(response.getEntity().getContent(), Charsets.UTF_8);
                    } catch (UnsupportedOperationException | IOException e) {
                        log.error(e.getMessage());
                    }
                    log.info("Submitted document nr {} with id {} successfully: {}",
                                    documentCounter.incrementAndGet(), doc.getUri(), body);
                }
            }).start();
        }
    }

    /**
     * Load data from sql database
     * 
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private static List<PortalDocumentDTO> loadDataFromDatabase() throws SQLException, IOException {
        TeamsiteMysqlDB db = new TeamsiteMysqlDB();
        PreparedStatement stmnt = db.getConnection().prepareStatement(
                        "select id, cid.url, lang, date, text from crawl_urls cid join webcontent wc on wc.source_id = cid.id and lang in ('EN','DE')");
        ResultSet rs = stmnt.executeQuery();
        List<PortalDocumentDTO> portalDocuments = new ArrayList<>();
        while (rs.next()) {
            log.info("Document {} is preparing...", rs.getInt("id"));
            String text = rs.getString("text");
            if (rs.getString("url").endsWith(".pdf")) {
                log.info("PDF found, preprocessing and cleaning it...");
                text = PreProcessor.pdfHealProcessing(DocumentHandler.removeDocumentHeader(text));
            }
            if (text.isEmpty()) {
                continue;
            }
            Map<String, String> metadataMap = new HashMap<>();
            metadataMap.put("published_date", rs.getString("date"));
            metadataMap.put("language_id", rs.getString("lang"));
            String title = text.split("\n").length > 0 ? text.split("\n")[0]
                            : EDA_TITLE + " " + rs.getInt("id");
            PortalDocumentDTO dto =
                            new PortalDocumentDTO(text, rs.getString("url"), title, metadataMap);
            portalDocuments.add(dto);
            log.info("Document {} prepared...", rs.getInt("id"));
        }
        return portalDocuments;
    }

    /* GERMAN */
    // private final static String FOLDER_LOCATION =
    // "/home/sandro/data/projects/03_integrity/Korpus/Faktiva/RAW/Korruption/extracted_withdate_2019";
    // private final static String FOLDER_LANGUAGE = "de";
    // private final static Charset CHARSET = Charsets.UTF_8;

    /* English */
    private final static String FOLDER_LOCATION =
                    "/home/sandro/data/projects/03_integrity/Korpus_EN/extracted_withdate_2020";
    private final static String FOLDER_LANGUAGE = "en";
    private final static Charset CHARSET = Charsets.ISO_8859_1;

    /**
     * Load data from disk
     * 
     * @return
     * @throws IOException
     */
    private static List<PortalDocumentDTO> loadDataFromDisk(Charset charset) throws IOException {
        Map<String, String> allFilesInDirectory =
                        DocumentHandler.readWholeFolder(FOLDER_LOCATION, charset);
        List<PortalDocumentDTO> portalDocuments = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> nameAndDocument : allFilesInDirectory.entrySet()) {
            log.info("Document {} is preparing...", ++i);
            Map<String, String> metadataMap = new HashMap<>();
            metadataMap.put("published_date", FilenameDateExtractor
                            .extractDateFromFilename(nameAndDocument.getKey()));
            metadataMap.put("language_id", FOLDER_LANGUAGE);
            String title = nameAndDocument.getValue().split("\n").length > 0
                            ? nameAndDocument.getValue().split("\n")[0]
                            : FAKTIVA_TITLE + " " + nameAndDocument.getKey();
            PortalDocumentDTO dto = new PortalDocumentDTO(nameAndDocument.getValue(),
                            "http://faktiva.sife.extracted/" + FOLDER_LANGUAGE + "/"
                                            + createDocumentId(nameAndDocument.getValue()),
                            title, metadataMap);
            portalDocuments.add(dto);
            log.info("Document {} with id {} is prepared", ++i, dto.getUri());
        }
        return portalDocuments;
    }
}
