package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Station;
import java.util.List;
import java.util.stream.Collectors;

@JdbcTest
class StationDaoTest {

    private final StationDao stationDao;

    private Station savedStation;

    @Autowired
    private StationDaoTest(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.stationDao = new StationDao(namedParameterJdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        Station station = new Station("선릉역");
        savedStation = stationDao.save(station);
    }

    @DisplayName("새로운 지하철 역을 등록할 수 있다.")
    @Test
    void save() {
        final Station station = new Station("강남역");
        final Station savedStation = stationDao.save(station);

        assertThat(savedStation).isNotNull();
    }

    @Test
    @DisplayName("등록된 지하철 역들을 반환한다.")
    void findAll() {
        final Station station1 = new Station("강남역");
        final Station station2 = new Station("역삼역");

        stationDao.save(station1);
        stationDao.save(station2);

        final List<String> actual = stationDao.findAll().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        final List<String> expected = List.of("강남역", "역삼역", "선릉역");

        assertThat(actual).containsAll(expected);
    }

    @DisplayName("등록된 지하철을 삭제한다.")
    @Test
    void deleteById() {
        final Long id = savedStation.getId();

        stationDao.deleteById(id);

        assertThat(stationDao.findAll()).hasSize(0);
    }

    @DisplayName("등록하려는 이름으로 지하철 역이 존재하는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"선릉역,true", "신림역,false"})
    void existByName(final String name, final boolean actual) {
        final boolean expected = stationDao.existByName(name);

        assertThat(actual).isEqualTo(expected);
    }
}
