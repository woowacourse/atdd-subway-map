package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;


public class SectionAcceptanceTest extends AcceptanceTest {

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("상행 종점 구간을 등록한다.")
    @Test
    void addSectionUpStation() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 3L;
        Long paramDownStationId = 1L;
        int paramDistance = 10;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        List<Station> stations = findResponse.body().jsonPath().getList("stations", Station.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0).getId()).isEqualTo(3L),
                () -> assertThat(stations.get(1).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(2).getId()).isEqualTo(2L)
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("하행 종점 구간을 등록한다.")
    @Test
    void addSectionDownStation() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 2L;
        Long paramDownStationId = 3L;
        int paramDistance = 10;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        List<Station> stations = findResponse.body().jsonPath().getList("stations", Station.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(1).getId()).isEqualTo(2L),
                () -> assertThat(stations.get(2).getId()).isEqualTo(3L)
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("상행선이 같은 갈래길 방지로 구간을 등록한다.")
    @Test
    void addSectionBetweenUpStation() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 1L;
        Long paramDownStationId = 3L;
        int paramDistance = 5;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        List<Station> stations = findResponse.body().jsonPath().getList("stations", Station.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(1).getId()).isEqualTo(3L),
                () -> assertThat(stations.get(2).getId()).isEqualTo(2L)
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("하행선이 같은 갈래길 방지로 구간을 등록한다.")
    @Test
    void addSectionBetweenDownStation() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 3L;
        Long paramDownStationId = 2L;
        int paramDistance = 5;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        List<Station> stations = findResponse.body().jsonPath().getList("stations", Station.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(1).getId()).isEqualTo(3L),
                () -> assertThat(stations.get(2).getId()).isEqualTo(2L)
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("기존 상하행 거리보다 거리가 멀거나 같은 경우 구간을 등록할 수 없다.")
    @Test
    void addSectionBetweenStationFailOverDistance() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 1L;
        Long paramDownStationId = 3L;
        int paramDistance = 10;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("역 사이 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionBetweenStationFailAlreadyRegisteredUpAndDown() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 1L;
        Long paramDownStationId = 2L;
        int paramDistance = 3;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionBetweenStationFailNotRegisteredUpAndDown() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long paramUpStationId = 3L;
        Long paramDownStationId = 3L;
        int paramDistance = 3;
        SectionRequest params = new SectionRequest(paramUpStationId, paramDownStationId, paramDistance);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
        );
    }

    @Sql(value = "/sql/InsertTwoSections.sql")
    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void deleteSectionTerminal() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : (1, 2), (2, 3)
        역 사이 거리 : 10, 10
         */
        // given
        Long lineId = 1L;
        Long stationId = 3L;

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        List<Station> stations = findResponse.body().jsonPath().getList("stations", Station.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(1).getId()).isEqualTo(2L)
        );
    }

    @Sql(value = "/sql/InsertTwoSections.sql")
    @DisplayName("중간역이 제거될 경우 재배치를 함")
    @Test
    void deleteSectionWhenFork() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : (1, 2), (2, 3)
        역 사이 거리 : 10, 10
         */
        // given
        Long lineId = 1L;
        Long stationId = 2L;

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        List<Station> stations = findResponse.body().jsonPath().getList("stations", Station.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(1).getId()).isEqualTo(3L)
        );
    }

    @Sql(value = "/sql/InsertSections.sql")
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음")
    @Test
    void deleteSectionFailWhenLastSection() {
        /*
        이미 등록된 노선 아이디 : 1
        이미 등록된 역 아이디 : 1, 2, 3, 4
        구간 등록된 역 아이디 : 1, 2
        역 사이 거리 : 10
         */
        // given
        Long lineId = 1L;
        Long stationId = 2L;

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음")
        );
    }
}
