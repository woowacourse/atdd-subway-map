package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.jdbc.StationJdbcDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateStationNameException;

@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao dao;

    @BeforeEach
    void setUp() {
        dao = new StationJdbcDao(jdbcTemplate);
    }

    @DisplayName("새로운 지하철 역을 저장한다")
    @Test
    void saveStation() {
        Station savedStation = dao.save(new Station("강남역"));

        List<Station> stations = dao.findAll();
        Station actual = stations.get(0);
        assertThat(actual).isEqualTo(savedStation);
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
        Station savedStation = dao.save(new Station("station"));

        // when
        dao.deleteById(savedStation.getId());

        // then
        assertThat(dao.findAll()).isEmpty();
    }

    @DisplayName("id로 역 하나를 조회한다")
    @Test
    void findById() {
        Station savedStation = dao.save(new Station("station"));

        Station findStation = dao.findById(savedStation.getId()).get();

        assertThat(findStation).isEqualTo(savedStation);
    }

    @DisplayName("존재하지 않는 id로 역을 조회하면 빈 값을 반환한다")
    @Test
    void throwExceptionWhenTargetLineDoesNotExist() {
        Optional<Station> optionalStation = dao.findById(1L);
        assertThat(optionalStation).isEqualTo(Optional.empty());
    }
}
