package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.SectionRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    SectionDao sectionDao;

    @Test
    @DisplayName("구간을 등록한다. 1-3 -> 1-2-3")
    void save() {
        // given
        Long station1 = createStation("강남역");
        Long station2 = createStation("교대역");
        Long station3 = createStation("역삼역");

        Long lineId = saveLine(station1, station3);
        SectionRequest sectionRequest = new SectionRequest(station1, station2, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineId + "/sections")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();

        List<Section> inputSections = sectionDao.findByLineId(lineId);
        Section section = inputSections.stream()
            .filter(i -> i.mathUpStationId(1L))
            .findAny()
            .get();

        // then
        assertAll(
            () -> assertThat(section.getDistance()).isEqualTo(1),
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @Test
    @DisplayName("구간을 제거한다. 1-2-3 -> 1-3")
    void remove() {
        // given
        Long station1 = createStation("강남역");
        Long station2 = createStation("교대역");
        Long station3 = createStation("역삼역");
        Long lineId = saveLine(station1, station3);
        SectionRequest sectionRequest1 = new SectionRequest(station1, station2, 1);

        RestAssured.given().log().all()
            .body(sectionRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineId + "/sections")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .queryParam("stationId", station2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();

        List<Section> inputSections = sectionDao.findByLineId(lineId);
        Section section = inputSections.stream()
            .filter(i -> i.mathUpStationId(1L))
            .findAny()
            .get();

        // then
        assertAll(
            () -> assertThat(section.getDistance()).isEqualTo(4),
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    Long saveLine(Long station1, Long station2) {
        LineRequest lineRequest = new LineRequest("3호선", "bg-orange-600", station1, station2, 4);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract();

        String uri = response.header("Location");
        return Long.parseLong(uri.split("/")[2]);
    }

    Long createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        String uri = createResponse.header("Location");
        return Long.parseLong(uri.split("/")[2]);
    }
}
