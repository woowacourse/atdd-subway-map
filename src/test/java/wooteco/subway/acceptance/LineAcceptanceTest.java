package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixtures.BLUE;
import static wooteco.subway.Fixtures.GANGNAM;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.LINE_2;
import static wooteco.subway.Fixtures.LINE_4;
import static wooteco.subway.Fixtures.RED;
import static wooteco.subway.Fixtures.SINSA;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;

@Nested
@DisplayName("지하철 노선 관리 API")
public class LineAcceptanceTest extends AcceptanceTest {

    /*
        Scenario: 지하철 노선 등록
            When: 지하철 노선 등록을 요청한다.
            Then: 지하철 노선이 생성된다.
            And: 201 상태, 지하철 노선 정보, 관련 역 정보, 저장 경로를 응답 받는다.
     */
    @Test
    @DisplayName("지하철 노선을 등록한다.")
    void create() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", LINE_2);
        params.put("color", RED);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        final List<StationResponse> stationResponses = response.body().jsonPath()
                .getList("stations", StationResponse.class);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
            assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_2);
            assertThat(response.body().jsonPath().getString("color")).isEqualTo(RED);
            assertThat(stationResponses).hasSize(2);
            assertThat(stationResponses.get(0).getName()).isEqualTo(HYEHWA);
            assertThat(stationResponses.get(1).getName()).isEqualTo(SINSA);
        });
    }

    /*
        Scenario: 중복된 지하철 노선 등록
            When: 지하철 노선 등록을 요청한다.
            Then: 같은 이름의 지하철 노선 등록을 요청한다.
            And: 400 상태, 에러 메시지를 응답 받는다.
     */
    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 생성하면, 예외를 발생한다.")
    void createWithDuplicateName() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", LINE_2);
        params.put("color", RED);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /*
        Scenario: 지하럴 노선 목록 조회
            When: 지하철 노선 목록 조회를 요청한다.
            Then: 200 상태, 모든 지하철 역 정보, 관련 역 종보를 응답 받는다.
     */
    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void showAll() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params1 = new HashMap<>();
        params1.put("name", LINE_2);
        params1.put("color", RED);
        params1.put("upStationId", upStationId);
        params1.put("downStationId", downStationId);
        params1.put("distance", 10);
        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all();

        final Map<String, Object> params2 = new HashMap<>();
        params2.put("name", LINE_4);
        params2.put("color", BLUE);
        params2.put("upStationId", upStationId);
        params2.put("downStationId", downStationId);
        params2.put("distance", 10);
        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        final List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(lineResponses.get(0).getName()).isEqualTo(LINE_2);
            assertThat(lineResponses.get(0).getColor()).isEqualTo(RED);
            assertThat(lineResponses.get(0).getStations().get(0).getName()).isEqualTo(HYEHWA);
            assertThat(lineResponses.get(0).getStations().get(1).getName()).isEqualTo(SINSA);
            assertThat(lineResponses.get(1).getName()).isEqualTo(LINE_4);
            assertThat(lineResponses.get(1).getColor()).isEqualTo(BLUE);
            assertThat(lineResponses.get(1).getStations().get(0).getName()).isEqualTo(HYEHWA);
            assertThat(lineResponses.get(1).getStations().get(1).getName()).isEqualTo(SINSA);
        });
    }

    /*
        Scenario: 지하럴 노선 조회
            When: 지하철 노선 조회를 요청한다.
            Then: 200 상태, 지하철 역 정보, 관련 역 종보를 응답 받는다.
     */
    @Test
    @DisplayName("지하철 노선 ID로 노선을 조회한다.")
    void show() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", LINE_2);
        params.put("color", RED);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        final long id = Long.parseLong(RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("Location").split("/")[2]);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        final List<StationResponse> stationResponses = response.body().jsonPath()
                .getList("stations", StationResponse.class);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_2);
            assertThat(response.body().jsonPath().getString("color")).isEqualTo(RED);
            assertThat(stationResponses).hasSize(2);
            assertThat(stationResponses.get(0).getName()).isEqualTo(HYEHWA);
            assertThat(stationResponses.get(1).getName()).isEqualTo(SINSA);
        });
    }

    /*
        Scenario: 없는 지하철 노선 조회
            When: 없는 지하철 노선 조회를 요청한다.
            Then: 404 상태, 에러 메시지를 응답 받는다.
     */
    @Test
    @DisplayName("존재하지 않는 지하철 노선 ID로 조회한다면, 예외를 발생한다.")
    void getLineNotExistId() {
        // given
        final long id = 1L;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /*
        Scenario: 지하철 노선 수정
            When: 지하철 노선 수정을 요청한다.
            Then: 지하철 노선이 수정된다.
            And: 200 상태를 응답한다.
     */
    @Test
    @DisplayName("노선을 업데이트 한다.")
    void update() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> saveParams = new HashMap<>();
        saveParams.put("name", LINE_2);
        saveParams.put("color", RED);
        saveParams.put("upStationId", upStationId);
        saveParams.put("downStationId", downStationId);
        saveParams.put("distance", 10);
        final long id = Long.parseLong(RestAssured.given().log().all()
                .body(saveParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("Location").split("/")[2]);

        final Map<String, String> params = new HashMap<>();
        params.put("name", LINE_4);
        params.put("color", BLUE);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        Scenario: 없는 지하철 노선 수정
            When: 없는 지하철 노선 수정을 요청한다.
            Then: 404 상태, 에러 메시지를 응답 받는다.
     */
    @Test
    @DisplayName("존재하지 않는 ID로 업데이트 한다면, 예외를 발생한다.")
    void updateNotExistId() {
        // given
        final long id = 100L;
        final Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "bg-blue-500");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /*
        Scenario: 지하철 노선 제거
            When: 지하철 노선 제거를 요청한다.
            Then: 지하철 노선이 제거된다.
            And: 204 상태를 응답 받는다.
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void delete() {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Map<String, Object> saveParams = new HashMap<>();
        saveParams.put("name", LINE_2);
        saveParams.put("color", RED);
        saveParams.put("upStationId", upStationId);
        saveParams.put("downStationId", downStationId);
        saveParams.put("distance", 10);
        final long id = Long.parseLong(RestAssured.given().log().all()
                .body(saveParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("Location").split("/")[2]);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /*
        Scenario: 없는 지하철 노선 제거
            When: 없는 지하철 노선 제거을 요청한다.
            Then: 404 상태, 에러 메시지를 응답 받는다.
     */
    @Test
    @DisplayName("존재하지 않는 ID로 삭제한다면, 예외를 발생한다.")
    void deleteLineNotExistId() {
        // given
        final long id = 1L;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Nested
    @DisplayName("지하철 구간을 등록한다.")
    class CreateSectionTest extends AcceptanceTest {

        /*
            Scenario: 지하철 구간 등록
                When: 지하철 구간 등록을 요청한다.
                Then: 지하철 구간이 생성된다.
                And: 200 상태를 응답 받는다.
         */
        @Test
        @DisplayName("노선의 끝에 구간을 추가한다. - 성공 200")
        void createSection1() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId2);
            params.put("downStationId", stationId3);
            params.put("distance", 10);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("노선의 처음에 구간을 추가한다. - 성공 200")
        void createSection2() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId2, stationId3, 10);

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId1);
            params.put("downStationId", stationId2);
            params.put("distance", 10);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("상행역이 겹치는 구간을 등록한다. - 성공 200")
        void createSection3() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId3, 10);

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId1);
            params.put("downStationId", stationId2);
            params.put("distance", 7);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("하행역이 겹치는 구간을 등록한다. - 성공 200")
        void createSection4() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId3, 10);

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId2);
            params.put("downStationId", stationId3);
            params.put("distance", 1);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("상/하행역이 겹치는 구간을 등록할 때, 기존보다 긴 구간을 등록한다. - 실패 400")
        void createSection5() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId3, 10);

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId1);
            params.put("downStationId", stationId2);
            params.put("distance", 15);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("라인이 없는 경우 - 실패 404")
        void createSection6() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final long lineId = 10L;

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId1);
            params.put("downStationId", stationId2);
            params.put("distance", 15);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("역이 없는 경우 - 실패 404")
        void createSection7() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);

            final Map<String, Object> params = new HashMap<>();
            params.put("upStationId", stationId2);
            params.put("downStationId", 3L);
            params.put("distance", 15);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/" + lineId + "/sections")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }


    @Nested
    @DisplayName("지하철 구간을 삭제한다.")
    class DeleteSectionTest extends AcceptanceTest {

        /*
            Scenario: 지하철 구간 제거
                When: 지하철 구간 제거를 요청한다.
                Then: 지하철 구간이 제거된다.
                And: 200 상태를 응답 받는다.
         */
        @Test
        @DisplayName("구간을 삭제한다. - 성공 200")
        void deleteSection1() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);
            createSection(lineId, stationId2, stationId3, 10);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .delete("/lines/" + lineId + "/sections?stationId=" + stationId1)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("없는 라인의 구간을 삭제한다. - 실패 404")
        void deleteSections2() {
            // given
            final Long stationId = createStation(HYEHWA);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .delete("/lines/" + 10L + "/sections?stationId=" + stationId)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("없는 역의 구간을 삭제한다. - 실패 404")
        void deleteSections3() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long stationId3 = createStation(GANGNAM);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);
            createSection(lineId, stationId2, stationId3, 10);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .delete("/lines/" + lineId + "/sections?stationId=" + 10L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("삭제할 수 없는 구간을 삭제한다. - 실패 400")
        void deleteSections4() {
            // given
            final Long stationId1 = createStation(HYEHWA);
            final Long stationId2 = createStation(SINSA);
            final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);

            // when
            final ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .delete("/lines/" + lineId + "/sections?stationId=" + stationId1)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    private Long createStation(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .body().jsonPath().getLong("id");
    }

    private Long createLine(final String name, final String color, final Long upStationId, final Long downStationId,
                            final int distance) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return Long.parseLong(RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("Location").split("/")[2]);
    }

    private void createSection(final Long lineId, final Long upStationId, final Long downStationId,
                               final int distance) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
