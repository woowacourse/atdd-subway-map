package wooteco.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.station.StationRequest.*;

@DisplayName("지하철역 관련 기능")
@Transactional
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> 강남역 = stationRequestForm("강남역");

        // when
        ExtractableResponse<Response> response = create(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> 강남역 = stationRequestForm("강남역");
        create(강남역);

        // when
        ExtractableResponse<Response> response = create(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> 강남역 = stationRequestForm("강남역");
        Map<String, String> 역삼역 = stationRequestForm("역삼역");
        ExtractableResponse<Response> 강남역생성 = create(강남역);
        ExtractableResponse<Response> 역삼역생성 = create(역삼역);

        // when
        ExtractableResponse<Response> response = get();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(강남역생성, 역삼역생성).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> 강남역 = stationRequestForm("강남역");
        ExtractableResponse<Response> 강남역생성 = create(강남역);

        int originalSize = stationDao.findAll().size();

        // when
        String uri = 강남역생성.header("Location");
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationDao.findAll()).hasSize(originalSize - 1);
    }
}
