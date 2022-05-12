package wooteco.subway.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setRemoveAssertJRelatedElementsFromStackTrace;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineControllerTest {

    private Map<String, Long> stationIds = new HashMap<>();

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        addStations(List.of("강남역", "선릉역", "역삼역", "교대역", "삼성역"));
    }

    private void addStations(List<String> names) {
        for (String name : names) {
            StationRequest stationRequest = new StationRequest(name);
            Long stationId = RestAssured.given()
                    .body(stationRequest)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/stations").jsonPath().getLong("id");
            stationIds.put(name, stationId);
        }
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> initializeLine(String name, String color, Long upStationId,
                                                         Long downStationId, Long distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        return RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);

        // when
        ExtractableResponse<Response> response1 = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);

        // then
        assertThat(response1.jsonPath().getString("message")).isEqualTo("[ERROR] 이미 같은 이름의 노선이 존재합니다.");
    }

    @DisplayName("노선을 생성하고 상행 종점이 바뀌는 구간 삽입의 경우")
    @Test
    void up_station_change() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");
        addSection(stationIds.get("선릉역"), stationIds.get("강남역"), 10L, id);

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        RestAssured.given().when().get("/lines/" + id + "/sections").then().extract().jsonPath().prettyPrint();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("선릉역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(2).getName()).isEqualTo("역삼역"));
    }

    private void addSection(Long upStationId, Long downStationId, Long distance, Long lineId) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        RestAssured.given()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections");
    }

    @DisplayName("노선을 생성하고 하행 종점이 바뀌는 구간 삽입의 경우")
    @Test
    void down_station_change() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");
        addSection(stationIds.get("역삼역"), stationIds.get("선릉역"), 10L, id);

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("역삼역"),
                () -> assertThat(result.get(2).getName()).isEqualTo("선릉역"));
    }

    @DisplayName("노선을 생성하고 하행역을 공유하는 삽입의 경우")
    @Test
    void down_station_no_change() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");
        addSection(stationIds.get("선릉역"), stationIds.get("역삼역"), 5L, id);

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        response1.jsonPath().prettyPrint();
        List<Station> result = response1.jsonPath().getList("stations", Station.class);

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("선릉역"),
                () -> assertThat(result.get(2).getName()).isEqualTo("역삼역"));
    }

    @DisplayName("노선을 생성하고 상행역을 공유하는 삽입의 경우")
    @Test
    void up_station_no_change() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("강남역"), stationIds.get("선릉역"), 5L, id);

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("선릉역"),
                () -> assertThat(result.get(2).getName()).isEqualTo("역삼역"));
    }

    @DisplayName("역 2개로 이루어진 사이클을 만드는 경우")
    @Test
    void make_cycle_of_two_stations() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("역삼역"), stationIds.get("강남역"), 5L, id);

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);
        response1.jsonPath().prettyPrint();

        assertCycle(result);
    }

    @DisplayName("역 3개로 이루어진 사이클을 만드는 경우")
    @Test
    void make_cycle_of_three_stations() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("역삼역"), stationIds.get("선릉역"), 5L, id);
        addSection(stationIds.get("선릉역"), stationIds.get("강남역"), 5L, id);

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);
        response1.jsonPath().prettyPrint();

        assertCycle(result);
    }

    private void assertCycle(List<Station> result) {
        Map<Long, Long> count = new HashMap<>();
        int targetSize = result.size() - 2;
        for (Station station : result) {
            count.put(station.getId(), 0L);
        }
        for (Station station : result) {
            count.put(station.getId(), count.get(station.getId()) + 1);
        }
        int two_count = (int) count.values().stream()
                .filter(times -> times == 2L)
                .count();
        int one_count = (int) count.values().stream()
                .filter(times -> times == 1L)
                .count();
        assertThat(two_count == 1 && one_count == targetSize).isTrue();
    }

    @DisplayName("직선 노선의 상행 종점을 삭제하는 경우")
    @Test
    void delete_up_end() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("역삼역"), stationIds.get("선릉역"), 5L, id);
        deleteSection(id, stationIds.get("강남역"));

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);
        response1.jsonPath().prettyPrint();

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("역삼역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("선릉역"));
    }

    @DisplayName("직선 노선의 하행 종점을 삭제하는 경우")
    @Test
    void delete_down_end() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("역삼역"), stationIds.get("선릉역"), 5L, id);
        deleteSection(id, stationIds.get("선릉역"));

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);
        response1.jsonPath().prettyPrint();

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("역삼역"));
    }

    @DisplayName("직선 노선의 중간 역을 삭제하는 경우")
    @Test
    void delete_middle() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("역삼역"), stationIds.get("선릉역"), 5L, id);
        deleteSection(id, stationIds.get("역삼역"));

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);
        response1.jsonPath().prettyPrint();

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("선릉역"));
    }

    @DisplayName("사이클 노선의 중간 역을 삭제하는 경우")
    @Test
    void delete_middle_in_cycle() {
        ExtractableResponse<Response> response = initializeLine("2호선", "green", stationIds.get("강남역"),
                stationIds.get("역삼역"), 10L);
        Long id = response.jsonPath().getLong("id");

        addSection(stationIds.get("역삼역"), stationIds.get("선릉역"), 5L, id);
        addSection(stationIds.get("선릉역"), stationIds.get("강남역"), 5L, id);
        deleteSection(id, stationIds.get("선릉역"));

        ExtractableResponse<Response> response1 = RestAssured.given()
                .when()
                .get("/lines/" + id)
                .then()
                .extract();

        List<Station> result = response1.jsonPath().getList("stations", Station.class);
        response1.jsonPath().prettyPrint();

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("역삼역"));
    }

    private void deleteSection(Long id, Long stationId) {
        RestAssured.given().when().delete("/lines/" + id + "/sections?stationId=" + stationId)
                .then().extract();
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> responseExtractableResponse = initializeLine("2호선", "green",
                stationIds.get("강남역"), stationIds.get("역삼역"), 10L);
        Long id = responseExtractableResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        List<Station> result = response.jsonPath().getList("stations", Station.class);

        assertAll(() -> assertThat(result.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(result.get(1).getName()).isEqualTo("역삼역"));
    }

    @DisplayName("노선들을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> initResponse1 = initializeLine("2호선", "green",
                stationIds.get("강남역"), stationIds.get("역삼역"), 10L);
        ExtractableResponse<Response> initResponse2 = initializeLine("3호선", "yellow",
                stationIds.get("선릉역"), stationIds.get("삼성역"), 10L);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        List<LineResponse> list = response.jsonPath().getList("$", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(initResponse1, initResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> responseExtractableResponse = initializeLine("2호선", "green",
                stationIds.get("강남역"), stationIds.get("역삼역"), 10L);
        Long id = responseExtractableResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하는 id와 중복되지 않는 이름으로 바꾼다.")
    @Test
    void change_name_success() {
        /// given
        ExtractableResponse<Response> responseExtractableResponse = initializeLine("2호선", "green",
                stationIds.get("강남역"), stationIds.get("역삼역"), 10L);
        Long id = responseExtractableResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> initResponse2 = initializeLine("3호선", "yellow",
                stationIds.get("선릉역"), stationIds.get("삼성역"), 10L);

        // when
        String uri = initResponse2.header("Location");
        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "3호선");
        params3.put("color", "bg-green-500");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params3)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 id의 노선 이름을 변경한다.")
    @Test
    void change_name_no_id() {
        /// given
        ExtractableResponse<Response> responseExtractableResponse = initializeLine("2호선", "green",
                stationIds.get("강남역"), stationIds.get("역삼역"), 10L);
        Long id = responseExtractableResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> initResponse2 = initializeLine("3호선", "yellow",
                stationIds.get("선릉역"), stationIds.get("삼성역"), 10L);

        // when
        String uri = "/lines/1000";
        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "3호선");
        params3.put("color", "bg-green-500");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params3)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo("[ERROR] 유효한 id가 아닙니다.");
    }

    @DisplayName("이미 저장된 이름으로 이름을 바꾼다.")
    @Test
    void change_name_name_duplicate() {
        /// given
        ExtractableResponse<Response> responseExtractableResponse = initializeLine("2호선", "green",
                stationIds.get("강남역"), stationIds.get("역삼역"), 10L);
        Long id = responseExtractableResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> initResponse2 = initializeLine("3호선", "yellow",
                stationIds.get("선릉역"), stationIds.get("삼성역"), 10L);

        // when
        String uri = initResponse2.header("Location");
        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "2호선");
        params3.put("color", "bg-green-500");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params3)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo("[ERROR] 중복된 이름으로 바꿀 수 없습니다.");
    }
}
