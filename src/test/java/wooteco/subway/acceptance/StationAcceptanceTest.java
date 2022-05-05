package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceFixture.강남역_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.STATION_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.역삼역_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.deleteMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getExpectedLineIds;
import static wooteco.subway.acceptance.AcceptanceFixture.getMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getResultLineIds;
import static wooteco.subway.acceptance.AcceptanceFixture.postMethodRequest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = postMethodRequest(강남역_인자, STATION_URL);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("강남역")
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 에러를 응답한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        postMethodRequest(강남역_인자, STATION_URL);

        // when
        ExtractableResponse<Response> response = postMethodRequest(강남역_인자, STATION_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = postMethodRequest(강남역_인자, STATION_URL);
        ExtractableResponse<Response> createResponse2 = postMethodRequest(역삼역_인자, STATION_URL);

        // when
        ExtractableResponse<Response> response = getMethodRequest(STATION_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = getExpectedLineIds(List.of(createResponse1, createResponse2));
        List<Long> resultLineIds = getResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = postMethodRequest(강남역_인자, STATION_URL);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = deleteMethodRequest(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
