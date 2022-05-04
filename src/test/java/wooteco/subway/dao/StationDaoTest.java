package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@SpringBootTest
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        Station station = new Station("강남역");
        stationDao.save(station);

        Integer count = jdbcTemplate.queryForObject("select count(*) from STATION", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("지하철역의 전체 목록을 조회한다.")
    @Test
    void findAll() {
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));

        List<String> stations = stationDao.findAll()
                .stream().map(Station::getName)
                .collect(Collectors.toList());

        assertThat(stations).containsExactly("강남역", "선릉역");
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        long stationId = stationDao.save(new Station("강남역"));

        assertThat(stationDao.delete(stationId)).isEqualTo(1);
    }
}
