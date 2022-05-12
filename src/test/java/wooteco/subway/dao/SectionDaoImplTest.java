package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
        lineDao = new LineDaoImpl(jdbcTemplate);
    }

    @Test
    void save() {
        // given
        Line line = new Line("1호선", "bg-red-600");
        Long savedLineId = lineDao.save(line);
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);

        // when
        Long savedSectionId = sectionDao.save(section, savedLineId);

        // then
        assertThat(savedSectionId).isPositive();
    }
}
