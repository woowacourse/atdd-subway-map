package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.exception.SubwayException;

@JdbcTest
class StationServiceTest {

    private static final Station STATION = new Station("강남역");

    private StationService stationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        StationDao stationDao = new JdbcStationDao(jdbcTemplate);
        stationService = new StationService(stationDao);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        stationService.save(STATION);

        assertThat(stationService.findAll()).hasSize(1);
    }

    @DisplayName("중복된 지하철역을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        stationService.save(STATION);

        assertThatThrownBy(() -> stationService.save(STATION))
                .isInstanceOf(SubwayException.class)
                .hasMessage("지하철역 이름이 중복됩니다.");
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        long stationId = stationService.save(STATION);

        assertThatCode(() -> stationService.delete(stationId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철역을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistStation() {
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(DataNotExistException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}
