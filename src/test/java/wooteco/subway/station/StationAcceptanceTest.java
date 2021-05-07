package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.web.dto.StationResponse;

@DisplayName("역 인수 테스트")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final Map<String, String> params1 = new HashMap<>();
    private static final Map<String, String> params2 = new HashMap<>();

    static {
        params1.put("name", "잠실역");
        params2.put("name", "강남역");
    }

    @Test
    @DisplayName("역 생성")
    void createStation() {
        // when
        ExtractableResponse<Response> response = getRequestSpecification()
                .body(params1)
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("중복이름 역 생성불가")
    void cannotCreateStationWithDuplicatedName() {
        // when
        ExtractableResponse<Response> response = getRequestSpecification()
                .body(params1)
                .post("/stations")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = getRequestSpecification()
                .body(params1)
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("역 조회")
    void listStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = getRequestSpecification()
                .body(params1)
                .post("/stations")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> createResponse2 = getRequestSpecification()
                .body(params2)
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> listResponse = getStationListResponse();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<StationResponse> expectedStations = Stream.of(createResponse1, createResponse2)
                .map(response -> response.jsonPath()
                        .getObject(".", StationResponse.class))
                .collect(Collectors.toList());

        List<StationResponse> resultStations = listResponse.jsonPath().getList(".", StationResponse.class);

        for (int i = 0; i < resultStations.size(); i++) {
            StationResponse result = resultStations.get(i);
            StationResponse expected = expectedStations.get(i);
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getName()).isEqualTo(expected.getName());
        }
    }

    @Test
    @DisplayName("역 삭제")
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = getRequestSpecification()
                .body(params1)
                .post("/stations")
                .then().log().all()
                .extract();

        getRequestSpecification()
                .body(params2)
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = getRequestSpecification()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> stationListResponse = getStationListResponse();
        assertThat(stationListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<StationResponse> stationResponses = stationListResponse.jsonPath().getList(".", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(1);
        assertThat(stationResponses.get(0).getName()).isEqualTo(params2.get("name"));
    }

    private ExtractableResponse<Response> getStationListResponse() {
        return getRequestSpecification()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private RequestSpecification getRequestSpecification() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
