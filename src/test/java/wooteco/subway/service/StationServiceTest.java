package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_잠실역;

import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationDuplicateException;

@JdbcTest
class StationServiceTest {

    @Autowired
    private DataSource dataSource;
    private JdbcStationDao stationDao;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        this.stationDao = new JdbcStationDao(dataSource);
        this.stationService = new StationService(stationDao);
    }

    @DisplayName("새로운 지하철역을 생성한다")
    @Test
    void create() {
        //given & when
        final Station station = stationService.create(STATION_REQUEST_강남역);

        // then
        Assertions.assertThat(station.getId()).isNotNull();
    }

    @DisplayName("호선을 중복 생성하면 예외가 발생한다.")
    @Test
    void create_duplicate() {
        //given
        stationService.create(STATION_REQUEST_강남역);

        //when & then
        assertThatThrownBy(() -> stationService.create(STATION_REQUEST_강남역))
            .isInstanceOf(StationDuplicateException.class)
            .hasMessage("[ERROR] 이미 존재하는 지하철역 이름입니다.");
    }

    @DisplayName("모든 역을 조회한다")
    @Test
    void show() {
        //given
        stationService.create(STATION_REQUEST_강남역);
        stationService.create(STATION_REQUEST_잠실역);

        //when
        final List<Station> stations = stationService.show();

        //then
        Assertions.assertThat(stations).hasSize(2);
    }

    @DisplayName("특정 역을 삭제한다")
    @Test
    void delete() {
        //given
        final Station station = stationService.create(STATION_REQUEST_강남역);

        //when
        stationService.delete(station.getId());

        //then
        Assertions.assertThat(stationService.show()).isEmpty();
    }
}
