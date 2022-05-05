package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Station;

@JdbcTest
@Import(JdbcStationDao.class)
public class JdbcStationDaoTest {

    @Autowired
    private JdbcStationDao jdbcStationDao;

    @Test
    @DisplayName("지하철 역 저장")
    void save() {
        Station station = new Station("호호역");
        Station saveStation = jdbcStationDao.save(station);

        assertThat(saveStation.getId()).isNotNull();
        assertThat(saveStation.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("지하철 역 이름 중복 여부 조회")
    void duplicateName() {
        Station station = new Station("호호역");
        jdbcStationDao.save(station);
        assertThat(jdbcStationDao.existByName("호호역")).isTrue();
    }

    @Test
    @DisplayName("지하철 역 전체 조회")
    void findAll() {
        Station station1 = new Station("호호역");
        Station station2 = new Station("수달역");
        jdbcStationDao.save(station1);
        jdbcStationDao.save(station2);

        List<Station> stations = jdbcStationDao.findAll();

        assertThat(stations).contains(station1, station2);
    }

    @Test
    @DisplayName("id로 지하철 역을 삭제")
    void deleteById() {
        Station station1 = new Station("호호역");
        Station saveStation = jdbcStationDao.save(station1);

        jdbcStationDao.deleteById(saveStation.getId());

        assertThat(jdbcStationDao.findAll()).hasSize(0);
    }


}
