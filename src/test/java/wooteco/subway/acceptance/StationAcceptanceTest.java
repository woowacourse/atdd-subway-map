package wooteco.subway.acceptance;

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
import wooteco.subway.ui.dto.StationRequest;
import wooteco.subway.ui.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final StationRequest GANGNAM_REQUEST = new StationRequest("강남역");
    private static final StationRequest YEOKSAM_REQUEST = new StationRequest("역삼역");

    @DisplayName("신규 지하철역 생성 성공 시, 응답코드는 CREATE이고 응답헤더에 Location이 존재한다")
    @Test
    void createStation() {
        // given
        final String newStationRequestJson = toJson(GANGNAM_REQUEST);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newStationRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 이름으로 지하철역 생성 시도 시 생성되지 않고 응답코드는 BAD_REQUEST 이다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final String newStationRequestJson = toJson(GANGNAM_REQUEST);

        RestAssured.given().log().all()
                .body(newStationRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newStationRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철역을 조회할 수 있으며, 응답코드는 OK이다")
    @Test
    void getStations() {
        /// given
        final String gangNamStationRequestJson = toJson(GANGNAM_REQUEST);

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(gangNamStationRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        final String yeokSamStationRequestJson = toJson(YEOKSAM_REQUEST);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(yeokSamStationRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

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
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("ID로 지하철역을 삭제할 수 있으며, 삭제 성공 시 응답코드는 NO_CONTENT 이다")
    @Test
    void deleteStation() {
        // given
        final String newStationRequestJson = toJson(GANGNAM_REQUEST);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(newStationRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

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
}
