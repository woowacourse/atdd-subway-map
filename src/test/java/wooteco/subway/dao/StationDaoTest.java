package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private final StationDao stationDao;

    @Autowired
    public StationDaoTest(NamedParameterJdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철 역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("선릉역");

        Station savedStation = stationDao.save(station);

        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @DisplayName("같은 이름의 지하철 역을 저장하는 경우 예외가 발생한다.")
    @Test
    void saveExistingName() {
        Station station = new Station("선릉역");

        stationDao.save(station);

        assertThatThrownBy(() -> stationDao.save(station))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("모든 지하철 역을 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("선릉역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("사우역");

        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        assertThat(stationDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("id로 지하철 역을 조회한다.")
    @Test
    void findById() {
        Station station = new Station("선릉역");

        Station savedStation = stationDao.save(station);

        assertThat(stationDao.findById(savedStation.getId()).getName()).isEqualTo("선릉역");
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void deleteById() {
        Station station = new Station("선릉역");
        Station savedStation = stationDao.save(station);

        stationDao.deleteById(savedStation.getId());

        assertThat(stationDao.findAll().size()).isZero();
    }
}
