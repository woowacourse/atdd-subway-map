package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

@JdbcTest
@Sql("classpath:sectionDao.sql")
class SectionDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("노선에 해당하는 Section들을 반환한다.")
    void findByLineId() {
        Long lineId = 1L;
        Section firstSection = new Section(lineId, 1L, 2L, 10);
        Section secondSection = new Section(lineId, 2L, 3L, 8);
        sectionDao.save(firstSection);
        sectionDao.save(secondSection);

        final List<Section> sections = sectionDao.findByLineId(lineId);

        assertThat(sections).contains(firstSection, secondSection);
    }
}
