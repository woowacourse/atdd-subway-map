package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

@JdbcTest
@Sql({"/schema.sql", "/test-data.sql"})
public class SectionDaoTest {

    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(JdbcTemplate jdbcTemplate) {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    void findByLineId() {
        //given
        //when
        final List<Section> sections = sectionDao.findByLineId(1L);
        //then
        final Section section = sections.get(0);

        assertAll(
                () -> assertThat(section.getUpStation().getName()).isEqualTo("삼전역"),
                () -> assertThat(section.getDownStation().getName()).isEqualTo("잠실역"),
                () -> assertThat(sections.size()).isEqualTo(1)
        );
    }
}
