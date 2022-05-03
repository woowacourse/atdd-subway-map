package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @DisplayName("새 지하철역을 저장한다.")
    @Test
    void save() {
        Station testStation = new Station(null, "hi");
        Station result = StationDao.save(testStation);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("hi");
    }

    @DisplayName("지하철역 이름을 이용해 지하철역을 조회한다.")
    @Test
    void findByName() {
        Station test = new Station(null, "test");
        StationDao.save(test);
        Station result = StationDao.findByName("test").orElse(null);
        Optional<Station> result2 = StationDao.findByName("test2");

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result2.isEmpty()).isTrue();
    }
}
