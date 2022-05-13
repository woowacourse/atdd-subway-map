package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@DisplayName("지하철역 관련 service 테스트")
@JdbcTest
class StationServiceTest {

    private static final StationRequest STATION_REQUEST = new StationRequest("강남역");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        StationDao stationDao = new StationDao(jdbcTemplate);
        stationService = new StationService(stationDao);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        // when
        stationService.save(STATION_REQUEST);

        // then
        List<String> stationNames = stationService.findAll().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).contains("강남역");
    }

    @DisplayName("중복된 지하철역을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        // given
        stationService.save(STATION_REQUEST);

        // when & then
        assertThatThrownBy(() -> stationService.save(STATION_REQUEST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철역 이름이 중복됩니다.");
    }

    @DisplayName("지하철역의 목록을 조회한다.")
    @Test
    void findAll() {
        // given
        stationService.save(STATION_REQUEST);

        // when
        List<String> stationNames = stationService.findAll().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(stationNames).contains("강남역");
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        // given
        long stationId = stationService.save(STATION_REQUEST);

        // when
        stationService.delete(stationId);

        // then
        List<String> stationNames = stationService.findAll().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).doesNotContain("강남역");
    }

    @DisplayName("존재하지 않는 지하철역을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistStation() {
        // when & then
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}
