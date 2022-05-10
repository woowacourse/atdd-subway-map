package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("새로운 노선을 생성한다.")
    void createLine() {
        // given
        createStationResponse(new StationRequest("강남역"));
        createStationResponse(new StationRequest("선릉역"));
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("새로운 구간을 추가한다.(맨앞)")
    void addSection_First() {
        // given
        createLineResponse(new LineRequest("2호선", "green", 1L, 2L, 10));
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("새로운 구간을 추가한다.(사이)")
    void addSection_Middle() {
        // given
        createLineResponse(new LineRequest("2호선", "green", 1L, 2L, 10));
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("새로운 구간을 추가한다.(맨뒤)")
    void addSection_End() {
        // given
        createLineResponse(new LineRequest("2호선", "green", 1L, 2L, 10));
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("현재 등록된 노선 전체를 불러온다.")
    void findAllLine() {
        //given
        ExtractableResponse<Response> line1 = createLineResponse(new LineRequest("2호선", "green", 1L, 2L, 10));
        ExtractableResponse<Response> line2 = createLineResponse(new LineRequest("3호선", "yellow", 1L, 2L, 10));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        //then
        List<Long> expectedLineIds = Arrays.asList(line1, line2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("등록된 노선들 중 입력된 id값과 일치하는 노선을 반환한다. ")
    void findLindById() {
        //given
        createLineResponse(new LineRequest("2호선", "green", 1L, 2L, 10));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("등록된 노선을 수정한다.")
    void update() {
        //given
        createLineResponse(new LineRequest("2호선", "green", 1L, 2L, 10));

        //when
        LineRequest lineRequest = new LineRequest("3호선","yellow");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

//    @Test
//    @DisplayName("등록된 노선을 삭제한다.")
//    void delete() {
//        //given
//        Map<String, String> params = new HashMap<>();
//        params.put("name", "신분당선");
//        params.put("color", "bg-red-600");
//        params.put("upStationId", "1");
//        params.put("downStationId", "2");
//        params.put("distance", "10");
//
//        RestAssured.given().log().all()
//                .body(params)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .post("/lines")
//                .then().log().all()
//                .extract();
//
//        //when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .delete("/lines/1")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
//    }

    private ExtractableResponse<Response> createLineResponse(LineRequest lineRequest) {
        createStationResponse(new StationRequest("강남역"));
        createStationResponse(new StationRequest("선릉역"));
        createStationResponse(new StationRequest("잠실역"));

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createStationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
