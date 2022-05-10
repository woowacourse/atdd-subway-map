package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {

    private static final Section LINE_1_SECTION_A = new Section(1L, 1L, 2L, 1);
    private static final Section LINE_1_SECTION_B = new Section(1L, 1L, 3L, 2);

    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(dataSource);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        final Section created = sectionDao.save(LINE_1_SECTION_A);

        assertThat(created.getId()).isNotNull();

        sectionDao.deleteById(created.getId());
    }

    @DisplayName("개별 구간을 조회한다.")
    @Test
    void findById() {

        final Section expected = sectionDao.save(LINE_1_SECTION_A);

        final Section actual = sectionDao.findById(expected.getId()).orElseThrow();

        assertThat(actual).isEqualTo(expected);

        sectionDao.deleteById(expected.getId());
    }

    @DisplayName("특정 노선의 모든 구간을 조회한다.")
    @Test
    void findAllByLineId() {
        final Long lineId = 1L;
        final Section createdA = sectionDao.save(LINE_1_SECTION_A);
        final Section createdB = sectionDao.save(LINE_1_SECTION_B);

        final List<Section> sections = sectionDao.findAllByLineId(lineId);

        assertThat(sections).isNotEmpty();

        sectionDao.deleteById(createdA.getId());
        sectionDao.deleteById(createdB.getId());
    }

    @DisplayName("특정 노선의 모든 구간을 제거한다.")
    @Test
    void deleteAllByLineId() {
        final Long lineId = 1L;
        final Section createdA = sectionDao.save(LINE_1_SECTION_A);
        final Section createdB = sectionDao.save(LINE_1_SECTION_B);

        sectionDao.deleteAllByLineId(lineId);

        assertThat(sectionDao.findAllByLineId(lineId)).isEmpty();
        sectionDao.deleteById(createdA.getId());
        sectionDao.deleteById(createdB.getId());
    }
}
