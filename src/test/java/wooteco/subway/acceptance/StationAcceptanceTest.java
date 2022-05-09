package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private Station station;
    private ExtractableResponse<Response> response;
    private String stationName = "잠실역";

    @BeforeEach
    void setBefore() {
        // given
        station = new Station(stationName);
        response = RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long resultStationId = Long.parseLong(response.header("Location").split("/")[2]);
        get("/stations/" + resultStationId).then()
                .assertThat()
                .body("id", equalTo(resultStationId.intValue()))
                .body("name", equalTo(stationName));
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(station)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Station station2 = new Station("역삼역");
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(station2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> stationResponse = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(stationResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(response, response2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = stationResponse.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // when
        String uri = response.header("Location");
        String resultLineId = uri.split("/")[2];
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        get("/stations/" + resultLineId).then()
                .assertThat()
                .body("message", equalTo("해당하는 역이 존재하지 않습니다."));
    }
}
