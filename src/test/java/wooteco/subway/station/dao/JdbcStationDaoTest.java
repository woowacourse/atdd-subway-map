package wooteco.subway.station.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import wooteco.subway.station.Station;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 역 jdbc 테스트")
@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-test.yml")
class JdbcStationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcStationDao jdbcStationDao;

    @BeforeEach
    void setUp() {
        this.jdbcStationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("Station 객체를 받아 DB에 저장")
    @Test
    void save() {
        // given
        Station 강남역 = new Station("강남역");

        // when
        Station station = jdbcStationDao.save(강남역);

        // then
        assertThat(station).usingRecursiveComparison()
                .isEqualTo(new Station(1L, "강남역"));
    }

    @DisplayName("지하철 역 이름으로 DB에서 지하철 역 조회")
    @Test
    void findByName() {
        // given
        String 강남역 = "강남역";
        jdbcStationDao.save(new Station(강남역));

        // when
        Optional<Station> station = jdbcStationDao.findByName(강남역);

        // then
        assertThat(station.get()).usingRecursiveComparison()
                .isEqualTo(new Station(1L, 강남역));
    }

    @DisplayName("모든 지하철 역 조회")
    @Test
    void findAll() {
        // given
        jdbcStationDao.save(new Station("강남역"));
        jdbcStationDao.save(new Station("왕십리역"));
        jdbcStationDao.save(new Station("잠실역"));

        // when
        List<Station> stations = jdbcStationDao.findAll();

        // then
        assertThat(stations).hasSize(3);
        assertThat(stations).usingRecursiveFieldByFieldElementComparator()
                .containsAll(Arrays.asList(
                        new Station(1L, "강남역"),
                        new Station(2L, "왕십리역"),
                        new Station(3L, "잠실역")
                ));
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void delete() {
        // given
        jdbcStationDao.save(new Station("강남역"));
        int originalSize = jdbcStationDao.findAll().size();

        // when
        jdbcStationDao.delete(1L);

        // then
        assertThat(jdbcStationDao.findAll().size()).isEqualTo(originalSize - 1);
    }
}