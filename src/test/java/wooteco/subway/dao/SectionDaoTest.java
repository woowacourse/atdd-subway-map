package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
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
        Section section = new Section(1L, 1L, 1L, 2L, 10);
        Section savedSection = sectionDao.save(section);

        assertAll(
            () -> assertThat(section.getId()).isEqualTo(1L),
            () -> assertThat(section.getLineId()).isEqualTo(1L),
            () -> assertThat(section.getUpStationId()).isEqualTo(1L),
            () -> assertThat(section.getDownStationId()).isEqualTo(2L),
            () -> assertThat(section.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("노선 id를 이용해 구간 목록을 조회한다.")
    @Test
    void findByLineId() {
        Section firstSection = new Section(1L, 1L, 2L, 10);
        Section secondSection = new Section(1L, 2L, 3L, 5);
        sectionDao.save(firstSection);
        sectionDao.save(secondSection);

        long lineId = 1;
        Sections sections = sectionDao.findByLineId(lineId);

        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        Section firstSection = new Section(1L, 1L, 2L, 10);
        Section secondSection = new Section(1L, 2L, 3L, 5);
        sectionDao.save(firstSection);
        Section savedLine = sectionDao.save(secondSection);

        sectionDao.delete(savedLine.getId());
        Sections result = sectionDao.findByLineId(1L);

        assertThat(result.getSections().size()).isEqualTo(1);
    }
}
