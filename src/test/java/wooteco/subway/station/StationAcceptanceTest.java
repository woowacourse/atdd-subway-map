package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철역 E2E 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String NOT_INPUT_MESSAGE = "[ERROR] 입력값이 존재하지 않습니다.";

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> response = createStationAPI("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    void createStationWithDuplicateName() {
        // given
        createStationAPI("강남역");

        // when
        ExtractableResponse<Response> response = createStationAPI("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("[ERROR] 중복된 이름입니다.");
    }

    @Test
    @DisplayName("null을 입력하여 역을 생성하면 에러가 출력된다.")
    void createLineWithDataNull() {
        //when
        ExtractableResponse<Response> response = createStationAPI(null);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(NOT_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("공백을 입력하여 역을 생성하면 에러가 출력된다.")
    void createLineWithDataSpace() {
        ExtractableResponse<Response> response = createStationAPI("    ");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(NOT_INPUT_MESSAGE);
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        /// given
        createStationAPI("강남역");
        createStationAPI("역삼역");

        // when
        ExtractableResponse<Response> response = getStationAllAPI();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Arrays.asList(1L, 2L);
        List<Long> resultLineIds = getResultLineIds(response);

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = createStationAPI("강남역");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = deleteStationAPI("/stations/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철역이 구간에 포함되어 있을때 역을 제거하면 에러가 발생한다.")
    void deleteStationWithUseStation() {
        // given
        createStationAPI("강남역");
        createStationAPI("잠실역");
        createLineAPI(new LineRequest("2호선", "green", 1L, 2L, 10));

        // when
        ExtractableResponse<Response> response = deleteStationAPI("/stations/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("[ERROR] 현재 사용중인 아이템이어서 삭제할 수 없습니다.");
    }

    private ExtractableResponse<Response> createStationAPI(String name) {
        StationRequest stationRequest = new StationRequest(name);
        return RestAssured.given()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> deleteStationAPI(String uri) {
        return RestAssured.given()
            .when()
            .delete(uri)
            .then()
            .extract();
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> getStationAllAPI() {
        return RestAssured.given()
            .when()
            .get("/stations")
            .then()
            .extract();
    }
}
