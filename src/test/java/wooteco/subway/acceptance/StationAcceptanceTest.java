package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.ui.dto.StationRequest;
import wooteco.subway.ui.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final StationRequest GANGNAM_REQUEST = new StationRequest("강남역");
    private static final StationRequest YEOKSAM_REQUEST = new StationRequest("역삼역");
    private static final String STATIONS_URI = "/stations";

    @DisplayName("신규 지하철역 생성 성공 시, 응답코드는 CREATED 이고 응답헤더에 Location 이 존재한다")
    @Test
    void createStation() {
        // given
        final String newStationRequestJson = toJson(GANGNAM_REQUEST);

        // when
        final ExtractableResponse<Response> createResponse = post(STATIONS_URI, newStationRequestJson);
        final StationResponse stationResponse = createResponse.as(StationResponse.class);

        // then
        assertAll(
                () -> assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(createResponse.header("Location")).isNotBlank(),
                () -> assertThat(stationResponse.getName()).isEqualTo(GANGNAM_REQUEST.getName())
        );
    }

    @DisplayName("기존에 존재하는 이름으로 지하철역 생성 시도 시 생성되지 않고 응답코드는 BAD_REQUEST 이다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final ExtractableResponse<Response> createResponse = post(STATIONS_URI, toJson(GANGNAM_REQUEST));

        // when
        final ExtractableResponse<Response> duplicateCreateResponse = post(STATIONS_URI, toJson(GANGNAM_REQUEST));

        // then
        assertAll(
                () -> assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(duplicateCreateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())

        );
    }

    @DisplayName("전체 지하철역을 조회할 수 있으며, 응답코드는 OK이다")
    @Test
    void getStations() {
        /// given
        final ExtractableResponse<Response> gangNamCreateResponse = post(STATIONS_URI, toJson(GANGNAM_REQUEST));
        final ExtractableResponse<Response> yeokSamCreateResponse = post(STATIONS_URI, toJson(YEOKSAM_REQUEST));

        // when
        final ExtractableResponse<Response> findAllResponse = get(STATIONS_URI);

        // then
        List<Long> expectedIds = Stream.of(gangNamCreateResponse, yeokSamCreateResponse)
                .map(response -> Long.parseLong(response.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> actualIds = findAllResponse.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(gangNamCreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(yeokSamCreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(findAllResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualIds).containsAll(expectedIds)
        );
    }

    @DisplayName("ID로 지하철역을 삭제할 수 있으며, 삭제 성공 시 응답코드는 NO_CONTENT 이다")
    @Test
    void deleteStation() {
        // given
        final ExtractableResponse<Response> createResponse = post(STATIONS_URI, toJson(GANGNAM_REQUEST));

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> deleteResponse = delete(uri);

        // then
        assertAll(
                () -> assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }
}
