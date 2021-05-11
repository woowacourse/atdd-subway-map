package wooteco.subway.dao.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.response.StationResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.dao.fixture.CommonFixture.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    public static final String STATIONS_URI = "/stations";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = setStation("name", "영통역");

        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(params, STATIONS_URI);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = setStation("name", "강남역");
        extractResponseWhenPost(params, STATIONS_URI);

        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(params, STATIONS_URI);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("올바르지 않은 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithWrongName() {
        // given
        Map<String, String> params = setStation("name", "강남");
        ExtractableResponse<Response> response = extractResponseWhenPost(params, STATIONS_URI);

        // when - then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Map<String, String> params1 = setStation("name", "강남역");
        ExtractableResponse<Response> createResponse1 = extractResponseWhenPost(params1, STATIONS_URI);

        Map<String, String> params2 = setStation("name", "역삼역");
        ExtractableResponse<Response> createResponse2 = extractResponseWhenPost(params2, STATIONS_URI);

        // when
        ExtractableResponse<Response> response = extractResponseWhenGet(STATIONS_URI);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = setStation("name", "강남역");
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(params, STATIONS_URI);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = extractResponseWhenDelete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> setStation(String key, String value) {
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        return params;
    }
}
