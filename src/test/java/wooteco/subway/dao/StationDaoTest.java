package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Station;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class StationDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철 역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("선릉역");
        Station savedStation = stationDao.save(station);

        assertThat(station.getName()).isEqualTo(savedStation.getName());
    }

//    @DisplayName("같은 이름의 지하철 역을 저장하는 경우 예외가 발생한다.")
//    @Test
//    void saveExistingName() {
//        Station station = new Station("선릉역");
//        stationDao.save(station);
//
//        assertThatThrownBy(() -> {
//            stationDao.save(station);
//        }).isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("같은 이름의 역은 등록할 수 없습니다.");
//    }

    @DisplayName("name으로 지하철 노선을 조회한다.")
    @Test
    void findByName() {
        Station station = new Station("선릉역");
        Station savedStation = stationDao.save(station);

        Optional<Station> foundStation = stationDao.findByName(savedStation.getName());

        assertThat(foundStation.isPresent()).isTrue();
    }

    @DisplayName("name으로 조회한 지하철이 없을 경우 empty optional을 반환한다")
    @Test
    void findByNameReturnOptional() {
        Optional<Station> foundStation = stationDao.findByName("선릉역");

        assertThat(foundStation.isPresent()).isFalse();
    }

    @DisplayName("id로 지하철 노선을 조회한다.")
    @Test
    void findById() {
        Station station = new Station("선릉역");
        Station savedStation = stationDao.save(station);

        Optional<Station> foundStation = stationDao.findById(savedStation.getId());

        assertThat(foundStation.get().getName()).isEqualTo(savedStation.getName());
    }

    @DisplayName("id로 조회한 지하철 노선이 없을 경우 empty optional을 반환한다.")
    @Test
    void findByIdReturnOptionalEmpty() {
        Optional<Station> foundStation = stationDao.findById(1L);
        assertThat(foundStation.isPresent()).isFalse();
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

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void deleteById() {
        Station station = new Station("선릉역");
        Station savedStation = stationDao.save(station);

        stationDao.deleteById(savedStation.getId());

        assertThat(stationDao.findAll().size()).isZero();
    }

    @DisplayName("존재하지 않는 역을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistingStation() {
        assertThatThrownBy(() -> stationDao.deleteById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }
}