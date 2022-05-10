package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {

    private static final Section SECTION_A = new Section(1L, 1L, 2L, 1);
    private static final Section SECTION_B = new Section(2L, 1L, 3L, 2);

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
        final Section created = sectionDao.save(SECTION_A);

        Assertions.assertThat(created.getId()).isNotNull();

        sectionDao.deleteById(created.getId());
    }

    @DisplayName("개별 구간을 조회한다.")
    @Test
    void findById() {

        final Section expected = sectionDao.save(SECTION_A);

        final Section actual = sectionDao.findById(expected.getId()).orElseThrow();

        Assertions.assertThat(actual).isEqualTo(expected);

        sectionDao.deleteById(expected.getId());
    }

    @DisplayName("특정 노선의 모든 구간을 조회한다.")
    @Test
    void findAllByLineId() {
        final Long lineId = 1L;
        final Section createdA = sectionDao.save(SECTION_A);
        final Section createdB = sectionDao.save(SECTION_B);

        final List<Section> sections = sectionDao.findAllByLineId(lineId);

        Assertions.assertThat(sections).isNotEmpty();

        sectionDao.deleteById(createdA.getId());
        sectionDao.deleteById(createdB.getId());
    }
}
