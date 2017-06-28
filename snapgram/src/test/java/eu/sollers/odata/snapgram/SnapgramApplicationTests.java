package eu.sollers.odata.snapgram;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import feign.Feign;
import feign.RequestLine;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SnapgramApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    public void test_readingService_returnsSth() {
        // GIVEN
        Api client = Feign.builder().target(Api.class, "http://localhost:" + port);

        // WHEN
        String result = client.readService();

        // THEN
        assertThat(result).isNotNull();
    }

    @Test
    public void test_readingMetadata_returnsSth() {
        // GIVEN
        Api client = Feign.builder().target(Api.class, "http://localhost:" + port);

        // WHEN
        String result = client.readMetadata();

        // THEN
        assertThat(result).isNotNull();
    }

    private interface Api {
        @RequestLine("GET /OData.svc/")
        String readService();

        @RequestLine("GET /OData.svc/$metadata")
        String readMetadata();
    }
}
