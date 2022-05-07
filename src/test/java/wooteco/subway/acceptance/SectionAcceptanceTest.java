package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간 추가 시 지하철 노선이 존재하지 않는 경우")
    @Test
    void addSectionToNotFoundLine() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/1/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("구간 추가 시 존재하지 않는 지하철 역을 사용할 경우")
    @Test
    void addSectionWithNotFoundStation() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", upStationId,
            "downStationId", Math.max(upStationId, downStationId) + 1,
            "distance", 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("종점에 새로운 구간 추가")
    @Test
    void addSectionToLastUpStation() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");
        long newStationId = requestCreateStation("선릉역").jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", newStationId,
            "downStationId", upStationId,
            "distance", 8);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중복된 구간 추가")
    @Test
    void addSectionByDuplicateSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", downStationId,
            "downStationId", upStationId,
            "distance", 8);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("정상적인 구간 추가")
    @Test
    void addValidSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long newStationId = requestCreateStation("선릉역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", upStationId,
            "downStationId", newStationId,
            "distance", 8);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("기존 구간보다 같거나 긴 거리로 구간 추가 시 실패")
    @Test
    void addNotValidDistanceSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long newStationId = requestCreateStation("선릉역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", upStationId,
            "downStationId", newStationId,
            "distance", 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 추가되지 않은 상행, 하행 지하철역을 구간으로 사용 시 실패")
    @Test
    void addNotValidStationSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long notFoundStationId1 = requestCreateStation("선릉역").jsonPath().getLong("id");
        long notFoundStationId2 = requestCreateStation("잠실역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", notFoundStationId1,
            "downStationId", notFoundStationId2,
            "distance", 3);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("동일한 상행, 하행 역으로 구간 등록 시 실패")
    @Test
    void addSectionWithSameUpAndDownStation() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long newStationId = requestCreateStation("선릉역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        Map<String, Object> params = Map.of(
            "upStationId", newStationId,
            "downStationId", newStationId,
            "distance", 3);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines/" + lineId+ "/sections")
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
