package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedFieldException;

@JdbcTest
class StationDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(namedParameterJdbcTemplate);
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
            .isInstanceOf(DuplicatedFieldException.class);
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
        final List<String> stationNames = Arrays.asList("잠실역", "강남역", "건대입구역");
        final List<Long> ids = stationNames.stream()
            .map(Station::new)
            .map(stationDao::save)
            .map(Station::getId)
            .collect(Collectors.toList());

        assertThat(stationDao.findByIds(ids))
            .extracting("id")
            .isEqualTo(ids);
    }
}
