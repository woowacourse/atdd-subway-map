package wooteco.subway.acceptance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.ui.request.StationRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    ExtractableResponse<Response> getExtractableGetResponse(String url) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    ExtractableResponse<Response> getExtractablePostResponse(Object request, String url) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(url)
            .then().log().all()
            .extract();
    }

    ExtractableResponse<Response> getExtractablePutResponse(Object request, String url) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(url)
            .then().log().all()
            .extract();
    }

    ExtractableResponse<Response> getExtractableDeleteResponse(String url) {
        return RestAssured.given().log().all()
            .when()
            .delete(url)
            .then().log().all()
            .extract();
    }

    List<Long> postStations(String... StationNames) {
        return Arrays.stream(StationNames)
            .map(name -> getIdFrom(getExtractablePostResponse(new StationRequest(name), "/stations")))
            .collect(Collectors.toList());
    }

    long getIdFrom(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
