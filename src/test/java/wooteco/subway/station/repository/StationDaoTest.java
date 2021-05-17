package wooteco.subway.station.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/truncate.sql")
public class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @BeforeEach
    public void setUp() {
        stationDao.save(new Station("1"));
        stationDao.save(new Station("2"));
    }

    @DisplayName("id 리스트를 전달하여 역 데이터들을 얻어온다.")
    @Test
    public void findByIds() {
        List<Station> stationList = stationDao.findByIds(Arrays.asList(1L, 2L));

        assertThat(stationList).hasSize(2);
    }

    @DisplayName("id를 전달하여 역을 찾는다.")
    @Test
    public void findById() {
        Station station = stationDao.findById(1L);
        assertThat(station.getName()).isEqualTo("1");
    }

    @DisplayName("id를 전달하여 역을 삭제 한다.")
    @Test
    public void deleteById() {
        stationDao.deleteById(1L);
        List<Station> stations = stationDao.findAll();
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getId()).isEqualTo(2L);
    }
}
