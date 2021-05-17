package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private StationRequest stationRequest1;
    private StationRequest stationRequest2;

    @BeforeEach
    void init() {
        stationRequest1 = new StationRequest("사당역");
        stationRequest2 = new StationRequest("종합운동장역");
    }

    private ExtractableResponse<Response> postStation(StationRequest params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = postStation(stationRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 역 이름은 XX역으로 끝나지 않으면 예외처리가 된다.")
    @Test
    void invalidStationName() {
        // given
        StationRequest invalidRequest = new StationRequest("역이름은역이라는단어로끝나야함");

        // when
        ExtractableResponse<Response> response = postStation(invalidRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("지하철 역 이름이 잘못되었습니다.");
    }

    @DisplayName("중복된 지하철 역을 생성할 수 없다.")
    @Test
    void cannotCreateDuplicatedStation() {
        // when
        ExtractableResponse<Response> response = postStation(stationRequest1);
        ExtractableResponse<Response> response2 = postStation(stationRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.body().asString()).isEqualTo("중복된 지하철 역입니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void showStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = postStation(stationRequest1);
        ExtractableResponse<Response> createResponse2 = postStation(stationRequest2);

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
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = postStation(stationRequest1);

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

    @DisplayName("노선에 등록된 지하철 역을 제거할 경우 예외처리한다.")
    @Test
    void cannotDeleteWhenStationInLine() {
        // given
        Long upStationId = 1L;
        LineRequest line = new LineRequest("신분당선", "bg-red-600", upStationId, 2L, 10);
        RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/stations/" + upStationId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("해당 지하철 역을 삭제할 수 없습니다.");
    }
}
