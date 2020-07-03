package ch.htwchur.document.portal;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenHandler {

    private static final String USERNAME = "api@integrity.semanticlab.net";
    private static final String PASSWORD = "wQbBlstbOeIvkEXzXhT44FaU4c";
    private static final String VERSION = "1.0";
    private static final String BASE_URL = "https://api.weblyzard.com/";
    private static final String ENDPOINT_TOKEN = "/token";

    /**
     * Call to get token for Portal api
     * 
     * @param baseUrl       url
     * @param version       endpoint version
     * @param tokenEndpoint endpoint for token
     * @param username      username
     * @param password      password
     * @return received token
     */
    public static String getToken(String baseUrl, String version, String tokenEndpoint,
                    String username, String password) {
        HttpGet request = new HttpGet(baseUrl + version + tokenEndpoint);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
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
        } catch (IOException e) {
            log.info("Couldn't get token due to {}", e.getMessage());
        }
        return token;
    }

    public static String getToken() {
        return getToken(BASE_URL, VERSION, ENDPOINT_TOKEN, USERNAME, PASSWORD);
    }
}
