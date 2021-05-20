package wooteco.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.JdbcStationDao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Sql("classpath:test.sql")
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private JdbcStationDao stationRepository;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest 신논현역 = new StationRequest("신논현역");

        // when
        ExtractableResponse<Response> 신논현역_생성_응답 = post("/stations", 신논현역);

        // then
        assertThat(신논현역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(신논현역_생성_응답.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        post("/stations", 강남역);

        // when
        ExtractableResponse<Response> 강남역_생성_응답 = post("/stations", 강남역);

        // then
        assertThat(강남역_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성_응답 = post("/stations", 강남역);

        StationRequest 역삼역 = new StationRequest("역삼역");
        ExtractableResponse<Response> 역삼역_생성_응답 = post("/stations", 역삼역);

        // when
        ExtractableResponse<Response> 모든역_조회_응답 = get("/stations");

        // then
        assertThat(모든역_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> 생성시_응답된_역ID_리스트 = Stream.of(강남역_생성_응답, 역삼역_생성_응답)
                .map(this::getIdFromResponse)
                .collect(Collectors.toList());

        List<Long> 조회시_응답된_역ID_리스트 = 모든역_조회_응답.jsonPath()
                .getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(조회시_응답된_역ID_리스트).containsAll(생성시_응답된_역ID_리스트);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> 강남역_생성_응답 = post("/stations", 강남역);
        int 모든역의_개수 = stationRepository.findAll().size();
        String path = 강남역_생성_응답.header("Location");
        ExtractableResponse<Response> 강남역_삭제_응답 = delete(path);

        // then
        assertThat(강남역_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationRepository.findAll()).hasSize(모든역의_개수 - 1);
    }
}
