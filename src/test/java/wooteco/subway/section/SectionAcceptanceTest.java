package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@Sql("/truncate.sql")
@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> response;
    private long upId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        upId = RestAssured.given().log().all()
            .body(new StationRequest("강남역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract().as(StationResponse.class)
            .getId();

        long downId = RestAssured.given().log().all()
            .body(new StationRequest("역삼역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract().as(StationResponse.class)
            .getId();

        response = RestAssured.given().log().all()
            .body(new LineRequest("분당선", "bg-yellow-600", upId, downId, 1))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("종점이 아닌 역을 제거하면, 양끝에 역간의 거리가 합해진 새 구간이 생긴다.")
    @Test
    void deleteSectionNotEndPoint() {

    }

    @DisplayName("구간이 2개 이상일 때, 종점인 역을 제거한다.")
    @Test
    void deleteSectionEndPoint() {

    }

    @DisplayName("구간이 1개일 때, 종점인 역을 제거할 수 없다.")
    @Test
    void deleteUniqueSectionEndPoint() {
        ExtractableResponse<Response> actualResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/{id}/sections?stationId={upId}", response.header("Location").split("/")[2], upId)
            .then().log().all()
            .extract();

        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
