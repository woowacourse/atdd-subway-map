package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

class StationDaoImplTest extends DaoImplTest {

    private StationDaoImpl stationDaoImpl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDaoImpl = new StationDaoImpl(jdbcTemplate);
    }

    @DisplayName("역 정보를 저장한다.")
    @Test
    void save() {
        Station station = new Station("역삼역");
        Station newStation = stationDaoImpl.save(station);

        assertThat(newStation.getName()).isEqualTo("역삼역");
    }

    @DisplayName("역 정보들을 가져온다.")
    @Test
    void findAll() {
        List<Station> stations = stationDaoImpl.findAll();

        assertThat(stations.size()).isEqualTo(3);
    }

    @DisplayName("역 정보를 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("역삼역");
        Station newStation = stationDaoImpl.save(station);

        assertThat(stationDaoImpl.deleteStation(newStation.getId())).isEqualTo(1);
    }
}
