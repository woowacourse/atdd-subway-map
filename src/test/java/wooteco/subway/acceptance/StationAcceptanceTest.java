package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.station.StationRequest;
import wooteco.subway.controller.dto.station.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private StationRequest 서울대입구 = new StationRequest("서울대입구");
    private StationRequest 신림 = new StationRequest("신림");
    private StationRequest 부평 = new StationRequest("부평");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> response = createPostStationResponse(부평);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().get("name").toString()).isEqualTo(부평.getName())
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 생성시 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        // when
        createPostStationResponse(서울대입구);
        ExtractableResponse<Response> response = createPostStationResponse(서울대입구);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> 낙성대응답 = createPostStationResponse(서울대입구);
        ExtractableResponse<Response> 선릉응답 = createPostStationResponse(신림);
        // when
        ExtractableResponse<Response> response = createGetStationResponse();
        List<Long> 추가한Id = postIds(낙성대응답, 선릉응답);
        List<Long> 전체Id = responseIds(response);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(전체Id).containsAll(추가한Id),
                () -> assertThat(response.body().jsonPath().get("name").toString()).contains(서울대입구.getName()),
                () -> assertThat(response.body().jsonPath().get("name").toString()).contains(신림.getName())
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = createPostStationResponse(서울대입구);
        String id = createResponse.header("Location").split("/")[2];
        // when
        ExtractableResponse<Response> response = createDeleteStationResponseById(id);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createPostStationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createGetStationResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private List<Long> postIds(ExtractableResponse<Response>... createResponse) {
        return Arrays.asList(createResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private List<Long> responseIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> createDeleteStationResponseById(String id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + id)
                .then().log().all()
                .extract();
    }
}
