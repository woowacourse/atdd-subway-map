package wooteco.subway.ui;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionControllerTest {

    private Map<String, Long> stationIds = new HashMap<>();
    private Long lineId;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        addStations(List.of("강남역", "선릉역", "역삼역", "교대역", "삼성역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", stationIds.get("강남역"), stationIds.get("선릉역"), 10L);
        Response response3 = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines");

        lineId = response3.jsonPath().getLong("id");
    }

    private void addStations(List<String> names) {
        for (String name : names) {
            StationRequest stationRequest = new StationRequest(name);
            Long stationId  = RestAssured.given()
                    .body(stationRequest)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/stations").jsonPath().getLong("id");
            stationIds.put(name, stationId);
        }
    }

    @DisplayName("하행 종점을 공유하는 구간을 생성한다.")
    @Test
    void createSection_no_change_down() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("역삼역"), stationIds.get("선릉역"), 3L);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점이 바뀌는 구간을 생성한다.")
    @Test
    void createSection_change_down() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("선릉역"), stationIds.get("역삼역"), 3L);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("상행 종점이 바뀌는 구간을 생성한다.")
    @Test
    void createSection_change_up() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("역삼역"), stationIds.get("강남역"), 3L);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("상행 종점을 공유하는 구간을 생성한다.")
    @Test
    void createSection_no_change_up() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("강남역"), stationIds.get("역삼역"), 3L);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("사이클을 형성하는 구간을 생성한다.")
    @Test
    void createSection_cycle() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("선릉역"), stationIds.get("강남역"), 3L);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("거리 조건을 만족하지 못하는 구간을 추가할 때 정확한 에러 메세지가 나오는지 테스트")
    @Test
    void cannot_createSection_bad_distance() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("역삼역"), stationIds.get("선릉역"), 30L);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo("[ERROR] 현재 구간의 거리가 너무 깁니다.");
    }

    @DisplayName("이미 있는 구간을 추가할 때 정확한 에러 메세지가 나오는지 테스트")
    @Test
    void cannot_createSection_already_exist() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("역삼역"), stationIds.get("선릉역"), 3L);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        //when
        SectionRequest sectionRequest2 = new SectionRequest(stationIds.get("강남역"), stationIds.get("선릉역"), 3L);
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(sectionRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response2.jsonPath().getString("message")).isEqualTo("[ERROR] 이미 존재하는 노선은 추가할 수 없습니다.");
    }

    @DisplayName("역을 공유하지 않는 구간을 추가할 때 정확한 에러 메세지가 나오는지 테스트")
    @Test
    void cannot_createSection_no_exist() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("삼성역"), stationIds.get("교대역"), 3L);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo("[ERROR] 상행역과 하행역 중 하나는 공유되는 역이여야 합니다.");
    }

    @DisplayName("하나의 구간만 남았을 때 삭제할 때 정확한 에러 메세지가 나오는지 테스트")
    @Test
    void cannot_deleteSection_only_one_exist() {
        // given
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationIds.get("강남역"))
                .then()
                .extract();

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo("[ERROR] 노선을 유지할 수 없습니다.");
    }

    @DisplayName("직선 구간 중 하나를 삭제한다")
    @Test
    void deleteSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("역삼역"), stationIds.get("선릉역"), 3L);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = RestAssured.given()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationIds.get("강남역"))
                .then()
                .extract();

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("사이클에서 사이클 구간을 하나를 삭제한다")
    @Test
    void deleteSection_cycle_size_one() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationIds.get("선릉역"), stationIds.get("역삼역"), 10L);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        SectionRequest sectionRequest2 = new SectionRequest(stationIds.get("역삼역"), stationIds.get("강남역"), 10L);
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(sectionRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response3 = RestAssured.given()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationIds.get("강남역"))
                .then()
                .extract();

        // then
        assertThat(response3.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
