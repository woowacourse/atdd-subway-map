package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedNameException;

@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        final Station station = new Station("잠실역");

        final Station createdStation = stationDao.save(station);

        assertThat(createdStation.getName()).isEqualTo(station.getName());
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외가 발생한다.")
    @Test
    void saveStationWithDuplicateName() {
        final Station station = new Station("잠실역");
        stationDao.save(station);

        assertThatThrownBy(() -> stationDao.save(station))
            .hasMessage("중복된 이름의 지하철역입니다.")
            .isInstanceOf(DuplicatedNameException.class);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void delete() {
        final Station station = new Station("잠실역");
        final Station createdStation = stationDao.save(station);

        assertThatCode(() -> stationDao.deleteById(createdStation.getId()))
            .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철역 이름으로 지하철역을 제거하면 예외가 발생한다.")
    @Test
    void deleteWithAbsentName() {
        assertThatThrownBy(() -> stationDao.deleteById(1L))
            .hasMessage("해당 Id의 지하철역이 없습니다.")
            .isInstanceOf(DataNotFoundException.class);
    }

    @DisplayName("전체 지하철역을 조회한다.")
    @Test
    void findAll() {
        final List<String> stationNames = Arrays.asList("잠실역", "강남역", "건대입구역");
        stationNames.stream()
            .map(Station::new)
            .forEach(stationDao::save);

        assertThat(stationDao.findAll()).extracting("name").isEqualTo(stationNames);
    }

    @DisplayName("특정 이름의 지하철역을 조회한다.")
    @Test
    void findByName() {
        final String name = "잠실역";
        final Station createdStation = stationDao.save(new Station(name));

        final Station station = stationDao.findByName(name).get();

        assertThat(station.getId()).isEqualTo(createdStation.getId());
        assertThat(station.getName()).isEqualTo(createdStation.getName());
    }

    @DisplayName("특정 id의 지하철역을 조회한다.")
    @Test
    void findById() {
        final String name = "잠실역";
        final Station createdStation = stationDao.save(new Station(name));

        final Station station = stationDao.findById(createdStation.getId()).get();

        assertThat(station.getId()).isEqualTo(createdStation.getId());
        assertThat(station.getName()).isEqualTo(createdStation.getName());
    }

    @DisplayName("여러 id의 지하철역들을 조회한다.")
    @Test
    void findByIds() {
        final List<Station> actual = Arrays.asList(
            new Station("강남역"),
            new Station("역삼역"),
            new Station("잠실역"),
            new Station("교대역")
        );
        actual.forEach(station -> stationDao.save(station));
        final List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L);
        List<Station> stations = stationDao.findByIds(ids);
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(stations);
    }
}