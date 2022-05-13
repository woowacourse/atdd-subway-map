package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {

        // when
        ExtractableResponse<Response> response = requestToCreateStation("강남역");

        StationResponse stationResponse = response.jsonPath()
                .getObject(".", StationResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(stationResponse.getId()).isEqualTo(1L);
        assertThat(stationResponse.getName()).isEqualTo("강남역");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하려하면 예외를 발생시킨다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        requestToCreateStation("강남역");

        // when
        ExtractableResponse<Response> response = requestToCreateStation("강남역");

        final ExceptionResponse exceptionResponse = response.jsonPath()
                .getObject(".", ExceptionResponse.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(exceptionResponse.getExceptionMessage()).isEqualTo("이미 존재하는 역 이름입니다.")
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations_success() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestToCreateStation("강남역");
        ExtractableResponse<Response> createResponse2 = requestToCreateStation("역삼역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation_badRequest() {
        // given
        ExtractableResponse<Response> createResponse = requestToCreateStation("강남역");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestToDeleteStation(1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 역을 삭제하려고 하면 badRequest를 응답한다.")
    @Test
    void deleteStation() {
        // given
        requestToCreateStation("강남역");
        Long invalidStationId = 2L;

        // when
        ExtractableResponse<Response> response = requestToDeleteStation(invalidStationId);

        final ExceptionResponse exceptionResponse = response.jsonPath()
                .getObject(".", ExceptionResponse.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(exceptionResponse.getExceptionMessage()).isEqualTo("존재하지 않는 역입니다.")
        );
    }

    private ExtractableResponse<Response> requestToCreateStation(String name) {
        return RestAssured.given().log().all()
                .body(Map.of("name", name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> requestToDeleteStation(Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + stationId)
                .then().log().all()
                .extract();
    }
}
