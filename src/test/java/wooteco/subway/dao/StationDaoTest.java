package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(dataSource);
    }

    @DisplayName("지하철 역 저장 테스트")
    @Test
    void saveStation() {
        Station station = new Station("강남역");

        Station persistStation = stationDao.save(station);

        assertThat(persistStation.getId()).isNotNull();
        assertThat(persistStation.getName()).isEqualTo("강남역");
    }

    @DisplayName("중복된 이름의 지하철 역을 저장할 경우 예외가 발생한다.")
    @Test
    void saveDuplicateStation() {
        Station station = new Station("강남역");
        stationDao.save(station);

        assertThatThrownBy(() -> stationDao.save(station))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("전체 역의 개수가 맞는지 확인한다.")
    @Test
    void findAllStation() {
        Station gangNam = new Station("강남역");
        Station jamSil = new Station("잠실역");

        stationDao.save(gangNam);
        stationDao.save(jamSil);

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @DisplayName("특정 id를 가지는 역을 조회한다.")
    @Test
    void findById() {
        Station gangNam = new Station("강남역");
        stationDao.save(gangNam);

        Station actual = stationDao.findById(gangNam.getId());

        assertThat(actual.getName()).isEqualTo("강남역");
    }

    @DisplayName("특정 id를 가지는 역을 삭제한다.")
    @Test
    void deleteStation() {
        Station station = new Station("강남역");
        Station persistStation = stationDao.save(station);
        stationDao.deleteById(persistStation.getId());

        assertThat(stationDao.findAll()).isEmpty();
    }
}
