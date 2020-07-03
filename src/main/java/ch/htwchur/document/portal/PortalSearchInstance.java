package ch.htwchur.document.portal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.jsonldjava.shaded.com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Portal search instance holds all necessary properties for a weblyzard portal querie.
 * 
 * @author sandro.hoerler@fhgr.ch
 *
 */
@Data
@Accessors(chain = true)
public class PortalSearchInstance {

    private String beginDate;
    private String endDate;
    private List<String> sources;
    private int count = 1;
    private List<String> fields;
    private Map<String, Map<String, String>> query = new HashMap<>();

    /**
     * Private constructor
     * 
     * @param beginDate
     * @param endDate
     * @param sources
     * @param count
     * @param fields
     */
    private PortalSearchInstance(String beginDate, String endDate, List<String> sources, int count,
                    List<String> fields) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.sources = sources;
        this.count = count;
        this.fields = fields;
    }

    /**
     * Constructs a query
     * 
     * @param contentId document_id to query for
     * @param beginDate date
     * @param endDate   date
     * @return the search query
     */
    public static PortalSearchInstance of(String contentId, String beginDate, String endDate) {
        var dto = new PortalSearchInstance(beginDate, endDate,
                        List.of("integrity.semanticlab.net/media"), 1,
                        List.of("document.url", "document.fulltext"));
        Map<String, Map<String, String>> queryMap = Maps.newHashMap();
        Map<String, String> searchTerm = Maps.newHashMap();
        searchTerm.put("eq", contentId);
        queryMap.put("contentid", searchTerm);
        dto.setQuery(queryMap);
        return dto;
    }

}
