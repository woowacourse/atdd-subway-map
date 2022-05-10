package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.domain.Station;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class StationDaoTest {

    private final StationDao stationDao;

    public StationDaoTest(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @BeforeEach
    void set() {
        stationDao.save("선릉역");
    }

    @AfterEach
    void reset() {
        stationDao.deleteAll();
    }

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        String expected = "강남역";

        Station station = stationDao.save(expected);
        String actual = station.getName();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("모든 지하철 역을 조회한다")
    void findAll() {
        stationDao.save("잠실역");

        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 삭제한다")
    void deleteById() {
        stationDao.deleteById(1L);

        assertThat(stationDao.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 검색한다")
    void findById() {
        Station actualStation = new Station("선릉역");
        Station expectedStation = stationDao.findById(1L).orElse(null);

        assertThat(actualStation).isEqualTo(expectedStation);
    }
}


