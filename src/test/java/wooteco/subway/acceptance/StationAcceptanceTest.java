package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
public class StationAcceptanceTest extends AcceptanceTest {

    private final String basicPath = "/stations";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        Long ResponseStationId = response.jsonPath().getObject(".", StationResponse.class).getId();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/stations/" + ResponseStationId)
        );
    }

    @DisplayName("비어있는 값으로 이름을 생성하면 400번 코드를 반환한다.")
    @Test
    void createStationWithInvalidDataSize() {
        // given
        StationRequest request = new StationRequest("");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 400번 코드를 반환한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        RestAssuredConvenienceMethod.postRequest(new StationRequest("선릉역"), basicPath);
        StationRequest request = new StationRequest("선릉역");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, basicPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Long 선릉역_id = RestAssuredConvenienceMethod.postLineAndGetId(new StationRequest("선릉역"), basicPath);
        Long 선정릉역_id = RestAssuredConvenienceMethod.postLineAndGetId(new StationRequest("선정릉역"), basicPath);

        // when
        ExtractableResponse<Response> response = RestAssuredConvenienceMethod.getRequest(basicPath);

        // then
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(List.of(선릉역_id, 선정릉역_id));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Long 선릉역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("선릉역"), basicPath);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/stations/" + 선릉역_id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철역을 삭제하려하면 400번 코드를 반환한다.")
    @Test
    void deleteStationWithNotExistData() {
        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/stations/" + 100L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 속해있는 지하철역을 삭제하려하면 400번 코드를 반환한다.")
    @Test
    void deleteStationWithStationInLine() {
        // given
        Long 선릉역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("선릉역"), "/stations");
        Long 선정릉역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("선정릉역"), "/stations");
        Long 분당선_id = RestAssuredConvenienceMethod.postLineAndGetId(
                new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10), "/lines");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest("/stations/" + 선릉역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
