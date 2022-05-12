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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.utils.StationAcceptanceTestFixture.*;

@DisplayName("지하철 역 E2E")
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
        ExtractableResponse<Response> response = 역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철 역 이름으로 지하철 역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 역 이름에 빈 문자열을 사용할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void createStationWithEmptyName(String stationName) {
        // when
        ExtractableResponse<Response> response = 역_생성_요청(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 역_생성_요청("강남역");
        ExtractableResponse<Response> createResponse2 = 역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 역_목록_요청();

        List<Long> actualLineIds = response.jsonPath().getList("id", Long.class);
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> it.body().jsonPath().getLong("id"))
            .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }


    @DisplayName("지하철 역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 역_생성_요청("강남역");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 역_삭제_요청(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 역을 제거한다.")
    @Test
    void deleteNotExistStation() {
        // when
        ExtractableResponse<Response> response = 역_삭제_요청("/stations/50");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
