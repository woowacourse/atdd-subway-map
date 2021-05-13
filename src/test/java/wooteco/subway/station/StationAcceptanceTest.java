package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.util.RestfulOrder.DEFAULT_MEDIA_TYPE;
import static wooteco.subway.util.RestfulOrder.testRequest;
import static wooteco.subway.util.RestfulOrder.testResponse;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    public void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> response = testResponse(params, DEFAULT_MEDIA_TYPE,
            "/stations");
        // when

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        testRequest(params, DEFAULT_MEDIA_TYPE, "/stations");
        // when
        ExtractableResponse<Response> response = testResponse(params,
            DEFAULT_MEDIA_TYPE, "/stations");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        ExtractableResponse<Response> createResponse1 = testResponse(params1, DEFAULT_MEDIA_TYPE,
            "/stations");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        ExtractableResponse<Response> createResponse2 = testResponse(params2, DEFAULT_MEDIA_TYPE,
            "/stations");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> createResponse = testResponse(params,
            DEFAULT_MEDIA_TYPE, "/stations");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("중복된 역 이름 추가시 예외 처리")
    @Test
    void nameDuplication() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        testResponse(params, DEFAULT_MEDIA_TYPE, "/stations");

        Map<String, String> params2 = new HashMap<>();
        ExtractableResponse<Response> createResponse2 = testResponse(params,
            DEFAULT_MEDIA_TYPE, "/stations");

        assertThat(createResponse2.statusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }


}
