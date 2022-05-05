package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.station.StationResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "선릉역");

        // when
        ExtractableResponse<Response> response = createPostStationResponse(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 생성시 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "선릉역");

        // when
        createPostStationResponse(params);
        ExtractableResponse<Response> response = createPostStationResponse(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "선릉역");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "잠실역");

        // when
        ExtractableResponse<Response> createResponse1 = createPostStationResponse(params1);
        ExtractableResponse<Response> createResponse2 = createPostStationResponse(params2);
        ExtractableResponse<Response> response = createGetStationResponse();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = postIds(createResponse1, createResponse2);
        List<Long> resultLineIds = responseIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "선릉역");

        // when
        ExtractableResponse<Response> createResponse = createPostStationResponse(params);
        String id = createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = createDeleteStationResponseById(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createPostStationResponse(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createGetStationResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private List<Long> postIds(ExtractableResponse<Response>... createResponse) {
        return Arrays.asList(createResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private List<Long> responseIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> createDeleteStationResponseById(String id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + id)
                .then().log().all()
                .extract();
    }
}
