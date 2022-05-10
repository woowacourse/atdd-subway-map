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

    @DisplayName("노선을 관리한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestStream() {
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
                    Long id = 10L;
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

    private long generateStationId(String name) {
        ExtractableResponse<Response> response = generateStation(name);
        return response.jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> generateStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> generateLine(String name, String color, Long upStationId,
                                                       Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
