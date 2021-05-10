package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.ControllerTest;
import wooteco.subway.station.service.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("지하철역 관련 기능")
public class StationControllerTest extends ControllerTest {

    @Autowired
    private StationDao stationDao;

    @AfterEach
    void afterEach() {
        stationDao.removeAll();
    }

    @DisplayName("지하철역 -  생성")
    @Test
    void createStation() {
        역_생성("강남역").statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo("강남역"));
    }

    private ValidatableResponse 역_생성(String name) {
        // given
        StationRequest stationRequest = new StationRequest(name);

        // when
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        ExtractableResponse<Response> stationRequest = 역_생성("강남역").extract();

        // when
        역_생성("강남역").statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 역_생성("강남역").extract();
        ExtractableResponse<Response> createResponse2 = 역_생성("역삼역").extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Transactional
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        String uri = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .header("Location");
        // when and then
        RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("역 제거 - 실패(존재하지 않는 역)")
    @Test
    void deleteStation_notExistStation() {
        StationRequest stationRequest = new StationRequest(1L, "강남역");

        RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(ContentType.JSON)
                .when()
                .delete("/stations/" + stationRequest.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(equalTo("역을 찾을 수 없습니다."));
    }

    @DisplayName("역 생성 - 실패(중복된 이름)")
    @Test
    void createStation_duplicatedName() throws Exception {
        //given
        ValidatableResponse stationRequest = 역_생성("강남역").statusCode(HttpStatus.CREATED.value());

        //when and then
        역_생성("강남역").statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("이미 등록되어 있는 역 이름입니다."));
    }
}
