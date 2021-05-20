package wooteco.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JDBCStationDaoTest {
    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    JDBCStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = new JDBCStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("디비 저장 테스트")
    void save() {
        //given
        Station station = new Station("김밥역");

        //when
        Station savedStation = stationDao.save(station);

        //then
        assertThat(station.name()).isEqualTo(savedStation.name());
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
                .isInstanceOf(DuplicateKeyException.class);
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

        //then
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
        stationDao.delete(saveStation.id());

        //then
        assertThat(stationDao.findById(saveStation.id())).isEmpty();
    }

    @Test
    @DisplayName(" 없는 아이디로 역을 삭제")
    void deleteStationByNotFoundId() {
        //given
        Station station = new Station("김밥역");

        //when
        Station saveStation = stationDao.save(station);
        stationDao.delete(saveStation.id());

        //then
        assertThatCode(() -> stationDao.delete(saveStation.id()))
                .doesNotThrowAnyException();
    }
}
