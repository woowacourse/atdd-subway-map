package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.dao.DbStationDao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.utils.FixtureUtils.*;

@DisplayName("지하철 역 E2E")
@SuppressWarnings("NonAsciiCharacters")
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DbStationDao stationDao;

    @BeforeEach
    void beforeEach() {
        stationDao.deleteAll();
    }

    @DisplayName("지하철 역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = post(STATION, 상도역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("중복된 이름의 역 이름을 생성할 수 없다 - 400 에러")
    @Test
    void createStationWithDuplicateName() {
        // given
        post(STATION, 상도역);

        // when
        ExtractableResponse<Response> response = post(STATION, 상도역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 역 이름에 빈 문자열을 사용할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void createStationWithEmptyName(String stationName) {
        // given
        Map<String, String> 빈_이름의_역 = Map.of("name", stationName);

        // when
        ExtractableResponse<Response> response = post(STATION, 빈_이름의_역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = post(STATION, 상도역);
        ExtractableResponse<Response> createResponse2 = post(STATION, 이수역);

        // when
        ExtractableResponse<Response> response = get(STATION, EMPTY_MAP);

        List<Long> actualLineIds = response.jsonPath().getList("id", Long.class);
        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2) {
        return Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> it.body().jsonPath().getLong("id"))
                .collect(Collectors.toList());
    }


    @DisplayName("지하철 역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = post(STATION, 상도역);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri, EMPTY_MAP);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 역을 제거한다.")
    @Test
    void deleteNotExistStation() {
        // given
        int 존재하지_않는_역_ID = 50;

        // when
        ExtractableResponse<Response> response = delete("/stations/" + 존재하지_않는_역_ID, EMPTY_MAP);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
