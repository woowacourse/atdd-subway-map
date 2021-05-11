package wooteco.subway.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
@Sql("/station.init.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("노선을 생성하면서 구간을 생성한다.")
    void createLineAndSection() throws JsonProcessingException {
        // given
        Station station1 = new Station(1L, "잠실역");
        Station station2 = new Station(2L, "잠실새내역");

        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 5);
        LineResponse lineResponse = new LineResponse(1L, "2호선", "green",
            Arrays.asList(
                new StationResponse(station1),
                new StationResponse(station2)
            )
        );

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().asString())
            .isEqualTo(OBJECT_MAPPER.writeValueAsString(lineResponse));
    }

    @Test
    @DisplayName("기존 노선에 구간을 생성 및 추가한다.")
    void createSection() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 5);

        ExtractableResponse<Response> lineResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        long id = Long.parseLong(lineResponse.header("Location").split("/")[2]);
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 3);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when()
            .post("/lines/" + id + "/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(201);
    }
}