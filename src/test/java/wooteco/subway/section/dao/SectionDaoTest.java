package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.section.model.Section;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    void name() {
        //given
        LineRequest request = new LineRequest("2호선", "green", 1L, 2L, 10);
        LineRequest request2 = new LineRequest("2호선", "green", 2L, 3L, 10);
        long lineId = 1L;
        sectionDao.save(lineId, request);
        sectionDao.save(lineId, request2);
        //when
        List<Section> sections = sectionDao.findSectionsByLineId(lineId);
        //then
        assertThat(sections).hasSize(2);
    }
}