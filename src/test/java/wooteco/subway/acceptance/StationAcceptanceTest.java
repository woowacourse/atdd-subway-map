package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성 성공 시 상태 코드 201을 반환하고 Location 헤더에 주소를 전달한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> createResponse = createStationAssured(new StationRequest("서울역"));
        // then
        assertAll(
            () -> assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(createResponse.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역 생성 시 상태 코드 400을 반환한다.")
    @Test
    void createStation_duplicateName_exception() {
        // given
        ExtractableResponse<Response> createResponse1 = createStationAssured(new StationRequest("선릉역"));
        ExtractableResponse<Response> createResponse2 = createStationAssured(new StationRequest("선릉역"));
        // then
        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회 시 상태 코드 200을 반환하고 Station 목록을 반환한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = createStationAssured(new StationRequest("종로3가역"));
        ExtractableResponse<Response> createResponse2 = createStationAssured(new StationRequest("동묘앞역"));
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("지하철역 제거 성공 시 상태 코드 204를 반환한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = createStationAssured(new StationRequest("선릉역"));
        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 ID의 지하철역 삭제 시 상태 코드 400 에러를 반환한다.")
    @Test
    void deleteStation_noExistStation_Exception() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .delete("/stations/10000")
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createStationAssured(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }
}
