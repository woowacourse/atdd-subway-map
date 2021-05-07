package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@Sql("/truncate.sql")
@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> response;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        response = RestAssured.given().log().all()
            .body(new StationRequest("강남역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().as(StationResponse.class).getName()).isEqualTo("강남역");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        ExtractableResponse<Response> duplicateResponse = RestAssured.given().log().all()
            .body(new StationRequest("강남역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then()
            .log().all()
            .extract();

        assertThat(duplicateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> resultLineNames = response.jsonPath().getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getName)
            .collect(Collectors.toList());
        assertThat(resultLineNames).hasSize(1);
        assertThat(resultLineNames).containsExactly("강남역");
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        String uri = response.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
