package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;
    private LineDao lineDao;

    private Section testSection1 = new Section(100L, 1L, 2L, 3L,10L);

    @Autowired
    private SectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SectionDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @BeforeEach
    public void setUp() {
        Line testLine1 = new Line(1L, "testName", "black", 10L);
        Line line = lineDao.save(testLine1);
    }

    @Test
    void save() {
//        Section section = sectionDao.save(testSection1);
    }

    @Test
    void findAllByLineId() {
    }
}
