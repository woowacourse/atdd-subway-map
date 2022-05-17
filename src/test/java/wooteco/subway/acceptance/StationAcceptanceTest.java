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
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.DbStationDao;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.utils.FixtureUtils.*;

@DisplayName("지하철 역 E2E")
@SuppressWarnings("NonAsciiCharacters")
@Sql("/init.sql")
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
        StationResponse stationResponse = convertType(response, StationResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(stationResponse.getId()).isNotNull();
        assertThat(stationResponse.getName()).isEqualTo("상도역");
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
        ExtractableResponse<Response> response = get(STATION);

        List<Long> actualLineIds = extractIds(response);

        StationResponse stationResponse1 = convertType(createResponse1, StationResponse.class);
        StationResponse stationResponse2 = convertType(createResponse2, StationResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualLineIds).containsExactlyInAnyOrder(stationResponse1.getId(), stationResponse2.getId());
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
        ExtractableResponse<Response> response = delete(stationById(존재하지_않는_역_ID));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
