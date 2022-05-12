package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcStationDaoTest {

    private static final String FAIL_FIND_STATION = "fail";

    private final JdbcStationDao jdbcStationDao;

    @Autowired
    public JdbcStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcStationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철역을 등록할 수 있다.")
    void save() {
        // given
        final Station station = new Station("지하철역이름");

        // when
        final Long savedId = jdbcStationDao.save(station);

        // then
        final Station savedStation = jdbcStationDao.findById(savedId)
                .orElseGet(() -> new Station(FAIL_FIND_STATION));

        assertThat(station).isEqualTo(savedStation);
    }

    @Test
    @DisplayName("지하철역 목록을 조회할 수 있다.")
    void findAll() {
        // given
        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("새로운지하철역이름");
        final Station station3 = new Station("또다른지하철역이름");

        jdbcStationDao.save(station1);
        jdbcStationDao.save(station2);
        jdbcStationDao.save(station3);

        // when
        final List<Station> stations = jdbcStationDao.findAll();

        // then
        assertThat(stations).hasSize(3)
                .extracting("name")
                .contains("지하철역이름", "새로운지하철역이름", "또다른지하철역이름");
    }

    @Test
    @DisplayName("지하철역을 삭제할 수 있다.")
    void deleteById() {
        // given
        final Station station = new Station("지하철역이름");
        final Long savedId = jdbcStationDao.save(station);

        // when & then
        assertDoesNotThrow(() -> jdbcStationDao.deleteById(savedId));
    }
}
