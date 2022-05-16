package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.fixture.LineFixture.createLineResponse;
import static wooteco.subway.fixture.StationFixture.getSavedStationId;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;

class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("라인을 등록한다.")
    @Test
    void createLine() {
        // when
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> response = createLineResponse(lineRequest);

        // then
        List<String> resultStationNames = response.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getLong("id")).isNotZero(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("5호선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("rgb-purple-600"),
                () -> assertThat(resultStationNames).containsExactly("상일동역", "아차산역")
        );
    }

    @DisplayName("라인을 등록 시에 현재 없는 역을 종점으로 입력하면 예외가 발생한다.")
    @Test
    void createLineWithWrongStation() {
        // when
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest wrongLineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId + 1, 10);
        ExtractableResponse<Response> response = createLineResponse(wrongLineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 에러가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        createLineResponse(lineRequest);

        // when
        ExtractableResponse<Response> response = createLineResponse(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 노선들을 조회한다.")
    @Test
    void findAllLines() {
        /// given
        Long upStationId1 = getSavedStationId("상일동역");
        Long downStationId1 = getSavedStationId("아차산역");
        LineRequest lineRequest1 = new LineRequest("5호선", "rgb-purple-600", upStationId1, downStationId1, 10);
        ExtractableResponse<Response> createResponse1 = createLineResponse(lineRequest1);

        Long upStationId2 = getSavedStationId("선릉역");
        Long downStationId2 = getSavedStationId("강남역");
        LineRequest lineRequest2 = new LineRequest("2호선", "rgb-green-600", upStationId2, downStationId2, 10);
        ExtractableResponse<Response> createResponse2 = createLineResponse(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<LineResponse> resultLine = new ArrayList<>(response.jsonPath()
                .getList(".", LineResponse.class));

        List<Long> resultLineIds = resultLine.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        List<String> resultStationNames = resultLine.stream()
                .map(LineResponse::getStations)
                .flatMap(List::stream)
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds),
                () -> assertThat(resultStationNames).containsAll(List.of("상일동역", "아차산역", "강남역", "선릉역"))
        );
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void findLine() {
        /// given
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        List<String> resultStationNames = response.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(id),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("5호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("rgb-purple-600"),
                () -> assertThat(resultStationNames).containsExactly("상일동역", "아차산역")
        );
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "6호선");
        updateParams.put("color", "rgb-brown-600");
        RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(id),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("6호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("rgb-brown-600")
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void createSection() {
        // given
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        Long addStationId = getSavedStationId("군자역");

        // when
        SectionRequest sectionRequest = new SectionRequest(downStationId, addStationId, 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        List<String> resultStationNames = findResponse.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultStationNames).containsExactly("상일동역", "아차산역", "군자역")
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 있는 지하철역을 삭제한다.")
    @Test
    void deleteStationInSection() {
        // given
        Long upStationId = getSavedStationId("상일동역");
        Long downStationId = getSavedStationId("아차산역");
        LineRequest lineRequest = new LineRequest("5호선", "rgb-purple-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        Long addStationId = getSavedStationId("군자역");
        SectionRequest sectionRequest = new SectionRequest(downStationId, addStationId, 10);

        RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id + "/sections?stationId=" + downStationId)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        List<String> resultStationNames = response.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultStationNames).containsExactly("상일동역", "군자역")
        );
    }
}
