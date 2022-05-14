package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateStationNameException;

@Transactional
@JdbcTest
class StationJdbcDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationJdbcDao dao;

    @BeforeEach
    void setUp() {
        dao = new StationJdbcDao(jdbcTemplate);
    }

    @DisplayName("새로운 지하철 역을 저장한다")
    @Test
    void saveStation() {
        // given
        Station station = new Station("강남역");

        // when
        Long id = dao.save(station);

        // then
        List<Station> stations = dao.findAll();
        Station actual = stations.get(0);
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("강남역");
    }

    @DisplayName("같은 지하철 역 이름이 있는 경우 예외를 발생시킨다")
    @Test
    void saveStationThrowException() {
        String name = "강남역";
        dao.save(new Station(name));

        assertThatThrownBy(() -> dao.save(new Station(name)))
                .isInstanceOf(DuplicateStationNameException.class);
    }

    @DisplayName("역 목록을 조회한다")
    @Test
    void findAll() {
        // given
        dao.save(new Station("name1"));
        dao.save(new Station("name2"));
        dao.save(new Station("name3"));

        // when
        List<Station> stations = dao.findAll();

        // then
        assertThat(stations).hasSize(3);
    }

    @DisplayName("id로 역 하나를 삭제한다")
    @Test
    void deleteById() {
        // given
        Long savedId1 = dao.save(new Station("station1"));
        Long savedId2 = dao.save(new Station("station2"));

        // when
        dao.deleteById(savedId1);

        // then
        assertThat(dao.findAll()).hasSize(1);
    }
}
