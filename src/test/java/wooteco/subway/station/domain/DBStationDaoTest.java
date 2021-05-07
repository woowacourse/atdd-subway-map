package wooteco.subway.station.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.station.domain.DBStationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DBStationDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    DBStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = new DBStationDao(jdbcTemplate);
    }

    @BeforeEach
    void init() {

    }

    @Test
    @DisplayName("디비 저장 테스트")
    void save() {
        //given
        Station station = new Station("김밥역");

        //when
        Station savedStation = stationDao.save(station);

        assertThat(station.getName()).isEqualTo(savedStation.getName());
    }

    @Test
    @DisplayName("동일한 이름의 역을 저장시 에러가 발생한다.")
    void saveException() {
        //given
        Station station = new Station("김밥역");
        Station secondStation = new Station("김밥역");

        //when
        Station savedStation = stationDao.save(station);

        //then
        assertThatThrownBy(() -> stationDao.save(secondStation))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("모든 역을 찾는다.")
    void findAll() {
        //given
        Station station = new Station("김밥역");
        Station station2 = new Station("안양역");

        //when
        stationDao.save(station);
        stationDao.save(station2);
        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("특정 역을 ID로 찾는다.")
    void findById() {
        //given
        Station station = new Station("김밥역");
        Station station2 = new Station("안양역");

        //when
        Station saveStation = stationDao.save(station);
        Station findById = stationDao.findById(saveStation.getId()).get();

        //then
        assertThat(findById.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("특정 역을 이름으로 찾는다.")
    void findByName() {
        //given
        Station station = new Station("김밥역");
        Station station2 = new Station("안양역");

        //when
        Station saveStation = stationDao.save(station);
        Station findByName = stationDao.findByName(station.getName()).get();

        //then
        assertThat(findByName.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("DB 전체 삭제시 예외가 발생한다")
    void clearException() {
        //given
        Station station = new Station("김밥역");

        //when
        Station saveStation = stationDao.save(station);

        //then
        assertThatThrownBy(stationDao::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("아이디로 역을 삭제한다")
    void delete() {
        //given
        Station station = new Station("김밥역");

        //when
        Station saveStation = stationDao.save(station);
        stationDao.delete(saveStation.getId());

        //then
        assertThat(stationDao.findById(saveStation.getId())).isEmpty();
    }

    @Test
    @DisplayName("아이디로 없는 역을 삭제시 예외가 발생한다.")
    void deleteException() {
        //given
        Station station = new Station("김밥역");

        //when
        Station saveStation = stationDao.save(station);
        stationDao.delete(saveStation.getId());

        //then
        assertThatThrownBy(() -> stationDao.delete(saveStation.getId()))
                .isInstanceOf(IllegalStateException.class);
    }
}