package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineEditRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void 노선_생성() {
        // given
        Long upStationId = postStationThenReturnId("강남역");
        Long downStationId = postStationThenReturnId("선릉역");
        LineRequest lineRequest = new LineRequest(
                "2호선", "bg-green-600", upStationId, downStationId, 10);

        // when
        ExtractableResponse<Response> response = postToLines(lineRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선")
        );
    }

    private ExtractableResponse<Response> postToLines(LineRequest lineRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    private ExtractableResponse<Response> getLine(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        return response;
    }

    private Long postStationThenReturnId(String name) {
        StationRequest request = new StationRequest(name);
        Long stationId = RestAssured.given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .jsonPath().getLong("id");
        return stationId;
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외 발생")
    @Test
    void 존재하는_노선_이름_생성_예외() {
        // given
        Long upStationId = postStationThenReturnId("강남역");
        Long downStationId = postStationThenReturnId("선릉역");
        LineRequest lineRequest = new LineRequest(
                "2호선", "bg-green-600", upStationId, downStationId, 10);
        postToLines(lineRequest);

        // when
        Long otherStationId = postStationThenReturnId("잠실역");
        LineRequest duplicatedLineNameRequest = new LineRequest(
                "2호선", "bg-red-600", downStationId, otherStationId, 10);

        ExtractableResponse<Response> response = postToLines(duplicatedLineNameRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void 전체_노선_조회() {
        // given
        Long 강남역Id = postStationThenReturnId("강남역");
        Long 선릉역Id = postStationThenReturnId("선릉역");
        LineRequest lineRequest1 = new LineRequest(
                "2호선", "bg-green-600", 강남역Id, 선릉역Id, 3);
        Long lineId1 = postToLines(lineRequest1).jsonPath().getLong("id");

        Long 양재역Id = postStationThenReturnId("양재역");
        LineRequest lineRequest2 = new LineRequest(
                "신분당선", "bg-green-600", 강남역Id, 양재역Id, 3);
        Long lineId2 = postToLines(lineRequest2).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = List.of(lineId1, lineId2);
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("단일 노선을 조회한다.")
    @Test
    void 단일_노선_조회() {
        //given
        Long 강남역Id = postStationThenReturnId("강남역");
        Long 선릉역Id = postStationThenReturnId("선릉역");
        LineRequest lineRequest1 = new LineRequest(
                "2호선", "bg-green-600", 강남역Id, 선릉역Id, 3);
        Long lineId = postToLines(lineRequest1).jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("bg-green-600"),
                () -> assertThat(response.jsonPath().getLong("stations[0].id")).isEqualTo(강남역Id)
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void 노선_수정() {
        // given
        Long 강남역Id = postStationThenReturnId("강남역");
        Long 선릉역Id = postStationThenReturnId("선릉역");
        LineRequest lineRequest1 = new LineRequest(
                "2호선", "bg-green-600", 강남역Id, 선릉역Id, 3);
        Long lineId = postToLines(lineRequest1).jsonPath().getLong("id");

        // when
        LineEditRequest request = new LineEditRequest("3호선", "bg-green-300");

        // then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void 노선_삭제() {
        // given
        Long 강남역Id = postStationThenReturnId("강남역");
        Long 선릉역Id = postStationThenReturnId("선릉역");
        LineRequest lineRequest = new LineRequest(
                "2호선", "bg-green-600", 강남역Id, 선릉역Id, 3);
        Long lineId = postToLines(lineRequest).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간을 추가한다")
    @Test
    void 구간_추가() {
        // given
        Long 강남역Id = postStationThenReturnId("강남역");
        Long 선릉역Id = postStationThenReturnId("선릉역");
        Long 역삼역Id = postStationThenReturnId("역삼역");
        LineRequest lineRequest = new LineRequest(
                "2호선", "bg-green-600", 강남역Id, 선릉역Id, 3);
        Long lineId = postToLines(lineRequest).jsonPath().getLong("id");

        // when
        SectionRequest request = new SectionRequest(역삼역Id, 선릉역Id, 1);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간을 삭제한다")
    @Test
    void 구간_삭제() {
        // given
        Long 강남역Id = postStationThenReturnId("강남역");
        Long 선릉역Id = postStationThenReturnId("선릉역");
        Long 역삼역Id = postStationThenReturnId("역삼역");
        LineRequest lineRequest = new LineRequest(
                "2호선", "bg-green-600", 강남역Id, 선릉역Id, 3);
        Long lineId = postToLines(lineRequest).jsonPath().getLong("id");

        // when
        SectionRequest request = new SectionRequest(역삼역Id, 선릉역Id, 1);

        RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + 선릉역Id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
