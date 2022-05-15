package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Sql("/sql/schema-test.sql")
@JdbcTest
public class SectionDaoTest {

    private static final Long LINE_ID = 1L;

    private static final Section FIRST_SECTION = new Section(LINE_ID, 1L, 2L, 10);
    private static final Section SECOND_SECTION = new Section(LINE_ID, 2L, 3L, 5);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @BeforeEach
    void beforeEach() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("새 구간을 저장한다.")
    @Test
    void save() {
        Section savedSection = sectionDao.save(FIRST_SECTION);

        assertAll(
            () -> assertThat(savedSection.getId()).isEqualTo(1L),
            () -> assertThat(savedSection.getLineId()).isEqualTo(1L),
            () -> assertThat(savedSection.getUpStationId()).isEqualTo(1L),
            () -> assertThat(savedSection.getDownStationId()).isEqualTo(2L),
            () -> assertThat(savedSection.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("노선 id를 이용해 구간 목록을 조회한다.")
    @Test
    void findByLineId() {
        sectionDao.save(FIRST_SECTION);
        sectionDao.save(SECOND_SECTION);

        Sections sections = sectionDao.findByLineId(LINE_ID);

        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        sectionDao.save(FIRST_SECTION);
        Section savedLine = sectionDao.save(SECOND_SECTION);

        sectionDao.delete(savedLine.getId());
        Sections result = sectionDao.findByLineId(LINE_ID);

        assertThat(result.getSections().size()).isEqualTo(1);
    }
}
