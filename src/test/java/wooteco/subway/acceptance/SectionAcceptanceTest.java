package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간 추가 시 지하철 노선이 존재하지 않는 경우")
    @Test
    void addSectionToNotFoundLine() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = requestAddSection(1L, upStationId, downStationId,
            10);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("구간 추가 시 존재하지 않는 지하철 역을 사용할 경우")
    @Test
    void addSectionWithNotFoundStation() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        ExtractableResponse<Response> response = requestAddSection(lineId, upStationId,
            Math.max(upStationId, downStationId) + 1, 10);

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

        ExtractableResponse<Response> response = requestAddSection(lineId, newStationId,
            upStationId, 8);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중복된 구간 추가")
    @Test
    void addSectionByDuplicateSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        ExtractableResponse<Response> response = requestAddSection(lineId, downStationId,
            upStationId, 8);

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

        ExtractableResponse<Response> response = requestAddSection(lineId, upStationId,
            newStationId, 8);

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

        ExtractableResponse<Response> response = requestAddSection(lineId, upStationId,
            newStationId, 10);

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

        ExtractableResponse<Response> response = requestAddSection(lineId, notFoundStationId1,
            notFoundStationId2, 3);

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

        ExtractableResponse<Response> response = requestAddSection(lineId, newStationId,
            newStationId, 3);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 삭제 시 지하철 노선이 존재하지 않는 경우")
    @Test
    void deleteSectionToNotFoundLine() {
        long stationId = requestCreateStation("강남역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/1/sections?stationId=" + stationId)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("구간 삭제 시 존재하지 않는 지하철 역을 사용할 경우")
    @Test
    void deleteSectionWithNotFoundStation() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");
        long notFoundStationId = Math.max(upStationId, downStationId) + 1;

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + notFoundStationId)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선에 추가되지 않은 지하철역을 삭제 시 실패")
    @Test
    void deleteNotValidStationSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long notFoundStationId = requestCreateStation("선릉역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + notFoundStationId)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 한 개만 있는 노선에서는 구간 삭제 실패")
    @Test
    void deleteOnlyOneSection() {
        long upStationId = requestCreateStation("강남역").jsonPath().getLong("id");
        long downStationId = requestCreateStation("역삼역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", upStationId, downStationId, 10)
            .jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + upStationId)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점 구간 제거")
    @Test
    void deleteLastUpStationSection() {
        long stationId1 = requestCreateStation("강남역").jsonPath().getLong("id");
        long stationId2 = requestCreateStation("역삼역").jsonPath().getLong("id");
        long stationId3 = requestCreateStation("잠실역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", stationId1, stationId2, 10)
            .jsonPath().getLong("id");
        requestAddSection(lineId, stationId2, stationId3, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId1)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점 구간 제거")
    @Test
    void deleteLastDownStationSection() {
        long stationId1 = requestCreateStation("강남역").jsonPath().getLong("id");
        long stationId2 = requestCreateStation("역삼역").jsonPath().getLong("id");
        long stationId3 = requestCreateStation("잠실역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", stationId1, stationId2, 10)
            .jsonPath().getLong("id");
        requestAddSection(lineId, stationId2, stationId3, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId3)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중간의 구간 제거")
    @Test
    void deleteSectionAndMerge() {
        long stationId1 = requestCreateStation("강남역").jsonPath().getLong("id");
        long stationId2 = requestCreateStation("역삼역").jsonPath().getLong("id");
        long stationId3 = requestCreateStation("잠실역").jsonPath().getLong("id");
        long lineId = requestCreateLine("신분당선", "bg-red-600", stationId1, stationId2, 10)
            .jsonPath().getLong("id");
        requestAddSection(lineId, stationId2, stationId3, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId2)
            .then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
