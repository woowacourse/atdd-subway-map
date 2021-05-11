package wooteco.subway.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("노선을 생성하면서 구간을 생성한다.")
    void createLineAndSection() throws JsonProcessingException {
        // given
        Station station1 = new Station(1L, "잠실새내역");
        Station station2 = new Station(2L, "잠실역");

        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(station1)
            .when()
            .post("/stations")
            .then().log().all();

        Section section = new Section(station1.getId(), station2.getId(), 5);
        Line line = new Line("1호선", "green");

        LineRequest lineRequest = new LineRequest(line, section);
        LineResponse lineResponse = new LineResponse(1L, "2호선", "green", new ArrayList<>());

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body()).isEqualTo(OBJECT_MAPPER.writeValueAsString(lineResponse));
    }

    @Test
    @DisplayName("기존 노선에 구간을 생성 및 추가한다.")
    void createSection() {
        // given
        Station station1 = new Station(3L, "잠실새내역");
        Station station2 = new Station(4L, "잠실역");
        Section section1 = new Section(station1.getId(), station2.getId(), 5);
        Line line = new Line("2호선", "green");

        LineRequest lineRequest = new LineRequest(line, section1);

        ExtractableResponse<Response> lineResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        long id = Long.parseLong(lineResponse.header("Location").split("/")[2]);

        Station station3 = new Station(5L, "강남역");
        Station station4 = new Station(6L, "역삼역");
        Section section2 = new Section(station3.getId(), station4.getId(), 3);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(section2)
            .when()
            .post("/lines/" + id + "/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED);
    }
}