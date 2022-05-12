package wooteco.subway.controller;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import wooteco.subway.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.controller.AcceptanceFixture.*;

@DisplayName("지하철역 관련 기능")
public class StationControllerTest extends AcceptanceTest {

    private static final String URL = "/stations";

    @Autowired
    private StationDao stationDao;

    @DisplayName("등록 - `POST /stations`")
    @Nested
    class Create {

        @Test
        @DisplayName("성공")
        void success() {
            Map<String, String> params = new HashMap<>();
            params.put("name", "강남역");

            ExtractableResponse<Response> response = post(params, URL);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        }

        @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성할 때 400을 반환한다.")
        @Test
        void stationWithDuplicateName() {
            stationDao.save(new Station("강남역"));
            Map<String, String> params = new HashMap<>();
            params.put("name", "강남역");

            ExtractableResponse<Response> response = post(params, URL);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @DisplayName("이름에 빈 값이 들어오면 400을 반환한다.")
        @Test
        void createStationEmptyName() {
            Map<String, String> params = new HashMap<>();
            params.put("name", "");

            ExtractableResponse<Response> response = post(params, URL);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("목록 - `GET /stations`")
    @Test
    void getStations() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));

        ExtractableResponse<Response> response = get(URL);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = List.of(station1.getId(), station2.getId());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("삭제 - `DELETE /stations/{id}`")
    @Nested
    class delete {
        @DisplayName("성공")
        @Test
        void success() {
            Station station = stationDao.save(new Station("강남역"));
            ExtractableResponse<Response> response = delete(URL + "/" + station.getId());

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @DisplayName("지하철역을 제거할 때 id에 맞는 노선이 없으면 404을 응답한다.")
        @Test
        void deleteLineResponse404() {
            ExtractableResponse<Response> response = delete(URL + "/" + 1);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }
}
