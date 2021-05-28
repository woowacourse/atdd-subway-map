package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.notFoundException.StationNotFoundException;

@JdbcTest
@DisplayName("역 레포지토리 레이어 테스트")
class StationDaoTest {

    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 역을 생성 및 저장한다.")
    void save() {
        // given
        Station station = new Station("잠실역");

        // when
        long id = stationDao.save(station);
        Station savedStation = stationDao.findById(id)
            .orElseThrow(StationNotFoundException::new);

        // then
        assertThat(id).isEqualTo(savedStation.getId());
    }

    @Test
    @DisplayName("생성된 역들을 불러온다.")
    void findAll() {
        // given
        Station station1 = new Station("잠실역");
        Station station2 = new Station("역삼역");
        stationDao.save(station1);
        stationDao.save(station2);

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("특정 역을 아이디로 찾아온다.")
    void findById() {
        // given
        Station station1 = new Station("잠실역");
        Long id = stationDao.save(station1);

        // when
        Station station2 = stationDao.findById(id)
            .orElseThrow(StationNotFoundException::new);

        // then
        assertThat(station2.getName()).isEqualTo(station1.getName());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 찾아올 때 에러가 발생한다.")
    void cannotFindById() {
        assertThatThrownBy(() -> {
            stationDao.findById(Long.MAX_VALUE)
                .orElseThrow(StationNotFoundException::new);
        }).isInstanceOf(StationNotFoundException.class);
    }

    @Test
    @DisplayName("등록된 역을 제거한다.")
    void deleteById() {
        // given
        Station station = new Station("잠실역");
        Long id = stationDao.save(station);

        // when
        stationDao.deleteById(id);


        // then
        assertThatThrownBy(() -> {
            stationDao.findById(id)
                .orElseThrow(StationNotFoundException::new);
        }).isInstanceOf(StationNotFoundException.class);
    }
}