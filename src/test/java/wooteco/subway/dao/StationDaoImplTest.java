package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql("classpath:stationDao.sql")
class StationDaoImplTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDaoImpl(dataSource);
        stationDao.save(new Station("서울역"));
    }

    @DisplayName("이름값을 받아 해당 이름값을 가진 역이 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource({"서울역, true", "선릉역, false"})
    void exists(String name, boolean expected) {
        Station newStation = new Station(name);

        boolean actual = stationDao.exists(newStation);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("저장되어있는 모든 지하철 역을 반환한다.")
    @Test
    void findAll() {
        stationDao.save(new Station("강남역"));
        Long seoulStationId = 1L;
        Long gangnamStationId = 2L;

        List<Station> stations = stationDao.findAll();

        assertThat(stations)
                .contains(new Station(seoulStationId, "서울역"), new Station(gangnamStationId, "강남역"));
    }

    @DisplayName("id 값에 해당하는 지하철 역을 삭제한다.")
    @Test
    void deleteById() {
        Long seoulStationId = 1L;
        stationDao.deleteById(seoulStationId);

        List<Station> stations = stationDao.findAll();

        assertThat(stations).isEmpty();
    }
}
