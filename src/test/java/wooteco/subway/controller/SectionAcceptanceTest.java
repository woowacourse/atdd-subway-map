package wooteco.subway.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
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

    private LineResponse lineResponse;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 3L, 5);

        lineResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .post("/lines")
            .then().log().all()
            .extract()
            .as(LineResponse.class);
    }

    @Test
    @DisplayName("기존 노선에 새 구간을 연결한다.")
    void createSection() {
        // given
        long id = lineResponse.getId();
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 3);

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


    @Test
    @DisplayName("기존 구간의 중간에 새로운 구간을 삽입한다.")
    void createMiddleSection() {
        // given
        long id = lineResponse.getId();
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

    @Test
    @DisplayName("거리 제한으로 중간에 구간을 삽입할 수 없다.")
    void failToCreateMiddleSection() {
        // given
        long id = lineResponse.getId();
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 8);

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when()
            .post("/lines/" + id + "/sections")
            .then().log().all()
            .statusCode(400);  // then
    }

    @Test
    @DisplayName("두 역이 노선에 없으면 구간을 생성할 수 없다.")
    void failToCreateNotExist() {
        long id = lineResponse.getId();
        SectionRequest sectionRequest = new SectionRequest(4L, 5L, 3);

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when()
            .post("/lines/" + id + "/sections")
            .then().log().all()
            .statusCode(400);  // then
    }

    @Test
    @DisplayName("이미 존재하는 구간을 넣을 수 없다.")
    void failToCreateSectionExist() {
        long id = lineResponse.getId();
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 3);

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when()
            .post("/lines/" + id + "/sections")
            .then().log().all()
            .statusCode(400);
    }

    @Test
    @DisplayName("하나의 역으로 구간을 생성할 수 없다.")
    void failToCreateSection() {
        // given
        long id = lineResponse.getId();
        SectionRequest sectionRequest = new SectionRequest(3L, 3L, 3);

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when()
            .post("/lines/" + id + "/sections")
            .then().log().all()
            .statusCode(400);
    }
}