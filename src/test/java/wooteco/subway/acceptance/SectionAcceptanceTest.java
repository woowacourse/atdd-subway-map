package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStation() {
        RestAssured.given().log().all()
            .body(new StationRequest("ㄱ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        RestAssured.given().log().all()
            .body(new StationRequest("ㄴ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        RestAssured.given().log().all()
            .body(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("구간을 등록한다(상행 종점, 하행 종점, 상행역이 존재하는 갈래길, 하행역이 존재하는 갈래길 순으로 테스트).")
    @ParameterizedTest
    @CsvSource(value = {
        "3,1",
        "2,3",
        "1,3",
        "3,2"
    })
    void createSection(Long upStationId, Long downStationId) {
        // given
        RestAssured.given().log().all()
            .body(new StationRequest("ㄷ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 7);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void createSectionError() {
        // given
        RestAssured.given().log().all()
            .body(new StationRequest("ㄷ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 555);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 삭제한다.")
    @ParameterizedTest
    @CsvSource(value = {
        "1",
        "2",
        "3"
    })
    void deleteSection(Long stationId) {
        // given
        RestAssured.given().log().all()
            .body(new StationRequest("ㄷ역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        RestAssured.given().log().all()
            .body(new SectionRequest(2L, 3L, 7))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1/sections?stationId=" + stationId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.")
    @Test
    void deleteSectionError() {// when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1/sections?stationId=1")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
