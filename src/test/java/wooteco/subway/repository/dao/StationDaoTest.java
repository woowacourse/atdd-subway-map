package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.entity.station.StationEntity;

@DisplayName("지하철역 DB")
@JdbcTest
class StationDaoTest {

    private static final StationEntity STATION_ENTITY = new StationEntity(1L, "강남역");

    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new StationDao(dataSource);
    }

    @DisplayName("역을 저장한다.")
    @Test
    void save() {
        Long actual = stationDao.save(new StationEntity(0L, "강남역"));
        assertThat(actual).isGreaterThan(0);
    }

    @DisplayName("역 목록을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {3})
    void findAll(int expected) {
        LongStream.rangeClosed(1, expected)
                .mapToObj(id -> new StationEntity(id, "역" + id))
                .forEach(stationDao::save);

        assertThat(stationDao.findAll()).hasSize(expected);
    }

    @DisplayName("역을 조회한다.")
    @Test
    void findById() {
        StationEntity expected = STATION_ENTITY;
        Optional<StationEntity> actual = stationDao.findById(stationDao.save(expected));

        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 역을 조회한다.")
    @Test
    void findWithNonexistentId() {
        Optional<StationEntity> actual = stationDao.findById(1L);
        assertThat(actual).isEmpty();
    }

    @DisplayName("해당 식별자의 지하철역이 존재하는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"0,true", "1,false"})
    void existsById(Long difference, boolean expected) {
        Long stationId = stationDao.save(STATION_ENTITY);

        boolean actual = stationDao.existsById(stationId + difference);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("해당 이름의 역이 존재하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForExistsByName")
    void existsByName(String stationName, boolean expected) {
        stationDao.save(STATION_ENTITY);

        boolean actual = stationDao.existsByName(stationName);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForExistsByName() {
        return Stream.of(
                Arguments.of(STATION_ENTITY.getName(), true),
                Arguments.of(STATION_ENTITY.getName() + "temp", false));
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void remove() {
        Stream.of(STATION_ENTITY)
                .map(stationDao::save)
                .forEach(stationDao::remove);

        assertThat(stationDao.findAll()).isEmpty();
    }
}
