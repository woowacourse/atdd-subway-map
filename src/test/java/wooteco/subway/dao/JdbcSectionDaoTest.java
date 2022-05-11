package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

@JdbcTest
@Sql("/sectionInitSchema.sql")
class JdbcSectionDaoTest {

    public static final Section GIVEN_SECTION = new Section(null, 1L, 1L, 2L, 6, 1L);

    public static final Section GIVEN_SECTION2 = new Section(null, 1L, 2L, 3L, 2, 2L);

    private final SectionDao sectionDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        sectionDao = new JdbcSectionDao(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("Section을 저장한다.")
    void saveSection() {
        Long id = sectionDao.save(GIVEN_SECTION);

        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("입력으로 들어온 값보다 lineOrder 값이 같거나 큰 구간들의 lineOrder 값을 1 증가시킨다.")
    void updateLineOrderById() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        sectionDao.updateLineOrderByInc(1L, 1L);

        // then
        assertThat(sectionDao.findAllByLineId(1L))
            .extracting("lineOrder")
            .containsExactly(2L);
    }

    @Test
    @DisplayName("입력으로 들어온 값보다 lineOrder 값이 큰 구간들의 lineOrder 값을 1 감소시킨다.")
    void notUpdateLineOrderById() {
        // given
        sectionDao.save(GIVEN_SECTION);
        sectionDao.save(GIVEN_SECTION2);

        // when
        sectionDao.updateLineOrderByDec(1L, 1L);

        // then
        assertThat(sectionDao.findAllByLineId(1L))
            .extracting("lineOrder")
            .containsExactly(1L, 1L);
    }

    @Test
    @DisplayName("입력받은 lineId가 SECTION에 존재하는 지 확인한다")
    void hasLineId() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        boolean result = sectionDao.existByLineId(GIVEN_SECTION.getLineId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("입력받은 lineId가 SECTION에 존재하지 않는 것을 확인한다")
    void hasNotLineId() {
        // given

        // when
        boolean result = sectionDao.existByLineId(2L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("입력받은 lineId의 모든 구간 정보를 조회한다")
    void findAll() {
        // given
        sectionDao.save(GIVEN_SECTION);
        sectionDao.save(GIVEN_SECTION2);

        // when
        List<Section> sections = sectionDao.findAllByLineId(1L);

        // then
        assertThat(sections).hasSize(2)
            .extracting("lineId", "upStationId", "downStationId", "distance", "lineOrder")
            .containsExactly(tuple(1L, 1L, 2L, 6, 1L), tuple(1L, 2L, 3L, 2, 2L));
    }

    @Test
    @DisplayName("stationId가 포함된 section들을 찾는다.")
    void findByLineIdAndStationId() {
        // given

        sectionDao.save(GIVEN_SECTION);
        sectionDao.save(GIVEN_SECTION2);

        // then
        List<Section> sections = sectionDao.findByLineIdAndStationId(1L, 2L);

        // when
        assertThat(sections).hasSize(2)
            .extracting("lineId", "upStationId", "downStationId", "distance", "lineOrder")
            .containsExactly(tuple(1L, 1L, 2L, 6, 1L), tuple(1L, 2L, 3L, 2, 2L));
    }

    @Test
    @DisplayName("id를 이용하여 section을 삭제한다.")
    void deleteById() {
        // given
        sectionDao.save(GIVEN_SECTION);
        sectionDao.save(GIVEN_SECTION2);

        // then
        sectionDao.deleteById(2L);

        // when
        assertThat(sectionDao.findAllByLineId(1L)).hasSize(1)
            .extracting("lineId", "upStationId", "downStationId", "distance", "lineOrder")
            .containsExactly(tuple(1L, 1L, 2L, 6, 1L));
    }
}
