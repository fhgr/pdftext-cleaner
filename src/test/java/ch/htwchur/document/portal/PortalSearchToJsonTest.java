package ch.htwchur.document.portal;

import static org.junit.Assert.fail;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PortalSearchToJsonTest {

    @Test
    public void portalSearchToJsonTest() {
        ObjectMapper mapper = new ObjectMapper();
        PortalSearchInstance dto = PortalSearchInstance.of("4855507708076037112", "2020-04-01", "2020-04-07");
        try {
            log.info(mapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            fail();
        }
    }
    
    
}
