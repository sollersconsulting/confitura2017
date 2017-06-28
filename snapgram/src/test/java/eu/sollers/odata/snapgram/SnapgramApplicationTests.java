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
    public void test_pingUrl_returnsPong() {
        // GIVEN
        Api client = Feign.builder().target(Api.class, "http://localhost:" + port);

        // WHEN
        String result = client.ping();

        // THEN
        assertThat(result).isEqualTo("PONG");
    }

    private interface Api {
        @RequestLine("GET /ping")
        String ping();
    }
}
