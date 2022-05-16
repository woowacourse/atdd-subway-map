package wooteco.subway.acceptance;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        String name = "2호선";
        String color = "bg-green-600";
        Long upStationId = generateStationId("선릉역");
        Long downStationId = generateStationId("잠실역");
        Integer distance = 7;

        ExtractableResponse<Response> response = generateLine(name, color, upStationId, downStationId, distance);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선 관리")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromLine() {
        String name1 = "1호선";
        String color1 = "bg-blue-600";
        Long upStationId1 = generateStationId("중동역");
        Long downStationId1 = generateStationId("신도림역");
        Integer distance1 = 10;

        String name2 = "2호선";
        String color2 = "bg-green-600";
        Long upStationId2 = generateStationId("선릉역");
        Long downStationId2 = generateStationId("잠실역");
        Integer distance2 = 10;

        ExtractableResponse<Response> createdResponse1 = generateLine(name1, color1, upStationId1, downStationId1,
                distance1);
        ExtractableResponse<Response> createdResponse2 = generateLine(name2, color2, upStationId2,
                downStationId2, distance2);

        return Stream.of(
                dynamicTest("노선을 조회한다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .get("/lines")
                            .then().log().all()
                            .extract();

                    List<Long> expectedLineIds = List.of(createdResponse1, createdResponse2)
                            .stream()
                            .map(ExtractableResponse::response)
                            .map(ResponseBodyExtractionOptions::jsonPath)
                            .map(it -> it.getLong("id"))
                            .collect(toList());
                    List<Long> resultLineIds = response.jsonPath()
                            .getList(".", LineResponse.class)
                            .stream()
                            .map(it -> it.getId())
                            .collect(toList());
                    assertAll(
                            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
                    );
                }),

                dynamicTest("단일 노선을 조회한다.", () -> {
                    JsonPath createdResponseJsonPath = createdResponse1.body().jsonPath();
                    Long id = createdResponseJsonPath.getLong("id");
                    String name = createdResponseJsonPath.getString("name");
                    String color = createdResponseJsonPath.getString("color");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .get("/lines/" + id)
                            .then().log().all()
                            .extract();

                    JsonPath responseJsonPath = response.body().jsonPath();
                    assertAll(
                            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                            () -> assertThat(responseJsonPath.getLong("id")).isEqualTo(id),
                            () -> assertThat(responseJsonPath.getString("name")).isEqualTo(name),
                            () -> assertThat(responseJsonPath.getString("color")).isEqualTo(color)
                    );
                }),

                dynamicTest("노선을 수정한다.", () -> {
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    String updateName = "1호선";
                    String updateColor = "bg-green-600";
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", updateName, "color", updateColor))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("존재하지 않는 노선을 수정할 경우 404를 반환한다.", () -> {
                    Long id = 0L;
                    String name = "2호선";
                    String color = "bg-blue-600";

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", name, "color", color))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                }),

                dynamicTest("중복된 이름을 가진 노선으로 수정할 경우 예외를 던진다.", () -> {
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(Map.of("name", "2호선", "color", "bg-green-600"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .put("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("노선을 삭제한다.", () -> {
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }),

                dynamicTest("존재하지 않는 노선의 id를 삭제할 경우 잘못된 요청이므로 404를 반환한다.", () -> {
                    Long id = createdResponse1.body().jsonPath().getLong("id");

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/" + id)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                })
        );
    }

    @DisplayName("노선에 구간 추가")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromAddSection() {
        String name = "1호선";
        String color = "bg-blue-600";
        Long basedUpStationId = generateStationId("중동역");
        Long basedDownStationId = generateStationId("신도림역");
        Integer basedDistance = 10;

        ExtractableResponse<Response> createdResponse = generateLine(name, color, basedUpStationId, basedDownStationId,
                basedDistance);

        Long lineId = createdResponse.jsonPath().getLong("id");

        return Stream.of(
                dynamicTest("상행 종점이 같은 경우 가장 앞단의 구간 보다 길이가 크거나 같으면 400을 반환한다.", () -> {
                    Long upStationId = basedUpStationId;
                    Long downStationId = generateStationId("부천역");
                    Integer distance = 10;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("상행 종점이 같은 경우 가장 앞단의 구간 보다 길이가 작으면 추가한다.", () -> {
                    Long upStationId = basedUpStationId;
                    Long downStationId = generateStationId("역곡역");
                    Integer distance = 4;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("상행 종점에 구간을 추가한다.", () -> {
                    Long upStationId = generateStationId("부평역");
                    Long downStationId = basedUpStationId;
                    Integer distance = 7;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("노선 조회 시 등록된 지하철 목록을 확인할 수 있다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .get("/lines/{id}", lineId)
                            .then().log().all()
                            .extract();

                    List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
                    assertAll(
                            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                            () -> assertThat(stations.size()).isEqualTo(4)
                    );
                }),

                dynamicTest("상행 종점 추가 시 지하철이 존재하지 않는 경우 404을 반환한다.", () -> {
                    Long upStationId = 0L;
                    Long downStationId = basedUpStationId;
                    Integer distance = 7;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                }),

                dynamicTest("상행 종점 추가 시 상행역이 기존 노선에 존재하는 경우 400을 반환한다.", () -> {
                    Long upStationId = basedDownStationId;
                    Long downStationId = basedUpStationId;
                    Integer distance = 7;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("하행 종점이 같은 경우 가장 뒷단의 구간보다 길이가 크거나 같으면 400을 반환한디.", () -> {
                    Long upStationId = generateStationId("온수역");
                    Long downStationId = basedDownStationId;
                    Integer distance = 10;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("하행 종점이 같은 경우 가장 앞단의 구간보다 길이가 작으면 추가한다.", () -> {
                    Long upStationId = generateStationId("개봉역");
                    Long downStationId = basedDownStationId;
                    Integer distance = 3;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("하행 종점에 구간을 추가한다.", () -> {
                    Long upStationId = basedDownStationId;
                    Long downStationId = generateStationId("영등포역");
                    Integer distance = 10;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("상행역과 하행역이 노선에 모두 존재하면 예외를 던진다.", () -> {
                    Long upStationId = basedUpStationId;
                    Long downStationId = basedDownStationId;
                    Integer distance = 1;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("상행역과 하행역이 노선에 모두 존재하지 않으면 예외를 던진다.", () -> {
                    Long upStationId = generateStationId("서울역");
                    Long downStationId = generateStationId("노량진역");
                    Integer distance = 1;

                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .body(new SectionRequest(upStationId, downStationId, distance))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                            .post("/lines/{id}/sections", lineId)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
        );
    }

    @DisplayName("구간 삭제 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromRemoveSection() {
        Long stationId1 = generateStationId("신도림역");
        Long stationId2 = generateStationId("온수역");
        Long stationId3 = generateStationId("역곡역");
        Long stationId4 = generateStationId("부천역");
        Long stationId5 = generateStationId("중동역");

        ExtractableResponse<Response> lineResponse = generateLine(
                "1호선", "bg-blue-600", stationId1, stationId2, 5);
        Long lineId = lineResponse.jsonPath().getLong("id");

        addSection(lineId, stationId2, stationId3, 5);
        addSection(lineId, stationId3, stationId4, 5);
        addSection(lineId, stationId4, stationId5, 5);

        return Stream.of(
                dynamicTest("중간에 위치한 역을 삭제한다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/{id}/sections?stationId={stationId}", lineId, stationId2)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("상행 종점의 구간을 삭제한다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/{id}/sections?stationId={stationId}", lineId, stationId1)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("존재하지 않는 역을 삭제할 경우 예외를 던진다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/{id}/sections?stationId={stationId}", lineId, stationId1)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("하행 종점의 구간을 삭제한다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/{id}/sections?stationId={stationId}", lineId, stationId5)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("구간이 한개 뿐인 경우 예외를 던진다.", () -> {
                    ExtractableResponse<Response> response = RestAssured.given().log().all()
                            .when()
                            .delete("/lines/{id}/sections?stationId={stationId}", lineId, stationId2)
                            .then().log().all()
                            .extract();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                })
        );
    }

    private Long generateStationId(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return response.jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> generateLine(String name, String color, Long upStationId,
                                                       Long downStationId, Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private void addSection(Long id, Long upStationId, Long downStationId, Integer distance) {
        RestAssured.given().log().all()
                .body(new SectionRequest(upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", id)
                .then().log().all()
                .extract();
    }
}
