package wooteco.subway.station.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class StationDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    StationDaoImplTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = new StationDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("디비 저장 테스트")
    void save() {
        //given
        Station station = new Station("김밥역");

        //when
        Station savedStation = stationDao.save(station);

        assertThat(station.name()).isEqualTo(savedStation.name());
    }

    @Test
    @DisplayName("모든 역을 찾는다.")
    void findAll() {
        //given

        //when
        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(5);
    }

    @Test
    @DisplayName("특정 역을 ID로 찾는다.")
    void findById() {
        //given
        Station station = new Station("김밥역");
        Station station2 = new Station("안양역");

        //when
        Station saveStation = stationDao.save(station);
        Station findById = stationDao.findById(saveStation.id()).get();

        //then
        assertThat(findById.name()).isEqualTo(station.name());
    }

    @Test
    @DisplayName("특정 역을 이름으로 찾는다.")
    void findByName() {
        //given
        Station station = new Station("김밥역");
        Station station2 = new Station("안양역");

        //when
        Station saveStation = stationDao.save(station);
        Station findByName = stationDao.findByName(station.name()).get();

        //then
        assertThat(findByName.name()).isEqualTo(station.name());
    }

    @Test
    @DisplayName("아이디로 역을 삭제한다")
    void delete() {
        //given
        Station station = new Station("김밥역");

        //when
        Station saveStation = stationDao.save(station);
        stationDao.delete(saveStation.id());

        //then
        assertThat(stationDao.findById(saveStation.id())).isEmpty();
    }

    @Test
    @DisplayName("아이디로 없는 역을 삭제시 예외가 발생한다.")
    void deleteException() {
        //given
        Station station = new Station("김밥역");

        //when
        Station saveStation = stationDao.save(station);
        stationDao.delete(saveStation.id());

        //then
        assertThatThrownBy(() -> stationDao.delete(saveStation.id()))
                .isInstanceOf(IllegalStateException.class);
    }
}