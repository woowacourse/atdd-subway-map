package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    public <T> ExtractableResponse<Response> post_요청을_보냄(String url, T body) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post(url)
                .then().log().all()
                .extract();
    }

    public <T> ExtractableResponse<Response> post_요청을_보냄(String url, T body, Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post(url, id)
                .then().log().all()
                .extract();
    }


    public ExtractableResponse<Response> get_요청을_보냄(String url) {
        return RestAssured
                .given().log().all()
                .when().get(url)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> get_요청을_보냄(String url, Long id) {
        return RestAssured
                .given().log().all()
                .when().get(url, id)
                .then().log().all()
                .extract();
    }

    public <T> ExtractableResponse<Response> put_요청을_보냄(String url, T body, Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().put(url, id)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> delete_요청을_보냄(String url, Long id) {
        return RestAssured
                .given().log().all()
                .when().delete(url, id)
                .then().log().all()
                .extract();
    }
}
