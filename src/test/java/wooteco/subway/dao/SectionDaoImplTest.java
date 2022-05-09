package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

@JdbcTest
public class SectionDaoImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @Test
    void save() {
        // given
        LineRequest lineRequest = new LineRequest("", "", 1L, 2L, 10);
        Section section = Section.from(1L, lineRequest);

        // when
        Long savedId = sectionDao.save(section);

        // then
        assertThat(savedId).isPositive();
    }
}
