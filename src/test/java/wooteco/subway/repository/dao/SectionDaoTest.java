package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.entity.section.SectionEntity;

@DisplayName("지하철구간 DB")
@JdbcTest
class SectionDaoTest {

    private static final Long SECTION_ID = 1L;
    private static final Long LINE_ID = 1L;
    private static final Long UP_STATION_ID = 1L;
    private static final Long DOWN_STATION_ID = 2L;
    private static final int DISTANCE = 10;
    private static final SectionEntity SECTION_ENTITY = new SectionEntity(
            SECTION_ID, LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(dataSource);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        Long actual = sectionDao.save(SECTION_ENTITY);
        assertThat(actual).isGreaterThan(0L);
    }

    @DisplayName("노선별 구간 목록을 조회한다.")
    @ParameterizedTest
    @CsvSource(value = {"5,2", "5,4"})
    void findAllByLineId(int totalSize, int expected) {
        LongStream.rangeClosed(1, expected)
                .mapToObj(id -> new SectionEntity(id, LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE))
                .forEach(sectionDao::save);

        LongStream.rangeClosed(expected, totalSize)
                .mapToObj(id -> new SectionEntity(id, LINE_ID + 1, UP_STATION_ID, DOWN_STATION_ID, DISTANCE))
                .forEach(sectionDao::save);

        List<SectionEntity> actual = sectionDao.findAllByLineId(LINE_ID);
        assertThat(actual).hasSize(expected);
    }

    @DisplayName("노선별 구간 식별자 목록을 조회한다.")
    @Test
    void findAllIdByLineId() {
        List<Long> expected = LongStream.rangeClosed(1, 5)
                .mapToObj(id -> new SectionEntity(id, LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE))
                .map(sectionDao::save)
                .collect(Collectors.toUnmodifiableList());

        List<Long> actual = sectionDao.findAllIdByLineId(LINE_ID);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("해당 식별자의 노선이 존재하는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"0,true", "1,false"})
    void existsById(Long difference, boolean expected) {
        Long sectionId = sectionDao.save(SECTION_ENTITY);

        boolean actual = sectionDao.existsById(sectionId + difference);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("해당 역의 식별자를 지닌 노선이 존재하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForExistsByStationId")
    void existsByStationId(Long stationId, boolean expected) {
        sectionDao.save(SECTION_ENTITY);

        boolean actual = sectionDao.existsByStationId(stationId);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForExistsByStationId() {
        return Stream.of(
                Arguments.of(UP_STATION_ID, true),
                Arguments.of(DOWN_STATION_ID, true),
                Arguments.of(999L, false));
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void remove() {
        Long lineId = 1L;
        Long sectionId = sectionDao.save(new SectionEntity(0L, lineId, 1L, 2L, 10));
        sectionDao.remove(sectionId);
        assertThat(sectionDao.findAllByLineId(lineId)).isEmpty();
    }
}
