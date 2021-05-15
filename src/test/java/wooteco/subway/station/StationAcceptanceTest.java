package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.api.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String stationName = "강남역";

        // when
        ExtractableResponse<Response> response = 지하철역_저장_후_응답(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "강남역";
        지하철역_저장_후_응답(stationName);

        // when
        ExtractableResponse<Response> response = 지하철역_저장_후_응답(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 역 이름입니다.");
    }

    @DisplayName("잘못된 요청값으로 지하철역 생성 요청시, 예외처리")
    @Test
    void createStationFailByNotValidatedRequest() {
        // given
        String wrongStationName = "1";

        // when
        ExtractableResponse<Response> response = 지하철역_저장_후_응답(wrongStationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("역 이름은 최소 2글자 이상이어야 합니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        String stationName1 = "강남역";
        ExtractableResponse<Response> createResponse1 = 지하철역_저장_후_응답(stationName1);

        String stationName2 = "역삼역";
        ExtractableResponse<Response> createResponse2 = 지하철역_저장_후_응답(stationName2);

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
        String stationName = "강남역";
        ExtractableResponse<Response> createResponse = 지하철역_저장_후_응답(stationName);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철역_삭제_후_응답(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 역 삭제 요청 시, 예외 처리 기능")
    @Test
    void deleteIfNotExistStationId() {
        //given
        String stationName = "강남역";
        ExtractableResponse<Response> createResponse = 지하철역_저장_후_응답(stationName);

        //when
        Long id = createResponse.body().jsonPath().getObject(".", StationResponse.class).getId();

        ExtractableResponse<Response> response = 지하철역_삭제_후_응답("/stations/" + (id + 1));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("존재하지 않는 역 ID 입니다.");
    }

    private ExtractableResponse<Response> 지하철역_저장_후_응답(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철역_삭제_후_응답(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then()
            .log().all()
            .extract();
    }

}
